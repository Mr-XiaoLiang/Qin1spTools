/*
 * Copyright 2015-2016 Nickolay Savchenko
 * Copyright 2017-2018 Nikita Shakarun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.microedition.shell;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Objects;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.ViewHandler;
import javax.microedition.lcdui.event.KeyEventPostHelper;
import javax.microedition.lcdui.event.SimpleEvent;
import javax.microedition.lcdui.keyboard.KeyMapper;
import javax.microedition.lcdui.overlay.OverlayView;
import javax.microedition.util.ContextHolder;
import javax.microedition.util.DisplayHost;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.playsoftware.j2meloader.R;
import ru.playsoftware.j2meloader.config.Config;
import ru.playsoftware.j2meloader.util.LogUtils;

import static ru.playsoftware.j2meloader.util.Constants.KEY_MIDLET_NAME;
import static ru.playsoftware.j2meloader.util.Constants.PREF_KEEP_SCREEN;
import static ru.playsoftware.j2meloader.util.Constants.PREF_STATUSBAR;
import static ru.playsoftware.j2meloader.util.Constants.PREF_TOOLBAR;
import static ru.playsoftware.j2meloader.util.Constants.PREF_VIBRATION;

public class MicroActivity extends AppCompatActivity implements DisplayHost {
	private static final int ORIENTATION_DEFAULT = 0;
	private static final int ORIENTATION_AUTO = 1;
	private static final int ORIENTATION_PORTRAIT = 2;
	private static final int ORIENTATION_LANDSCAPE = 3;

	private Displayable current;
	private boolean visible;
	private boolean actionBarEnabled;
	private boolean statusBarEnabled;
	private boolean keyLongPressed;
	private FrameLayout layout;
	private Toolbar toolbar;
	private MicroLoader microLoader;
	private String appName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		super.onCreate(savedInstanceState);
		ContextHolder.setCurrentHost(this);
		setContentView(R.layout.activity_micro);
		layout = findViewById(R.id.displayable_container);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBarEnabled = sp.getBoolean(PREF_TOOLBAR, false);
		statusBarEnabled = sp.getBoolean(PREF_STATUSBAR, false);
		if (sp.getBoolean(PREF_KEEP_SCREEN, false)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		ContextHolder.setVibration(sp.getBoolean(PREF_VIBRATION, true));
		Intent intent = getIntent();
		appName = intent.getStringExtra(KEY_MIDLET_NAME);
		Uri data = intent.getData();
		if (data == null) {
			showErrorDialog("Invalid intent: app path is null");
			return;
		}
		String appPath = data.toString();
		microLoader = new MicroLoader(this, appPath);
		if (!microLoader.init()) {
			finish();
			return;
		}
		microLoader.applyConfiguration();
		setOrientation(microLoader.getOrientation());
		try {
			loadMIDlet();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e.toString());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		visible = true;
		MidletThread.resumeApp();
	}

	@Override
	public void onPause() {
		visible = false;
		MidletThread.pauseApp();
		super.onPause();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && current instanceof Canvas) {
			hideSystemUI();
		}
	}

	@SuppressLint("SourceLockedOrientationActivity")
	private void setOrientation(int orientation) {
		switch (orientation) {
			case ORIENTATION_AUTO:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
				break;
			case ORIENTATION_PORTRAIT:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
				break;
			case ORIENTATION_LANDSCAPE:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
				break;
			case ORIENTATION_DEFAULT:
			default:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
				break;
		}
	}

	private void loadMIDlet() throws Exception {
		LinkedHashMap<String, String> midlets = microLoader.loadMIDletList();
		int size = midlets.size();
		String[] midletsNameArray = midlets.values().toArray(new String[0]);
		String[] midletsClassArray = midlets.keySet().toArray(new String[0]);
		if (size == 0) {
			throw new Exception("No MIDlets found");
		} else if (size == 1) {
			MidletThread.create(microLoader, midletsClassArray[0]);
		} else {
			showMidletDialog(midletsNameArray, midletsClassArray);
		}
	}

	private void showMidletDialog(String[] names, final String[] classes) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle(R.string.select_dialog_title)
				.setItems(names, (d, n) -> MidletThread.create(microLoader, classes[n]))
				.setOnCancelListener(d -> {
					d.dismiss();
					MidletThread.notifyDestroyed();
				});
		builder.show();
	}

	void showErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.error)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, (d, w) -> MidletThread.notifyDestroyed());
		builder.setOnCancelListener(dialogInterface -> MidletThread.notifyDestroyed());
		builder.show();
	}

	private final SimpleEvent msgSetCurrent = new SimpleEvent() {
		@Override
		public void process() {
			current.clearDisplayableView();
			layout.removeAllViews();
			layout.addView(current.getDisplayableView());
			invalidateOptionsMenu();
			ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
			if (current instanceof Canvas) {
				hideSystemUI();
				if (!actionBarEnabled) {
					actionBar.hide();
				} else {
					final String title = current.getTitle();
					actionBar.setTitle(title == null ? appName : title);
					layoutParams.height = (int) (getToolBarHeight() / 1.5);
				}
			} else {
				showSystemUI();
				actionBar.show();
				final String title = current.getTitle();
				actionBar.setTitle(title == null ? appName : title);
				layoutParams.height = getToolBarHeight();
			}
			toolbar.setLayoutParams(layoutParams);
		}
	};

	private int getToolBarHeight() {
		int[] attrs = new int[]{R.attr.actionBarSize};
		TypedArray ta = obtainStyledAttributes(attrs);
		int toolBarHeight = ta.getDimensionPixelSize(0, -1);
		ta.recycle();
		return toolBarHeight;
	}

	private void hideSystemUI() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			if (!statusBarEnabled) {
				flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
			}
			getWindow().getDecorView().setSystemUiVisibility(flags);
		} else if (!statusBarEnabled) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	private void showSystemUI() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	public void setCurrent(Displayable displayable) {
		current = displayable;
		ViewHandler.postEvent(msgSetCurrent);
	}

	@Override
	public ViewGroup getRootView() {
		return findViewById(R.id.midletFrame);
	}

	@Override
	public OverlayView getOverlayView() {
		return findViewById(R.id.vOverlay);
	}

	public Displayable getCurrent() {
		return current;
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	private void showExitConfirmation() {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(R.string.CONFIRMATION_REQUIRED)
				.setMessage(R.string.FORCE_CLOSE_CONFIRMATION)
				.setPositiveButton(android.R.string.ok, (d, w) -> MidletThread.destroyApp())
				.setNeutralButton(R.string.action_settings, (d, w) -> {
					Intent intent = getIntent();
					Config.startApp(this, intent.getStringExtra(KEY_MIDLET_NAME),
							intent.getDataString());
					MidletThread.destroyApp();
				})
				.setNegativeButton(android.R.string.cancel, null);
		alertBuilder.create().show();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
			onKeyUp(event.getKeyCode(), event);
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void openOptionsMenu() {
		if (!actionBarEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && current instanceof Canvas) {
			showSystemUI();
		}
		super.openOptionsMenu();
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showExitConfirmation();
			keyLongPressed = true;
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) && !keyLongPressed) {
			openOptionsMenu();
			return true;
		}
		keyLongPressed = false;
		Displayable current = getCurrent();
		if (current instanceof Canvas) {
			keyCode = KeyMapper.convertAndroidKeyCode(keyCode);
			if (keyCode == Integer.MAX_VALUE) {
				return super.onKeyUp(keyCode, event);
			}
			return KeyEventPostHelper.postKeyReleased(current, keyCode) || super.onKeyUp(keyCode, event);
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Displayable current = getCurrent();
		if (current instanceof Canvas) {
			keyCode = KeyMapper.convertAndroidKeyCode(keyCode);
			if (keyCode == Integer.MAX_VALUE) {
				return super.onKeyDown(keyCode, event);
			}
			boolean eventResult;
			if (event.getRepeatCount() == 0) {
				eventResult = KeyEventPostHelper.postKeyPressed(current, keyCode);
			} else {
				eventResult = KeyEventPostHelper.postKeyRepeated(current, keyCode);
			}
			return eventResult || super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		if (current == null) {
			inflater.inflate(R.menu.midlet_displayable, menu);
			return true;
		}
		boolean hasCommands = current.countCommands() > 0;
		Menu group;
		if (hasCommands) {
			inflater.inflate(R.menu.midlet_common, menu);
			group = menu.getItem(0).getSubMenu();
		} else {
			group = menu;
		}
		inflater.inflate(R.menu.midlet_displayable, group);
		if (current instanceof Canvas) {
			if (actionBarEnabled) {
				inflater.inflate(R.menu.midlet_canvas, menu);
			} else {
				inflater.inflate(R.menu.midlet_canvas_no_bar, group);
			}
		}
		if (!hasCommands) {
			return true;
		}
		for (Command cmd : current.getCommands()) {
			menu.add(Menu.NONE, cmd.hashCode(), Menu.NONE, cmd.getAndroidLabel());
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (item.getGroupId() == R.id.action_group_common_settings) {
			if (id == R.id.action_exit_midlet) {
				showExitConfirmation();
			} else if (id == R.id.action_take_screenshot) {
				takeScreenshot();
			} else if (id == R.id.action_save_log) {
				saveLog();
			} else if (id == R.id.action_limit_fps){
				showLimitFpsDialog();
			}
			return true;
		}
		if (current != null) {
			return current.menuItemSelected(id);
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("CheckResult")
	private void takeScreenshot() {
		microLoader.takeScreenshot((Canvas) current)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new SingleObserver<String>() {
					@Override
					public void onSubscribe(@NonNull Disposable d) {
					}

					@Override
					public void onSuccess(@NonNull String s) {
						Toast.makeText(MicroActivity.this, getString(R.string.screenshot_saved)
								+ " " + s, Toast.LENGTH_LONG).show();
					}

					@Override
					public void onError(@NonNull Throwable e) {
						e.printStackTrace();
						Toast.makeText(MicroActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void saveLog() {
		try {
			LogUtils.writeLog();
			Toast.makeText(this, R.string.log_saved, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
		}
	}

	private void showLimitFpsDialog(){
		EditText editText = new EditText(this);
		editText.setHint(R.string.unlimited);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
		editText.setMaxLines(1);
		editText.setSingleLine(true);
		float density = getResources().getDisplayMetrics().density;
		LinearLayout linearLayout = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		int margin = (int) (density * 20);
		params.setMargins(margin, 0, margin, 0);
		linearLayout.addView(editText, params);
		int paddingVertical = (int) (density * 16);
		int paddingHorizontal = (int) (density * 8);
		editText.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
		new AlertDialog.Builder(this)
				.setTitle(R.string.PREF_LIMIT_FPS)
				.setView(linearLayout)
				.setPositiveButton(android.R.string.ok, (d, w) -> {
					Editable text = editText.getText();
					int fps = 0;
					try {
						fps = TextUtils.isEmpty(text) ? 0 : Integer.parseInt(text.toString().trim());
					} catch (NumberFormatException ignored) {
					}
					microLoader.setLimitFps(fps);
				})
				.setNegativeButton(android.R.string.cancel, null)
				.setNeutralButton(R.string.reset, ((d, which) -> microLoader.setLimitFps(-1)))
				.show();
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		if (current instanceof Form) {
			((Form) current).contextMenuItemSelected(item);
		} else if (current instanceof List) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			((List) current).contextMenuItemSelected(item, info.position);
		}

		return super.onContextItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ContextHolder.notifyOnActivityResult(requestCode, resultCode, data);
	}
}

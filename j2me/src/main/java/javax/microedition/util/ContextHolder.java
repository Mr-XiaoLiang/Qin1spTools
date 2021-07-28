/*
 * Copyright 2012 Kulikov Dmitriy
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

package javax.microedition.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;

import javax.microedition.shell.AppClassLoader;

public class ContextHolder {

    private static Display display = null;
    private static WeakReference<DisplayHost> currentHost = null;
    private static Vibrator vibrator = null;
    private static Context appContext = null;
    private static final ArrayList<ActivityResultListener> resultListeners = new ArrayList<>();
    private static boolean vibrationEnabled = false;

    public static Context getAppContext() {
        return appContext;
    }

    private static Display getDisplay() {
        if (display == null) {
            display = ((WindowManager) Objects.requireNonNull(
                    getAppContext().getSystemService(Context.WINDOW_SERVICE))
            ).getDefaultDisplay();
        }
        return display;
    }

    public static int getDisplayWidth() {
        return getDisplay().getWidth();
    }

	public static int getDisplayHeight() {
        return getDisplay().getHeight();
	}

    public static void setCurrentHost(DisplayHost host) {
        currentHost = new WeakReference<>(host);
    }

    public static void addActivityResultListener(ActivityResultListener listener) {
        if (!resultListeners.contains(listener)) {
            resultListeners.add(listener);
        }
    }

    public static void removeActivityResultListener(ActivityResultListener listener) {
        resultListeners.remove(listener);
    }

    public static void notifyOnActivityResult(int requestCode, int resultCode, Intent data) {
        for (ActivityResultListener listener : resultListeners) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static InputStream getResourceAsStream(Class<?> resClass, String resName) {
        return AppClassLoader.getResourceAsStream(resClass, resName);
    }

    public static FileOutputStream openFileOutput(String name) throws FileNotFoundException {
        File dir = new File(AppClassLoader.getDataDir());
        File file = new File(dir, name);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new FileNotFoundException("Can't create directory: " + dir);
        }
        return new FileOutputStream(file);
    }

    public static FileInputStream openFileInput(String name) throws FileNotFoundException {
        return new FileInputStream(getFileByName(name));
    }

    public static boolean deleteFile(String name) {
        return getFileByName(name).delete();
    }

    public static File getFileByName(String name) {
        return new File(AppClassLoader.getDataDir(), name);
    }

    public static File getCacheDir() {
        return getAppContext().getExternalCacheDir();
    }

    public static boolean requestPermission(String permission) {
        if (currentHost == null) {
            return false;
        }
        DisplayHost displayHost = currentHost.get();
        if (displayHost == null) {
            return false;
        }
        Activity activity = displayHost.getActivity();
        if (activity == null) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
            return false;
        } else {
            return true;
        }
    }

    public static String getAssetAsString(String fileName) {
        StringBuilder sb = new StringBuilder();

        try (InputStream is = getAppContext().getAssets().open(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static Activity getActivity() {
        if (currentHost == null) {
            return null;
        }
        DisplayHost displayHost = currentHost.get();
        if (displayHost == null) {
            return null;
        }
        return displayHost.getActivity();
    }

    public static DisplayHost getDisplayHost() {
        if (currentHost == null) {
            return null;
        }
        return currentHost.get();
    }

    public static boolean vibrate(int duration) {
        if (!vibrationEnabled) {
            return false;
        }
        if (vibrator == null) {
            vibrator = (Vibrator) getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibrator == null || !vibrator.hasVibrator()) {
            return false;
        }
        if (duration > 0) {
            vibrator.vibrate(duration);
        } else if (duration < 0) {
            throw new IllegalStateException();
        } else {
            vibrator.cancel();
        }
        return true;
    }

    public static void vibrateKey(int duration) {
        if (vibrator == null) {
            vibrator = (Vibrator) getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }
        vibrator.vibrate(duration);
    }

    public static void setApplication(Application application) {
        appContext = application;
    }

    public static void setVibration(boolean vibrationEnabled) {
        ContextHolder.vibrationEnabled = vibrationEnabled;
    }
}

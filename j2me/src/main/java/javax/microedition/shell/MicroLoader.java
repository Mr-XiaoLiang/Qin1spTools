/*
 * Copyright 2018 Nikita Shakarun
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

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.event.EventQueue;
import javax.microedition.lcdui.keyboard.KeyMapper;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.midlet.MIDlet;
import javax.microedition.util.ContextHolder;

import dalvik.system.DexClassLoader;
import io.reactivex.Single;
import ru.playsoftware.j2meloader.config.Config;
import ru.playsoftware.j2meloader.config.ProfileModel;
import ru.playsoftware.j2meloader.config.ProfilesManager;
import ru.playsoftware.j2meloader.config.ShaderInfo;
import ru.playsoftware.j2meloader.util.FileUtils;

public class MicroLoader {
    private static final String TAG = MicroLoader.class.getName();
    private static final int MIN_SCREEN_SIZE = 720 * 1280;

    private final File appDir;
    private final Context context;
    private final String workDir;
    private final String appDirName;
    private ProfileModel params;

    public MicroLoader(Context context, String appPath) {
        this.context = context;
        this.appDir = new File(appPath);
        File parentFile = appDir.getParentFile();
        if (parentFile == null) {
            throw new RuntimeException("appPath has error");
        }
        workDir = parentFile.getParent();
        appDirName = appDir.getName();
    }

    public boolean init() {
        File config = new File(workDir + Config.MIDLET_CONFIGS_DIR + appDirName);
        this.params = ProfilesManager.loadConfig(config);
        if (params == null) {
            this.params = new ProfileModel(config);
            ProfilesManager.saveConfig(this.params);
        }
        Display.initDisplay();
        Graphics3D.initGraphics3D();
        File cacheDir = ContextHolder.getCacheDir();
        // Some phones return null here
        if (cacheDir != null && cacheDir.exists()) {
            FileUtils.clearDirectory(cacheDir);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitNetwork()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);
        return true;
    }

    public LinkedHashMap<String, String> loadMIDletList() {
        LinkedHashMap<String, String> midletMap = new LinkedHashMap<>();
        LinkedHashMap<String, String> params =
                FileUtils.loadManifest(new File(appDir, Config.MIDLET_MANIFEST_FILE));
        MIDlet.initProps(params);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().matches("MIDlet-[0-9]+")) {
                String tmp = entry.getValue();
                String clazz = tmp.substring(tmp.lastIndexOf(',') + 1).trim();
                String title = tmp.substring(0, tmp.indexOf(',')).trim();
                midletMap.put(clazz, title);
            }
        }
        return midletMap;
    }

    public MIDlet loadMIDlet(String mainClass) throws Exception {
        File dexSource = new File(appDir, Config.MIDLET_DEX_FILE);
        File codeCacheDir = SDK_INT >= N ? context.getDataDir() : context.getFilesDir();
        File dexOptDir = new File(codeCacheDir, Config.DEX_OPT_CACHE_DIR);
        if (dexOptDir.exists()) {
            FileUtils.clearDirectory(dexOptDir);
        } else if (!dexOptDir.mkdirs()) {
            throw new IOException("Cant't create directory: [" + dexOptDir + ']');
        }
        DexClassLoader loader = new AppClassLoader(
                dexSource.getAbsolutePath(),
                dexOptDir.getAbsolutePath(),
                context.getClassLoader(),
                appDir
        );
        Log.i(TAG, "loadMIDletList main: " + mainClass + " from dex:" + dexSource.getPath());
        Log.i(TAG, "MIDlet-Name: " + appDirName);
        // noinspection unchecked
        Class<MIDlet> clazz = (Class<MIDlet>) loader.loadClass(mainClass);
        return clazz.newInstance();
    }

    private void setProperties() {
        final Locale defaultLocale = Locale.getDefault();
        final String country = defaultLocale.getCountry();
        System.setProperty("microedition.locale", defaultLocale.getLanguage()
                + (country.length() == 2 ? "-" + country : ""));
        // FIXME: 21.10.2020 Config.getDataDir() may be in different storage
        final String primaryStoragePath = Environment.getExternalStorageDirectory().getPath();
        String uri = "file:///c:" + Config.getDataDir().substring(primaryStoragePath.length()) + appDirName;
        System.setProperty("fileconn.dir.cache", uri + "/cache");
        System.setProperty("fileconn.dir.private", uri + "/private");
        System.setProperty("user.home", primaryStoragePath);
    }

    public int getOrientation() {
        return params.orientation;
    }

    public void setLimitFps(int fps) {
        if (fps == -1) Canvas.setLimitFps(params.fpsLimit);
        else Canvas.setLimitFps(fps);
    }

    public void applyConfiguration() {
        try {
            // Apply configuration to the launching MIDlet
            setProperties();

            final String[] propLines = params.systemProperties.split("\n");
            for (String line : propLines) {
                String[] prop = line.split(":[ ]*", 2);
                if (prop.length == 2) {
                    System.setProperty(prop[0], prop[1]);
                }
            }
            try {
                Charset.forName(System.getProperty("microedition.encoding"));
            } catch (Exception e) {
                System.setProperty("microedition.encoding", "ISO-8859-1");
            }

            int paramsWidth = params.screenWidth;
            int paramsHeight = params.screenHeight;
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            if (screenWidth * screenHeight > MIN_SCREEN_SIZE) {
                Displayable.setVirtualSize(paramsWidth, paramsHeight);
            } else {
                Displayable.setVirtualSize(screenWidth, screenHeight);
            }
            Canvas.setBackgroundColor(params.screenBackgroundColor);
            Canvas.setScale(params.screenGravity, params.screenScaleType, params.screenScaleRatio);
            Canvas.setFilterBitmap(params.screenFilter);
            EventQueue.setImmediate(params.immediateMode);
            Canvas.setGraphicsMode(params.graphicsMode, params.parallelRedrawScreen);
            ShaderInfo shader = params.shader;
            if (shader != null) {
                shader.dir = workDir + Config.SHADERS_DIR;
            }
            Canvas.setShaderFilter(shader);
            Canvas.setForceFullscreen(params.forceFullscreen);
            Canvas.setShowFps(params.showFps);
            Canvas.setLimitFps(params.fpsLimit);

            Font.applySettings(params);

            KeyMapper.setKeyMapping(params);
            Canvas.setHasTouchInput(params.touchInput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SimpleDateFormat")
    Single<String> takeScreenshot(Canvas canvas) {
        return Single.create(emitter -> {
            String path = takeScreenshotSync(canvas);
            emitter.onSuccess(path);
        });
    }

    public static String takeScreenshotSync(Canvas canvas) throws Exception {
        Bitmap bitmap = canvas.getScreenShot();
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
        String fileName = "Screenshot_" + simpleDateFormat.format(now) + ".png";
        File screenshotDir = new File(Config.SCREENSHOTS_DIR);
        File screenshotFile = new File(screenshotDir, fileName);
        if (!screenshotDir.exists() && !screenshotDir.mkdirs()) {
            throw new IOException("Can't create directory: " + screenshotDir);
        }
        FileOutputStream out = new FileOutputStream(screenshotFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        // 回收
        bitmap.recycle();
        return screenshotFile.getAbsolutePath();
    }
}

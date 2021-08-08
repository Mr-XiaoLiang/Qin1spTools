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

package ru.playsoftware.j2meloader.config;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.microedition.util.ContextHolder;

public class ProfilesManager {

	private static final String TAG = ProfilesManager.class.getName();
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Nullable
	public static ProfileModel loadConfig(File dir) {
		File file = new File(dir, Config.MIDLET_CONFIG_FILE);
		ProfileModel params = null;
		if (file.exists()) {
			try (FileReader reader = new FileReader(file)) {
				params = gson.fromJson(reader, ProfileModel.class);
				params.dir = dir;
			} catch (Exception e) {
				Log.e(TAG, "loadConfig: ", e);
			}
		}
		if (params == null) {
			return null;
		}
		switch (params.version) {
			case 0:
				if (params.hwAcceleration) {
					params.graphicsMode = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? 2 : 3;
				}
				updateSystemProperties(params);
			case 1:
				params.fontAA = true;

			case 2:
				if (params.screenScaleToFit) {
					if (params.screenKeepAspectRatio) {
						params.screenScaleType = 1;
					} else {
						params.screenScaleType = 2;
					}
				} else {
					params.screenScaleType = 0;
				}
				params.screenGravity = 1;

				params.version = ProfileModel.VERSION;
				ProfilesManager.saveConfig(params);
				break;
		}
		return params;
	}

	public static boolean saveConfig(ProfileModel p) {
		try {
			File file = new File(p.dir, Config.MIDLET_CONFIG_FILE);
			File parentFile = file.getParentFile();
			if (parentFile != null && !parentFile.exists()) {
				boolean mkdirsResult = parentFile.mkdirs();
				Log.e(TAG, "make config dir: " + mkdirsResult);
			}
			if (!file.exists()) {
				boolean newFile = file.createNewFile();
				Log.e(TAG, "make config file: " + newFile);
			}
			FileWriter writer = new FileWriter(file);
			gson.toJson(p, writer);
			writer.close();
			return true;
		} catch (Exception e) {
			Log.e(TAG, "saveConfig: ", e);
		}
		return false;
	}

	public static void updateSystemProperties(ProfileModel params) {
		String defaultProperties = ContextHolder.getAssetAsString("defaults/system.props");
		String properties = params.systemProperties;
		StringBuilder sb = new StringBuilder();
		if (properties == null) {
			params.systemProperties = defaultProperties;
			return;
		}
		sb.append(properties);
		String[] defaults = defaultProperties.split("[\\n\\r]+");
		for (String line : defaults) {
			if (properties.contains(line.substring(0, line.indexOf(':')))) continue;
			sb.append(line).append('\n');
		}
		params.systemProperties = sb.toString();
	}
}

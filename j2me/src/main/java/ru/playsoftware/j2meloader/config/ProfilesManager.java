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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.microedition.lcdui.keyboard.VirtualKeyboard;
import javax.microedition.util.ContextHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.playsoftware.j2meloader.util.FileUtils;
import ru.playsoftware.j2meloader.util.XmlUtils;

public class ProfilesManager {

	private static final String TAG = ProfilesManager.class.getName();
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	static ArrayList<Profile> getProfiles() {
		File root = new File(Config.getProfilesDir());
		return getList(root);
	}

	@NonNull
	private static ArrayList<Profile> getList(File root) {
		File[] dirs = root.listFiles();
		if (dirs == null) {
			return new ArrayList<>();
		}
		int size = dirs.length;
		Profile[] profiles = new Profile[size];
		for (int i = 0; i < size; i++) {
			profiles[i] = new Profile(dirs[i].getName());
		}
		return new ArrayList<>(Arrays.asList(profiles));
	}

	static void load(Profile from, String toPath, boolean config, boolean keyboard)
			throws IOException {
		if (!config && !keyboard) {
			return;
		}
		File dstConfig = new File(toPath, Config.MIDLET_CONFIG_FILE);
		File dstKeyLayout = new File(toPath, Config.MIDLET_KEY_LAYOUT_FILE);
		try {
			if (config) {
				File source = from.getConfig();
				if (source.exists())
					FileUtils.copyFileUsingChannel(source, dstConfig);
				else {
					ProfileModel params = loadConfig(from.getDir());
					if (params != null) {
						params.dir = dstConfig.getParentFile();
						saveConfig(params);
					}
				}
			}
			if (keyboard) FileUtils.copyFileUsingChannel(from.getKeyLayout(), dstKeyLayout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	static void save(Profile profile, String fromPath, boolean config, boolean keyboard)
			throws IOException {
		if (!config && !keyboard) {
			return;
		}
		profile.create();
		File srcConfig = new File(fromPath, Config.MIDLET_CONFIG_FILE);
		File srcKeyLayout = new File(fromPath, Config.MIDLET_KEY_LAYOUT_FILE);
		try {
			if (config) FileUtils.copyFileUsingChannel(srcConfig, profile.getConfig());
			if (keyboard) FileUtils.copyFileUsingChannel(srcKeyLayout, profile.getKeyLayout());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

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
			File oldFile = new File(dir, "config.xml");
			if (oldFile.exists()) {
				try (FileInputStream in = new FileInputStream(oldFile)) {
					HashMap<String, Object> map = XmlUtils.readMapXml(in);
					JsonElement json = gson.toJsonTree(map);
					params = gson.fromJson(json, ProfileModel.class);
					params.dir = dir;
					// Fix keyboard shape for old configs
					if (params.vkType == 1 || params.vkType == 2) {
						params.vkButtonShape = VirtualKeyboard.ROUND_RECT_SHAPE;
					}
					if (saveConfig(params) && oldFile.delete()) {
						Log.d(TAG, "loadConfig: old config file deleted");
					}
				} catch (Exception e) {
					Log.e(TAG, "loadConfig: ", e);
				}
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
		try (FileWriter writer = new FileWriter(new File(p.dir, Config.MIDLET_CONFIG_FILE))) {
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

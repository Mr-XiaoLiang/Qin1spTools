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

package ru.playsoftware.j2meloader.appsdb;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.playsoftware.j2meloader.applist.AppItem;

public class AppRepository {

	private AppItemDao appItemDao;

	public AppRepository(Context context) {
		AppDatabase db = AppDatabase.getDatabase(context);
		appItemDao = db.appItemDao();
	}

	public List<AppItem> getAll() {
		return appItemDao.getAllByName();
	}

	public void insert(AppItem item) {
		appItemDao.insert(item);
	}

	public void insertAll(ArrayList<AppItem> items) {
		appItemDao.insertAll(items);
	}

	public void delete(AppItem item) {
		appItemDao.delete(item);
	}

	public void deleteAll() {
		appItemDao.deleteAll();
	}

}

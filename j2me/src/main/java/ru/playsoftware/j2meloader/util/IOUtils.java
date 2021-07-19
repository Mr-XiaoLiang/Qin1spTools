/*
 * Copyright 2020 Nikita Shakarun
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

package ru.playsoftware.j2meloader.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

	private static final int BUFFER_SIZE = 16384;

	public static byte[] toByteArray(InputStream stream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buf = new byte[BUFFER_SIZE];
		int len;
		while ((len = stream.read(buf)) != -1) {
			outputStream.write(buf, 0, len);
		}
		return outputStream.toByteArray();
	}

	public static void copy(InputStream input, OutputStream output) throws IOException {
		byte[] buf = new byte[BUFFER_SIZE];
		int len;
		while ((len = input.read(buf)) != -1) {
			output.write(buf, 0, len);
		}
	}
}

/*
 *  Nokia API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  Contributor(s):
 *    Bartek Teodorczyk <barteo@barteo.net>
 *    Nikita Shakarun
 */

package com.nokia.mid.ui;

import android.util.Log;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class DirectGraphicsImp implements DirectGraphics {
	private static final String TAG = DirectGraphicsImp.class.getName();
	private final Graphics graphics;
	private int alphaComponent;

	public DirectGraphicsImp(Graphics g) {
		graphics = g;
	}

	@Override
	public void drawImage(Image img, int x, int y, int anchor, int manipulation) {
		if (img == null) {
			throw new NullPointerException();
		}
		int transform = getTransformation(manipulation);
		if (anchor >= 64 || transform == -1) {
			throw new IllegalArgumentException();
		} else {
			graphics.drawRegion(img, 0, 0, img.getWidth(), img.getHeight(),
					transform, x, y, anchor);
		}
	}

	@Override
	public void setARGBColor(int argb) {
		alphaComponent = (argb >> 24 & 0xff);
		graphics.setColorAlpha(argb);
	}

	@Override
	public int getAlphaComponent() {
		return alphaComponent;
	}

	@Override
	public int getNativePixelFormat() {
		return TYPE_INT_8888_ARGB;
	}

	@Override
	public void drawPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor) {
		setARGBColor(argbColor);
		graphics.drawPolygon(xPoints, xOffset, yPoints, yOffset, nPoints);
	}

	@Override
	public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) {
		drawPolygon(new int[]{x1, x2, x3}, 0, new int[]{y1, y2, y3}, 0, 3, argbColor);
	}

	@Override
	public void fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor) {
		setARGBColor(argbColor);
		graphics.fillPolygon(xPoints, xOffset, yPoints, yOffset, nPoints);
	}

	@Override
	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) {
		fillPolygon(new int[]{x1, x2, x3}, 0, new int[]{y1, y2, y3}, 0, 3, argbColor);
	}

	@Override
	public void drawPixels(byte[] pix, byte[] alpha, int off, int scanlen, int x, int y, int width, int height, int manipulation, int format) {
		if (pix == null) {
			throw new NullPointerException();
		}
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		if (width == 0 || height == 0) {
			return;
		}

		int transform = getTransformation(manipulation);
		int[] pixres = new int[height * width];

		switch (format) {
			case TYPE_BYTE_1_GRAY: {
				int b = 7 - off % 8;
				for (int yj = 0; yj < height; yj++) {
					int line = off + yj * scanlen;
					int ypos = yj * width;
					for (int xj = 0; xj < width; xj++) {
						pixres[ypos + xj] = doAlpha(pix, alpha, (line + xj) / 8, b);
						b--;
						if (b < 0) b = 7;
					}
					b = b - (scanlen - width) % 8;
					if (b < 0) b = 8 + b;
				}
				break;
			}
			case TYPE_BYTE_1_GRAY_VERTICAL: {
				int ods = off / scanlen;
				int oms = off % scanlen;
				int b = ods % 8;
				for (int yj = 0; yj < height; yj++) {
					int ypos = yj * width;
					int tmp = (ods + yj) / 8 * scanlen + oms;
					for (int xj = 0; xj < width; xj++) {
						pixres[ypos + xj] = doAlpha(pix, alpha, tmp + xj, b);
					}
					b++;
					if (b > 7) b = 0;
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Illegal format: " + format);
		}

		Image image = Image.createRGBImage(pixres, width, height, true);
		graphics.drawRegion(image, 0, 0, width, height, transform, x, y, 0);
	}

	@Override
	public void drawPixels(short[] pix, boolean trans, int off, int scanlen,
						   int x, int y, int width, int height, int manipulation, int format) {
		if (pix == null) {
			throw new NullPointerException();
		}
		if (format != TYPE_USHORT_4444_ARGB && format != TYPE_USHORT_444_RGB) {
			throw new IllegalArgumentException("Illegal format: " + format);
		}
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		if (width == 0 || height == 0) {
			return;
		}

		int transform = getTransformation(manipulation);
		int[] pixres = new int[height * width];

		switch (format) {
			case TYPE_USHORT_4444_ARGB: {
				for (int iy = 0; iy < height; iy++) {
					for (int ix = 0; ix < width; ix++) {
						short s = pix[off + ix + iy * scanlen];
						int a = (s & 0xF000) << 12;
						int r = (s & 0x0F00) << 8;
						int g = (s & 0x00F0) << 4;
						int b = (s & 0x000F);
						int argb = a | r | g | b;
						pixres[iy * width + ix] = argb | argb << 4;
					}
				}
				break;
			}
			case TYPE_USHORT_444_RGB: {
				for (int iy = 0; iy < height; iy++) {
					for (int ix = 0; ix < width; ix++) {
						short s = pix[off + ix + iy * scanlen];
						int rgb = (s & 0x0F00) << 8 | (s & 0x00F0) << 4 | (s & 0x000F);
						pixres[iy * width + ix] = 0xFF000000 | rgb | rgb << 4;
					}
				}
				break;
			}
			case TYPE_USHORT_565_RGB: {
				for (int iy = 0; iy < height; iy++) {
					for (int ix = 0; ix < width; ix++) {
						short s = pix[off + ix + iy * scanlen];
						int r = (s & 0xF800) << 8 | (s & 0xE000) << 3;
						int g = (s & 0x07E0) << 5 | (s & 0x0600) >> 1;
						int b = (s & 0x001F) << 3 | (s & 0x001C) >> 2;
						pixres[iy * width + ix] = 0xFF000000 | r | g | b;
					}
				}
				break;
			}
		}
		Image image = Image.createRGBImage(pixres, width, height, true);
		graphics.drawRegion(image, 0, 0, width, height, transform, x, y, 0);
	}

	@Override
	public void drawPixels(int[] pix, boolean trans, int off, int scanlen, int x, int y, int width, int height, int manipulation, int format) {
		if (pix == null) {
			throw new NullPointerException();
		}
		if (format != TYPE_INT_888_RGB && format != TYPE_INT_8888_ARGB) {
			throw new IllegalArgumentException("Illegal format: " + format);
		}
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		if (width == 0 || height == 0) {
			return;
		}

		int transform = getTransformation(manipulation);
		int[] pixres = new int[height * width];

		for (int iy = 0; iy < height; iy++) {
			for (int ix = 0; ix < width; ix++) {
				int c = pix[off + ix + iy * scanlen];
				if (format == TYPE_INT_888_RGB) {
					c |= (0xFF << 24);
				}
				pixres[iy * width + ix] = c;
			}
		}
		Image image = Image.createRGBImage(pixres, width, height, true);
		graphics.drawRegion(image, 0, 0, width, height, transform, x, y, 0);
	}

	@Override
	public void getPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanLength,
						  int x, int y, int width, int height, int format) {
		if (pixels == null) {
			throw new NullPointerException();
		}
		if (x < 0 || y < 0 || width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		if (width == 0 || height == 0) return;
		switch (format) {
			case TYPE_BYTE_1_GRAY:
				final int dataLen = height * scanLength - (scanLength - width);
				int minBytesLen = (dataLen + 7) / 8;
				if (minBytesLen > pixels.length - offset)
					throw new ArrayIndexOutOfBoundsException();
				if (transparencyMask != null && minBytesLen > transparencyMask.length - offset)
					throw new IllegalArgumentException();
				int[] colors = new int[width * height];
				graphics.getPixels(colors, 0, width, x, y, width, height);
				for (int i = offset, k = 0, w = 0, d = 0; d < dataLen; i++) {
					for (int j = 7; j >= 0 && d < dataLen; j--, w++, d++) {
						if (w == scanLength) w = 0;
						if (w >= width) {
							continue;
						}
						int color = colors[k++];
						int alpha = color >>> 31;
						int gray = (((color & 0x80) >> 7) + ((color & 0x8000) >> 15) + ((color & 0x800000) >> 23)) >> 1;
						if (gray == 0 && alpha == 1) pixels[i] |= 1 << j;
						else pixels[i] &= ~(1 << j);
						if (transparencyMask != null) {
							if (alpha == 1) transparencyMask[i] |= 1 << j;
							else transparencyMask[i] &= ~(1 << j);
						}
					}
				}
				break;
			case TYPE_BYTE_1_GRAY_VERTICAL:
			case TYPE_BYTE_2_GRAY:
			case TYPE_BYTE_4_GRAY:
			case TYPE_BYTE_8_GRAY:
			case TYPE_BYTE_332_RGB:
				Log.e(TAG, "getPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanLength, int x, int y, int width, int height, int format)");
			default:
				throw new IllegalArgumentException();
		}

	}

	@Override
	public void getPixels(short[] pix, int offset, int scanlen, int x, int y, int width, int height, int format) {
		if (pix == null) {
			throw new NullPointerException();
		}
		if (format != TYPE_USHORT_444_RGB && format != TYPE_USHORT_4444_ARGB && format != TYPE_USHORT_565_RGB) {
			throw new IllegalArgumentException("Illegal format: " + format);
		}
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		if (width == 0 || height == 0) {
			return;
		}

		int[] pixels = new int[width * height];
		graphics.getPixels(pixels, 0, width, x, y, width, height);
		switch (format) {
			case TYPE_USHORT_4444_ARGB: {
				for (int iy = 0; iy < height; iy++) {
					for (int ix = 0; ix < width; ix++) {
						int a = pixels[ix + iy * width] >> 16 & 0xF000;
						int r = pixels[ix + iy * width] >> 12 & 0x0F00;
						int g = pixels[ix + iy * width] >> 8 & 0x00F0;
						int b = pixels[ix + iy * width] >> 4 & 0x000F;

						pix[offset + iy * scanlen + ix] = (short) (a | r | g | b);
					}
				}
				break;
			}
			case TYPE_USHORT_444_RGB: {
				for (int iy = 0; iy < height; iy++) {
					for (int ix = 0; ix < width; ix++) {
						int r = pixels[ix + iy * width] >> 12 & 0x0F00;
						int g = pixels[ix + iy * width] >> 8 & 0x00F0;
						int b = pixels[ix + iy * width] >> 4 & 0x000F;

						pix[offset + iy * scanlen + ix] = (short) (0xf000 | r | g | b);
					}
				}
				break;
			}
			case TYPE_USHORT_565_RGB: {
				for (int iy = 0; iy < height; iy++) {
					for (int ix = 0; ix < width; ix++) {
						int r = pixels[ix + iy * width] >> 8 & 0xF800;
						int g = pixels[ix + iy * width] >> 5 & 0x07E0;
						int b = pixels[ix + iy * width] >> 3 & 0x001F;
						pix[offset + iy * scanlen + ix] = (short) (r | g | b);
					}
				}
			}
		}
	}

	@Override
	public void getPixels(int[] pix, int offset, int scanlen, int x, int y, int width, int height, int format) {
		if (pix == null) {
			throw new NullPointerException();
		}
		if (format != TYPE_INT_888_RGB && format != TYPE_INT_8888_ARGB) {
			throw new IllegalArgumentException("Illegal format: " + format);
		}
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		if (width == 0 || height == 0) {
			return;
		}

		graphics.getPixels(pix, offset, scanlen, x, y, width, height);
		if (format == TYPE_INT_888_RGB) {
			for (int iy = 0; iy < height; iy++) {
				for (int ix = 0; ix < width; ix++) {
					pix[offset + iy * scanlen + ix] |= 0xFF000000;
				}
			}
		}
	}

	private static int doAlpha(byte[] pix, byte[] alpha, int pos, int shift) {
		int p;
		int a;
		if (isBitSet(pix[pos], shift))
			p = 0;
		else
			p = 0x00FFFFFF;
		if (alpha == null || isBitSet(alpha[pos], shift))
			a = 0xFF000000;
		else
			a = 0;
		return p | a;
	}

	private static boolean isBitSet(byte b, int pos) {
		return ((b & (byte) (1 << pos)) != 0);
	}

	private static int getTransformation(int manipulation) {
		// manipulations are C-CW and sprite rotations are CW
		int ret = -1;
		int rotation = manipulation & 0x0FFF;
		if ((manipulation & FLIP_HORIZONTAL) != 0) {
			if ((manipulation & FLIP_VERTICAL) != 0) {
				// horiz and vertical flipping
				switch (rotation) {
					case 0:
						ret = Sprite.TRANS_ROT180;
						break;
					case ROTATE_90:
						ret = Sprite.TRANS_ROT90;
						break;
					case ROTATE_180:
						ret = Sprite.TRANS_NONE;
						break;
					case ROTATE_270:
						ret = Sprite.TRANS_ROT270;
						break;
					default:
				}
			} else {
				// horizontal flipping
				switch (rotation) {
					case 0:
						ret = Sprite.TRANS_MIRROR;
						break;
					case ROTATE_90:
						ret = Sprite.TRANS_MIRROR_ROT90;
						break;
					case ROTATE_180:
						ret = Sprite.TRANS_MIRROR_ROT180;
						break;
					case ROTATE_270:
						ret = Sprite.TRANS_MIRROR_ROT270;
						break;
					default:
				}
			}
		} else {
			if ((manipulation & FLIP_VERTICAL) != 0) {
				// vertical flipping
				switch (rotation) {
					case 0:
						ret = Sprite.TRANS_MIRROR_ROT180;
						break;
					case ROTATE_90:
						ret = Sprite.TRANS_MIRROR_ROT270;
						break;
					case ROTATE_180:
						ret = Sprite.TRANS_MIRROR;
						break;
					case ROTATE_270:
						ret = Sprite.TRANS_MIRROR_ROT90;
						break;
					default:
				}
			} else {
				// no flipping
				switch (rotation) {
					case 0:
						ret = Sprite.TRANS_NONE;
						break;
					case ROTATE_90:
						ret = Sprite.TRANS_ROT270;
						break;
					case ROTATE_180:
						ret = Sprite.TRANS_ROT180;
						break;
					case ROTATE_270:
						ret = Sprite.TRANS_ROT90;
						break;
					default:
				}
			}
		}
		return ret;
	}

}

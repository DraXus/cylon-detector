/**
 * 
 */
package com.nauj27.android.cylondetector;

import android.graphics.Bitmap;

/**
 * @author nauj27
 * The Utils class contains utilities for CylonDetector.
 */
public class Utils {
	
	/**
	 * See http://www.fourcc.org/yuv.php#NV21 for more information.
	 * We only read luminance for speed up the whole process. 
	 * All colors of the image are set to the luminance value and
	 * this way we obtains a black and white image for processing.
	 * 
	 * @param data The data array in NV21 (YCbCr_420_SP) format.
	 * @param width The photo width.
	 * @param height The photo height.
	 * @param bitmapConfig The desired Bitmap.Config format.
	 * @return Black and white bitmap decoded from NV21 input data.
	 */
	public static Bitmap getBitmapFromNV21(byte[] data, int width, int height, Bitmap.Config bitmapConfig) {
		
		int grey = 0;
		int pixelsNumber = width * height;
		int[] colors = new int[pixelsNumber];
		
		for (int pixel = 0; pixel < pixelsNumber; pixel++) {
			grey = data[pixel] & 0xff;
			colors[pixel] = 0xff000000 | (grey * 0x00010101);
		}

		Bitmap bitmap = Bitmap.createBitmap(colors, width, height, bitmapConfig);
		
		return bitmap;
	}

}

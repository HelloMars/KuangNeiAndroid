package me.kuangneipro.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtil {

	public static int calculateInSampleSize(BitmapFactory.Options options, int thumbnailSize) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > thumbnailSize || width > thumbnailSize) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)thumbnailSize);
	        } else {
	            inSampleSize = Math.round((float)width / (float)thumbnailSize);
	        }
	    }
	    return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmap(String imgFile, int thumbnailSize) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imgFile, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, thumbnailSize);
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(imgFile, options);
	}
	
	
}

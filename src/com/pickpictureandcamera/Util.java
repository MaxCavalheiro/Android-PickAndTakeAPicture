package com.pickpictureandcamera;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class Util {

	/**
	 * Calculate of scale to set image
	 *  
	 * @param Activity
	 *            activity
	 * @param String imagePath
	 * 
	 * @return BitmapFactory.Options options
	 * 
	 */
	
	public static BitmapFactory.Options calculateAcaleForImage(Activity activity,
			String imagePath) {

		int widthFullScreen = Util.getWidthOfScreen(activity);
		int heightFullScreen = Util.getHeightOfScreen(activity);

		/* Get the size of the image */
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > heightFullScreen || width > widthFullScreen) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > heightFullScreen
					|| (halfWidth / inSampleSize) > widthFullScreen) {
				inSampleSize *= 2;
			}
		}

		/* Set bitmap options to scale the image decode target */
		options.inJustDecodeBounds = false;
		options.inSampleSize = inSampleSize;
		options.inPurgeable = true;

		return options;

	}

	/**
	 * Checking device has camera hardware or not
	 * 
	 * @param Activity
	 *            activity
	 */
	public static boolean isDeviceSupportCamera(Activity activity) {
		if (activity.getApplicationContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/**
	 * Return the height of the screen
	 * 
	 * @param Activity
	 *            activity
	 */
	public static int getHeightOfScreen(Activity activity) {

		int heightOfScreen = 0;
		Point size = new Point();
		WindowManager w = activity.getWindowManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			w.getDefaultDisplay().getSize(size);
			heightOfScreen = size.y;
		} else {
			Display d = w.getDefaultDisplay();
			heightOfScreen = d.getHeight();

		}

		return heightOfScreen;
	}

	/**
	 * Return the width of the screen
	 * 
	 * @param Activity
	 *            activity
	 *
	 * @return int heightOfScreen
	 */
	public static int getWidthOfScreen(Activity activity) {

		int widthOfScreen = 0;
		Point size = new Point();
		WindowManager w = activity.getWindowManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			w.getDefaultDisplay().getSize(size);
			widthOfScreen = size.x;
		} else {
			Display d = w.getDefaultDisplay();
			widthOfScreen = d.getWidth();
		}

		return widthOfScreen;
	}

	/**
	 * Return the height of the screen
	 * 
	 * @param Activity
	 *            activity
	 * @param String
	 *            message
	 *            
	 * @return int widthOfScreen
	 */
	public static void showToast(Activity activity, String message) {
		Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
	}

}

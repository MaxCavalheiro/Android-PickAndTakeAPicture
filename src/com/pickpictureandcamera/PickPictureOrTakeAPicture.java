package com.pickpictureandcamera;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PickPictureOrTakeAPicture extends Fragment implements
		OnClickListener {

	public static final int SELECT_IMAGE_FROM_GALLERY = 1;
	private static final int SELECT_NEW_PICTURE_USING_CAMERA = 2;
	public static final int RESULT_OK = -1;

	private String mCurrentPhotoPath;
	
	private static final String PNG_FILE_PREFIX = "IMG_";
	private static final String PNG_FILE_SUFFIX = ".png";
	
	private ImageView mImageBackground;
	private Button mPickasAPicture;
	private Button mTakeAPicture;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
		
		mPickasAPicture = (Button)rootView.findViewById(R.id.pickPicture);
		mTakeAPicture = (Button)rootView.findViewById(R.id.takePicture);
		mImageBackground = (ImageView)rootView.findViewById(R.id.imageViewBackground);
		
		mPickasAPicture.setOnClickListener(this);
		mTakeAPicture.setOnClickListener(this);
		
		return rootView;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.pickPicture:
			pickAPicture();
			break;

		case R.id.takePicture:
			dispatchTakePictureIntent(SELECT_NEW_PICTURE_USING_CAMERA);
			break;

		default:
			break;
		}

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_IMAGE_FROM_GALLERY:
			if (resultCode == RESULT_OK) {

				Uri selectedImage = imageReturnedIntent.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String imagePath = cursor.getString(columnIndex);
				cursor.close();

				try {
					
					Bitmap imageReadyScaled = BitmapFactory.decodeFile(imagePath, Util.calculateAcaleForImage(getActivity(), imagePath));
					mImageBackground.setImageBitmap(imageReadyScaled);

				} catch (Exception e) {

				}

			}
			break;
		case SELECT_NEW_PICTURE_USING_CAMERA:
			if (resultCode == RESULT_OK) {
				setImageFromCamera();
			}
			break;
		}
	}
	
	private void pickAPicture(){
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, SELECT_IMAGE_FROM_GALLERY);	
	}

	private void dispatchTakePictureIntent(int actionCode) {

		switch (actionCode) {
		case SELECT_NEW_PICTURE_USING_CAMERA:
			if (Util.isDeviceSupportCamera(getActivity())) {
				try {
					Intent takePictureIntent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					File f = setUpPhotoFile();
					mCurrentPhotoPath = f.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(f));
					startActivityForResult(takePictureIntent, actionCode);
				} catch (IOException e) {
					e.printStackTrace();
					mCurrentPhotoPath = null;
					Util.showToast(getActivity(), getActivity().getResources().getString(R.string.error));
				}
			}

			break;

		default:
			break;
		}

	}
	
	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	// Create an image file name
	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = PNG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, PNG_FILE_SUFFIX, albumF);
		return imageF;
	}
	

	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/DCIM/MyPictures/");
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}
	
	private void setImageFromCamera() {
		if (mCurrentPhotoPath != null) {			
			Bitmap imageScaled = BitmapFactory.decodeFile(mCurrentPhotoPath, Util.calculateAcaleForImage(getActivity(), mCurrentPhotoPath));
			mImageBackground.setImageBitmap(imageScaled);
			
			mCurrentPhotoPath = null;
		}
	}
	
}

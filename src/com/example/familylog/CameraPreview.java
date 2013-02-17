package com.example.familylog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private final String	TAG = CameraPreview.class.getCanonicalName();
	
	private	SurfaceHolder	mSurfaceHolder;
	private	Camera			mCamera;
	private	int				mReady;
	private	long			mShotNo;
	
	private List<String> mPnrmFilePaths;
	private List<String> mPartFilePaths;

	public float getVerticalViewAngle(){
		final Parameters params = mCamera.getParameters();
		return params.getVerticalViewAngle();
	}
	
	public List<String> getPnrmList(){
		return mPnrmFilePaths;
	}
	public List<String> getPartList(){
		return mPartFilePaths;
	}

	public CameraPreview(Context context,Bundle savedInstanceState){
		super(context);

		Log.d(TAG, "CameraView::CameraView()");

		mReady = 0;
		mShotNo = 0;
		OnLoadState(savedInstanceState);
	
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	
		mPnrmFilePaths = new ArrayList<String>();
		mPartFilePaths = new ArrayList<String>();
	}

	public void OnSaveState(Bundle outState) {
		Log.d(TAG, "CameraView::OnSaveState()");

		if(outState != null)
		{
			outState.putLong("_nShotNo",mShotNo);
		}
	}

	public void OnLoadState(Bundle savedInstanceState) {
		Log.d(TAG, "CameraView::OnLoadState()");

		if(savedInstanceState != null){
			mShotNo = savedInstanceState.getLong("_nShotNo");
		}
	}
	

	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.d(TAG, "CameraView::surfaceCreated()");

		try
		{
			mCamera = Camera.open();
			mCamera.setPreviewDisplay(holder);
		}
		catch (Exception e)
		{
			Log.e(TAG, "CameraView::surfaceCreated() : Fail to setPreviewDisplay.");
		}
	}

	
	public void surfaceChanged(SurfaceHolder holder,int format,int w,int h)
	{
		Log.d(TAG, "CameraView::surfaceChanged()");

		Camera.Parameters	paramCamera = mCamera.getParameters();
		List<Size>			asizeSupport;
		Size		size;


		asizeSupport = paramCamera.getSupportedPictureSizes();
		if(asizeSupport.size() != 0)
		{
			
			size = asizeSupport.get(0);
			paramCamera.setPictureSize(size.width/2,size.height/2);
		}
		
		asizeSupport = paramCamera.getSupportedPreviewSizes();
		if(asizeSupport.size() != 0)
		{
			size = asizeSupport.get(asizeSupport.size() - 1);
			paramCamera.setPreviewSize(size.width,size.height);


			LayoutParams	paramLayout;

			paramLayout = getLayoutParams();
			paramLayout.width = size.width;
			paramLayout.height = size.height;
			setLayoutParams(paramLayout);
		}

		mCamera.setParameters(paramCamera);
		
		mCamera.startPreview();
		mReady = 1;
	}

	
	public void surfaceDestroyed(SurfaceHolder holder){
		Log.d(TAG, "CameraView::surfaceDestroyed()");

		mReady = 0;
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	
	private void restartCamera(){
		mReady = 0;
		Camera.Parameters	paramCamera = mCamera.getParameters();
		
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		
		try
		{
			mCamera = Camera.open();
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.setParameters(paramCamera);
			
			mCamera.startPreview();
			mReady = 1;
		}
		catch (Exception e)
		{
			Log.e(TAG, "CameraView::surfaceCreated() : Fail to setPreviewDisplay.");
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Log.d(TAG, "CameraView::onTouchEvent()");
//			TakePicture();	
		}
		return true;
	}



	private ShutterCallback	_pfnShutterCallback = new ShutterCallback();

	private final class ShutterCallback implements android.hardware.Camera.ShutterCallback{
		public void onShutter(){
			Log.d(TAG, "CameraView::onShutter()");
		}
	};



	private RawPictureCallback _pfnRawPictureCallback = new RawPictureCallback();
	
	private final class RawPictureCallback implements PictureCallback{
		public void onPictureTaken(byte [] rawData, android.hardware.Camera camera){
			Log.d(TAG, "CameraView::onRawPictureTaken()");
			//restartCamera();
		};
	}

	
	private final class JpgPictureCallback implements PictureCallback{
		private int mPhotoType;
		
		public JpgPictureCallback(int type){
			mPhotoType = type;
		}
		
		public void onPictureTaken(byte [] data, android.hardware.Camera camera){
			Log.d(TAG, "CameraView::onJpgPictureTaken()");

			String	strFolder;
			String	strFile;

			strFolder = Environment.getExternalStorageDirectory()+"/DCIM/Camera/";

			strFile = strFolder + "test" + mShotNo + ".jpg";
			
			//TODO
			if(mPhotoType == Const.IMEGE_TYPE_PNRM){
				mPnrmFilePaths.add(strFile);
				Log.e(TAG, "mPnrmFilePaths.add : " + strFile);
			}else{
				mPartFilePaths.add(strFile);
				Log.e(TAG, "mPartFilePaths.add : " + strFile);
			}
			
			//if(_nShotNo == 0)
			//{
			//
			//	File	newFolder = new File(strFolder);
			//	newFolder.mkdir();
			//}

			try{
				FileOutputStream	cFile = new FileOutputStream(strFile);
				cFile.write(data);
				cFile.close();

				long	nDate;
				ContentValues values = new ContentValues();

				nDate = System.currentTimeMillis();
				values.put(Images.Media.MIME_TYPE,"image/jpeg");			//
				values.put(Images.Media.DATA,strFile);						
				values.put(Images.Media.SIZE,new File(strFile).length()); 	
//				values.put(Images.Media.TITLE,strFile);
//				values.put(Images.Media.DISPLAY_NAME,strFile);
				values.put(Images.Media.DATE_ADDED,nDate);
				values.put(Images.Media.DATE_TAKEN,nDate);
				values.put(Images.Media.DATE_MODIFIED,nDate);
//				values.put(Images.Media.DESCRIPTION,"");
//				values.put(Images.Media.LATITUDE,0.0);
//				values.put(Images.Media.LONGITUDE,0.0);
//				values.put(Images.Media.ORIENTATION,"");
				
				ContentResolver	contentResolver = getContext().getContentResolver();
				contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
			}
			catch(Exception e)
			{
				Log.e(TAG, "CameraView::onJpgPictureTaken() : Fail to save image file.");
			}
			mShotNo++;

			camera.startPreview();	
			mReady = 1;
		};
	};


	private Camera.AutoFocusCallback _pfnAutoFocusCallback = new AutoFocusCallback();

	private final class AutoFocusCallback implements Camera.AutoFocusCallback
	{
		public void onAutoFocus(boolean success, Camera camera)
		{
			Log.d(TAG, "CameraView::onAutoFocus()");

			camera.autoFocus(null);
			camera.takePicture(_pfnShutterCallback, _pfnRawPictureCallback,new JpgPictureCallback(Const.IMEGE_TYPE_PART));
		};
	}


	public void TakePicture(int type){
		Log.d(TAG, "CameraView::takePicture()");

		if(mReady != 0){
			mReady = 0;
			if(false){
				mCamera.autoFocus(_pfnAutoFocusCallback);
			}else{
				mCamera.takePicture(_pfnShutterCallback, _pfnRawPictureCallback,new JpgPictureCallback(type));
			}
		}
	}
	
	
	
}


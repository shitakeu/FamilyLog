package com.example.familylog;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.sowd.imgstitch.Stitcher;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class FamilyLogActivity extends Activity implements SensorEventListener{

	private	CameraPreview	mCameraView = null;
	private SensorManager mSensor;
	private static final int MATRIX_SIZE = 16;
	private static final int DIMENSION = 3;
	private static final String TAG = FamilyLogActivity.class.getSimpleName();
	private float[] mMagneticValues = new float[DIMENSION];
	private float[] mAccelerometerValues = new float[DIMENSION];
	
	//�^���}�l�[�W���[
	private MediaManager mMediaManager;

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         
        mSensor = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);

		mCameraView = new CameraPreview(this,savedInstanceState);

//		layout.addView(_view,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		layout.addView(mCameraView,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		setContentView(layout);
		
		//�^���J�n
		mMediaManager = new MediaManager(getApplicationContext());
		mMediaManager.recordStart();
	}
    
    @Override
    public void onResume() {
    	super.onResume();
      
    	// �Z���T�[�C�x���g�̓o�^
    	mSensor.registerListener(this, mSensor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
        mSensor.registerListener(this, mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }
    
    
    @Override
    public void onPause() {
        // �Z���T�[�C�x���g���폜
        mSensor.unregisterListener(this);
        //�^���I��
        mMediaManager.recordEnd();
        
        //TODO �B�e�����p�X���擾���A�p�m���}�ʐ^���쐬�E�A�b�v
        Log.e(TAG, "VerticalViewAngle : " + mCameraView.getVerticalViewAngle());
        createPanoramaPhoto();
    	Log.e(TAG, "PnrmList size : " + mCameraView.getPnrmList().size());
        
        
        //TODO �B�e�����p�X���擾���A�ʂ̎ʐ^���A�b�v
//        mCameraView.getPnrmList();
        Log.e(TAG, "getPartList size : " + mCameraView.getPartList().size());
        
        super.onPause();
    }
    
    
    private void createPanoramaPhoto(){
    	final List<String> pnrmFilePath = mCameraView.getPnrmList();
    	final String[] paths = new String[pnrmFilePath.size()];
    	int cnt= 0;
    	Log.e(TAG, "path size : " + pnrmFilePath.size());
    	for(String path : pnrmFilePath){
    		paths[cnt] = path;
Log.e(TAG, "path : " + path);
    		cnt++;
    	}
    	
    	final String[] img_names = paths ;    	
        Bitmap[] bmps = new Bitmap[img_names.length] ;
    	
Log.e(TAG, " point 1 -------------------------"); 	
        for( int i=0;i<bmps.length;++i ) {
        	final BitmapFactory.Options options = new BitmapFactory.Options();
        	options.inJustDecodeBounds = true;
        	options.inPreferredConfig = Bitmap.Config.RGB_565;
        	BitmapFactory.decodeFile(img_names[i], options);
        	
        	int k = 2;
        	final int scaleW = options.outWidth / (380 + 1) * k;
        	final int scaleH = options.outHeight / (420 + 1)* k;
        	final int scale = Math.max(scaleW, scaleH);
        	
        	options.inJustDecodeBounds = false;
        	options.inSampleSize = scale;
        	
            bmps[i] = BitmapFactory.decodeFile(img_names[i], options) ;
            Log.e(TAG, "Width : "+bmps[i].getWidth()); 
        }
        Log.e(TAG, " point 2 -------------------------"); 
        final double angle_d = 60 ; // angle difference between adjacent images
        Bitmap stitched = Stitcher.stitch(bmps,angle_d);
        Log.e(TAG, " point 3 -------------------------");         
        
        try {
        	 // sdcard�t�H���_���w��
        	 File root = Environment.getExternalStorageDirectory();

        	 // ���t�Ńt�@�C�������쐬�@
        	 final Date mDate = new Date();
        	 final SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

        	 // �ۑ������J�n
        	 FileOutputStream fos = null;
        	 fos = new FileOutputStream(new File(root, fileName.format(mDate) + ".jpg"));

        	 // jpeg�ŕۑ�
        	 stitched.compress(CompressFormat.JPEG, 100, fos);

        	 // �ۑ������I��
        	 fos.close();
        } catch (Exception e) {
        	 Log.e("Error", "" + e.toString());
        }
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);

		if(mCameraView != null)
			mCameraView.OnSaveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);  
		
		if(mCameraView != null)
			mCameraView.OnLoadState(savedInstanceState);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		//�[���Ɉˑ����Ă��܂��܂��@�񐄏��ł����d���Ȃ����ׂẴZ���T�[�Ńf�[�^�����
//	    if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
//	        return;
	 
	    switch (event.sensor.getType()) {
	        case Sensor.TYPE_MAGNETIC_FIELD: // �n���C�Z���T
	            mMagneticValues = event.values.clone();
	            break;
	        case Sensor.TYPE_ACCELEROMETER:  // �����x�Z���T
	            mAccelerometerValues = event.values.clone();
	            break;
	    }
	 
	    if (mMagneticValues != null && mAccelerometerValues != null) {
	        final float[] rotationMatrix = new float[MATRIX_SIZE];
	        final float[] inclinationMatrix = new float[MATRIX_SIZE];
	        final float[] remapedMatrix = new float[MATRIX_SIZE];
	 
	        final float[] orientationValues = new float[DIMENSION];
	 
	        // �����x�Z���T�ƒn���C�Z���T�����]�s����擾
	        SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, mAccelerometerValues, mMagneticValues);
	        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remapedMatrix);
	        SensorManager.getOrientation(remapedMatrix, orientationValues);
	 
	        // ���ʂ��擾����
	        final int orientDegrees = toOrientationDegrees(orientationValues[0]);
	        final String orientString = toOrientationString(orientationValues[0]);  
	        if(orientDegrees % 30 == 0) {
	        	takePicture(Const.IMEGE_TYPE_PNRM);
	        }
	    }
	}
	
	private void takePicture(int type){
		mCameraView.TakePicture(type);
	}
	
	/**
	 * ���ʂ̊p�x�ɕϊ�����
	 * @param angrad
	 * @return
	 */
	private int toOrientationDegrees(float angrad) {
	    return (int)Math.floor(angrad >= 0 ? Math.toDegrees(angrad) : 360 + Math.toDegrees(angrad));
	}
	 
	/**
	 * ���ʂ̕�����ɕϊ�����
	 * @param angrad
	 * @return
	 */
	private String toOrientationString(float angrad) {
	    double[] orientation_range = {
	        - (Math.PI * 3 / 4), // ��
	        - (Math.PI * 1 / 4), // ��
	        + (Math.PI * 1 / 4), // �k
	        + (Math.PI * 3 / 4), // ��
	    };
	 
	    String[] orientation_string = {
	        "south",
	        "west",
	        "north",
	        "east",
	    };
	 
	    for (int i = 0; i < orientation_range.length; i++) {
	        if (angrad < orientation_range[i]) {
	            return orientation_string[i];
	        }
	    }
	 
	    return orientation_string[0];
	}
	
}

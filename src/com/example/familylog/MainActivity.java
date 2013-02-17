package com.example.familylog;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, SensorEventListener{
	
	private TextView mTxt;
	private TextView mAngTxt;
	private MediaManager mMediaManager;
	
	private SensorManager mSensor;
	private static final int MATRIX_SIZE = 16;
	private static final int DIMENSION = 3;
	private float[] mMagneticValues = new float[DIMENSION];
	private float[] mAccelerometerValues = new float[DIMENSION];
	
	private final String TAG = MainActivity.class.getSimpleName(); 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //�Z���T�[����
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         
        mSensor = (SensorManager)getSystemService(SENSOR_SERVICE);
        
        setContentView(R.layout.activity_main);
       
        mMediaManager = new MediaManager(getApplicationContext());
        
        findViewById(R.id.rec_start_btn).setOnClickListener(this);
        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.end_btn).setOnClickListener(this);
        findViewById(R.id.photo_btn).setOnClickListener(this);
        findViewById(R.id.angle_photo_btn).setOnClickListener(this);
        findViewById(R.id.all_start_btn).setOnClickListener(this);
        
        mTxt = (TextView)findViewById(R.id.textView);
        mAngTxt = (TextView)findViewById(R.id.angle_txt);
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
      
        super.onPause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
	 final int id = v.getId();
	 	switch (id) {
		case R.id.start_btn:
			mTxt.setText("�^���J�n");
			mMediaManager.recordStart();
			break;
		case R.id.end_btn:
			mTxt.setText("�^����~");
			mMediaManager.recordEnd();
			break;
		case R.id.rec_start_btn:
			mMediaManager.play();
			break;
		case R.id.rec_stop_btn:
			mTxt.setText("�Đ��I��");
			mMediaManager.stop();
			break;	
		case R.id.photo_btn:
			final Intent photoIntent = new Intent(this, PhotoActivity.class);
			photoIntent.setAction(Intent.ACTION_VIEW);
			startActivity(photoIntent);
			break;
		case R.id.angle_photo_btn:
			break;
		case R.id.all_start_btn:
			final Intent intent = new Intent(this, FamilyLogActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);
			break;
		case R.id.all_stop_btn:
			
			final Intent VideoIntent = new Intent(this, VideoActivity.class);
			VideoIntent.setAction(Intent.ACTION_VIEW);
			startActivity(VideoIntent);
			break;
		default:
			break;
		}
		
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
	        mAngTxt.setText("orientDegrees = " + orientDegrees + " , orientString = " + orientString);
	    } 
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

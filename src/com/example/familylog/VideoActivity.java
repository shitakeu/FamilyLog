package com.example.familylog;

	import java.io.File;
	 
	import java.io.IOException;
	import java.util.List;
	 
	import android.os.Environment;
	import android.app.Activity;
	import android.hardware.Camera;
	import android.hardware.Camera.Size;
	import android.media.MediaRecorder;
	import android.os.Bundle;
	import android.util.Log;
import android.view.MotionEvent;
	import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
	import android.view.SurfaceView;
	import android.view.View;
import android.view.ViewGroup.LayoutParams;
	 
	public class VideoActivity extends Activity {
		private MediaRecorder myRecorder;
		private boolean isRecording;
		SurfaceHolder v_holder;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
//			setContentView(R.layout.main);
//
//			SurfaceView mySurfaceView = (SurfaceView) findViewById(R.id.surface_view);
			SurfaceView mySurfaceView = new SurfaceView(this);
			  LayoutParams params = new LayoutParams (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			  mySurfaceView.setLayoutParams(params);
			
			
			SurfaceHolder holder = mySurfaceView.getHolder();
			holder.addCallback((Callback) this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			
			myRecorder = new MediaRecorder(); 
		}

		public void surfaceCreated(SurfaceHolder holder) {
			//
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			v_holder = holder; 
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			//
		}

		public void initializeVideoSettings() {
			myRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT); 
			myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); 
			myRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP); 

			myRecorder.setOutputFile("/sdcard/sample.3gp"); 
			myRecorder.setVideoFrameRate(30); 
			myRecorder.setVideoSize(320, 240);		
				myRecorder.setPreviewDisplay(v_holder.getSurface()); 
			try {
				myRecorder.prepare(); //
			} catch (Exception e) {
				Log.e("recMovie", e.getMessage());
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (!isRecording) {
					initializeVideoSettings(); 
					myRecorder.start(); 
					isRecording = true; 

				} else {
					myRecorder.stop(); 
					myRecorder.reset(); 
					isRecording = false; 
				}
			}
			return true;
		}
	}

package com.example.familylog;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

public class MediaManager {
	
	MediaRecorder mRecorder;	
	String mFilePath;
	MediaPlayer mPlayer;
	
	public MediaManager(Context context){
        mRecorder = new MediaRecorder();
        mFilePath = Environment.getExternalStorageDirectory() + "/audio.3gp";
        mPlayer = new MediaPlayer();
        
        final File file = new File(mFilePath);
        if(file.isFile()){
        	file.delete();
        }
	}
	
	public boolean recordStart(){
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
 
        //保存先
        mRecorder.setOutputFile(mFilePath);
 
        //録音準備＆録音開始
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        mRecorder.start();   //録音開始
		return true;
	}
	
	public void recordEnd(){
        mRecorder.stop();
        mRecorder.reset();   //オブジェクトのリセット
        //release()前であればsetAudioSourceメソッドを呼び出すことで再利用可能
        mRecorder.release(); //Recorderオブジェクトの解放
	}
	
	public boolean play(){
		try {
			mPlayer.setDataSource(mFilePath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void stop(){
		mPlayer.stop();
	}
	
	
	
}

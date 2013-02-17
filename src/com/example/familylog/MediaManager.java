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
 
        //�ۑ���
        mRecorder.setOutputFile(mFilePath);
 
        //�^���������^���J�n
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        mRecorder.start();   //�^���J�n
		return true;
	}
	
	public void recordEnd(){
        mRecorder.stop();
        mRecorder.reset();   //�I�u�W�F�N�g�̃��Z�b�g
        //release()�O�ł����setAudioSource���\�b�h���Ăяo�����Ƃōė��p�\
        mRecorder.release(); //Recorder�I�u�W�F�N�g�̉��
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

package com.samsandberg.talkingjames;

import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * Notes:
 * 
 * Since API Level 9:
 * http://developer.android.com/reference/android/media/audiofx/Visualizer.html
 * http://developer.android.com/reference/android/media/audiofx/Visualizer.OnDataCaptureListener.html
 * http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/media/AudioFxDemo.html
 * 
 * Going with these for now (available since API level 3):
 * http://developer.android.com/reference/android/media/AudioRecord.html (recording)
 * http://developer.android.com/reference/android/media/AudioTrack.html (playback)
 * 
 * Tutorial:
 * http://www.androiddevblog.net/android/android-audio-recording-part-2
 * 
 * Other considerations:
 * Channels - http://developer.android.com/reference/java/nio/channels/package-summary.html
 * (New I/O http://en.wikipedia.org/wiki/New_I/O)
 */
class Talking {
	protected final String TAG = "Talking";
	
	protected String mFileName;
	protected AudioManager mAudioManager;
	protected MediaPlayer mPlayer;
	protected MediaRecorder mRecorder;
	protected double mEMA;

	protected int status;
	
	static final int STATUS_NONE = 0;
	static final int STATUS_RECORD = 1;
	static final int STATUS_PLAYBACK = 2;
	
	// TODO: this solution is temporary - will switch to detecting audio to know when to playback
	protected long mTimestamp;
	
	public Talking(Context context) {		
		mEMA = 0.0;
		
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/talkingjames.3gp";
		
		mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);

		status = Talking.STATUS_NONE;
	}
	
	public void updateTalking() {
		// TODO: for now switch off every 5 seconds
		long timeNow = Calendar.getInstance().getTimeInMillis();
		if (status == Talking.STATUS_NONE || timeNow - 5000 > mTimestamp) {
			if (status == Talking.STATUS_NONE || status == Talking.STATUS_PLAYBACK) {
				Log.i(TAG, "SWITCHING TO STATUS_RECORD!");
				stopPlaying();
		        startRecording();
			}
			else if (status == Talking.STATUS_RECORD) {
				Log.i(TAG, "SWITCHING TO STATUS_PLAYBACK!");
				stopRecording();
				startPlaying();
			}
		}
	}
    
    protected void startRecording() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mRecorder.setOutputFile("/dev/null");
            mRecorder.setOutputFile(mFileName);
            
            try {
				mRecorder.prepare();
	            mRecorder.start();

	    		status = Talking.STATUS_RECORD;
	            mTimestamp = Calendar.getInstance().getTimeInMillis();
	            return;
            
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, e.toString());
			}
        }
		
        // This is only reached if an exception is thrown
    	stopRecording();
    }
    
    protected void stopRecording() {
    	if (mRecorder != null) {
	    	mRecorder.stop();
	    	mRecorder.release();
	    	mRecorder = null;
    	}
    	status = Talking.STATUS_NONE;
    }
    
    protected void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
    		status = Talking.STATUS_PLAYBACK;
            mTimestamp = Calendar.getInstance().getTimeInMillis();
            return;
        
        } catch (IOException e) {
            Log.e(TAG, "playback() prepare failed");
        }

        // This is only reached if an exception is thrown
        stopPlaying();
    }
    
    protected void stopPlaying() {
    	if (mPlayer != null) {
        	mPlayer.release();
        	mPlayer = null;
    	}
    	status = Talking.STATUS_NONE;
    }
}
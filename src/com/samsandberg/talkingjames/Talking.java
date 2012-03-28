package com.samsandberg.talkingjames;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Environment;
import android.os.Handler;

/**
 * 
 * Notes:
 * 
 * Since API Level 9:
 * http://developer.android.com/reference/android/media/audiofx/Visualizer.html
 * http://developer.android.com/reference/android/media/audiofx/Visualizer.OnDataCaptureListener.html
 * http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/media/AudioFxDemo.html
 * 
 * At some point must figure out how to use these instead (available since API level 3):
 * http://developer.android.com/reference/android/media/AudioRecord.html (recording)
 * http://developer.android.com/reference/android/media/AudioTrack.html (playback)
 * 
 * Tutorial:
 * http://www.androiddevblog.net/android/android-audio-recording-part-2
 * 
 * Other considerations:
 * Channels - http://developer.android.com/reference/java/nio/channels/package-summary.html
 * (New I/O http://en.wikipedia.org/wiki/New_I/O)
 * 
 * http://stackoverflow.com/questions/4811211/visualising-android-audiotrack-from-a-bytestream
 */
class Talking {
	protected final String TAG = "TalkingJames_Talking";
	
	public static final int STATE_NONE = 0;
	public static final int STATE_RECORD = 1;
	public static final int STATE_PLAYBACK = 2;

	protected int myState;
	protected boolean running;
	
	protected Scene scene;
	
	protected Visualizer mVisualizer;
	protected MediaRecorder recorder;
	protected MediaPlayer player;
	
	protected String mFileName;
	
	// TODO: this solution is temporary - will switch to detecting audio to know when to playback
	protected int recordTimeSecs = 5;
	protected long mTimestamp;
	
	
	public Talking(Scene scene) {
		this.scene = scene;
		changeMyState(Talking.STATE_NONE);
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/talkingjames.3gp";
	}
	
	public void startTalking() {
//		Log.i(TAG, "start()");
		running = true;
		startRecording();
	}
	
	public void stopTalking() {
//		Log.i(TAG, "stop()");
		running = false;
		stopRecording();
		stopPlaying();
	}
	
	protected void changeMyState(int newState) {
//    	Log.i(TAG, "changeMyState()");
		myState = newState;
		scene.updatemyState(myState);
	}
    
    protected void startRecording() {
//    	Log.i(TAG, "startRecording()");
    	
    	if (! running) {
    		return;
    	}
    	
    	if (recorder == null) {
		    recorder = new MediaRecorder();
		    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		    recorder.setOutputFile(mFileName);
		    
		    try {
                   recorder.prepare();
		           recorder.start();
		           changeMyState(Talking.STATE_RECORD);
		           
		           // Sleep for recordTimeSecs
		           Handler handler = new Handler(); 
		           handler.postDelayed(new Runnable() { 
		                public void run() { 
		                     stopRecording();
		                     startPlaying();
		                } 
		           }, recordTimeSecs * 1000); 
            } 
		    
		    catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
//                Log.e(TAG, e.toString());
                stopRecording();
            } 
		    
		    catch (IOException e) {
            	// TODO Auto-generated catch block
                e.printStackTrace();
//                Log.e(TAG, e.toString());
                stopRecording();
            }
		}
    }
    
    protected void stopRecording() {
//    	Log.i(TAG, "stopRecording()");
    	
    	if (recorder != null) {
	    	recorder.stop();
	    	recorder.release();
	    	recorder = null;
    	}
    	changeMyState(Talking.STATE_NONE);
    }
    
    protected void startPlaying() {
//    	Log.i(TAG, "startPlaying()");
    	
    	if (! running) {
    		return;
    	}
    	
    	player = new MediaPlayer();
    	try {
    	    player.setDataSource(mFileName);
    	    player.prepare();
    	}
    	
    	catch (IOException e) {
//    	    Log.e(TAG, "playback() prepare failed");
    	    stopPlaying();
    	    return;
    	}

    	// Create the Visualizer object and attach it to our media player.
    	mVisualizer = new Visualizer(player.getAudioSessionId());
    	mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
    	mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
    		public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
//    			updateMouthOpenSize(bytes);
        	}

    		public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
    			updateMouthOpenSize(bytes);
			}
		}, Visualizer.getMaxCaptureRate() / 2, false, true);
    	mVisualizer.setEnabled(true);
    	
	    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	    	public void onCompletion(MediaPlayer mediaPlayer) {
	    		stopPlaying();
	    		startRecording();
	    	}
	    });
	    
	    player.start();
	    changeMyState(Talking.STATE_PLAYBACK);
    }
    
    protected void stopPlaying() {
//    	Log.i(TAG, "stopPlaying()");

		mVisualizer.setEnabled(false);
    	scene.updateMouthOpenSize(0);
    	
    	if (player != null) {
    		player.stop();
        	player.release();
        	player = null;
    	}
    	changeMyState(Talking.STATE_NONE);
    }
    
    /**
     * updateMouthOpenSize
     * 
     * @param bytes
     */
    protected void updateMouthOpenSize(byte[] bytes) {
    	float max = 0;
    	for (int i = 0; i < bytes.length; i++) {
    		float byteI = Math.abs((byte) bytes[i]);
    		if (byteI > max) {
    			max = byteI;
    		}
    	}
    	scene.updateMouthOpenSize(max);
//    	Log.i(TAG, "mouthOpenSize=" + max);
    }
}
package com.samsandberg.talkingjames;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Calendar;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaRecorder;
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
class Talking implements OnPlaybackPositionUpdateListener {
	protected final String TAG = "Talking";
	
	protected float mouthOpenSize;

	protected int myState;
	
	static final int STATE_NONE = 0;
	static final int STATE_RECORD = 1;
	static final int STATE_PLAYBACK = 2;
	
	protected AudioRecord recorder;
	protected AudioTrack player;
	
	protected int sampleRateInHz = 44100;
	protected int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	protected int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	protected int recordBufferSize;
	protected int playbackBufferSize;
	
	protected int offset;
	protected byte[] mBuffer;
	
	// TODO: this solution is temporary - will switch to detecting audio to know when to playback
	protected int recordTimeSecs = 5;
	protected long mTimestamp;
	
	protected boolean running;
	
	
	public Talking(Context context) {
        recordBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        playbackBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        Log.i(TAG, "recordBufferSize=" + recordBufferSize + " playbackBufferSize=" + playbackBufferSize);

		myState = Talking.STATE_NONE;
		
		mouthOpenSize = 0;
	}
	
	public void startTalking() {
		Log.i(TAG, "start()");
		running = true;
		startRecording();
	}
	
	public void stopTalking() {
		Log.i(TAG, "stop()");
		running = false;
	}
	
	public float getMouthOpenSize() {
		return mouthOpenSize;
	}
    
    protected void startRecording() {
    	Log.i(TAG, "startRecording()");
    	
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 
    		sampleRateInHz, channelConfig, audioFormat, recordBufferSize);
        recorder.startRecording();
        
        // Not sure how big to make this guy...
        mBuffer = new byte[recordBufferSize * 1000];
//        mBuffer = new byte[80000];
    	
		myState = Talking.STATE_RECORD;
        mTimestamp = Calendar.getInstance().getTimeInMillis();
    	
    	new Thread(new Runnable() {
            public void run() {
            	offset = 0;
                while(running &&
	            		offset + recordBufferSize < mBuffer.length && 
	            		myState == Talking.STATE_RECORD) {
                    recorder.read(mBuffer, offset, recordBufferSize);
                    offset += recordBufferSize;
                    
                    if (Calendar.getInstance().getTimeInMillis() - (recordTimeSecs * 1000) > mTimestamp) {
                    	myState = Talking.STATE_NONE;
                    }
                }
                stopRecording();
                
                if (! running) {
                    return;
                }
                
                startPlaying();
            }
    	}).start();
    }
    
    protected void stopRecording() {
    	Log.i(TAG, "stopRecording()");
    	
    	if (recorder != null) {
	    	recorder.stop();
	    	recorder.release();
	    	recorder = null;
    	}
    	myState = Talking.STATE_NONE;
    }
    
    protected void startPlaying() {
    	Log.i(TAG, "startPlaying()");
    	
    	player = new AudioTrack(AudioManager.STREAM_MUSIC,
    		sampleRateInHz, channelConfig, audioFormat, playbackBufferSize, AudioTrack.MODE_STREAM);
    	player.play();
//    	player.setPositionNotificationPeriod(2205);
//    	player.setPlaybackPositionUpdateListener(this);
    	myState = Talking.STATE_PLAYBACK;

    	new Thread(new Runnable() {
            public void run() {
            	/**
            	 * from Napalm
//            	int[] mLoundness = new int[256];
//            	int dropoutSpeed = 1;
            	 */
            	
            	int playOffset = 0;
            	
            	int segments = 0;
//            	int updateSegmentFreq = 10;
            	
//            	int[] counts = new int[101];
//            	int[] values = new int[101];
            	
                while(running && playOffset + playbackBufferSize < offset && myState == Talking.STATE_PLAYBACK) {
                	player.write(mBuffer, playOffset, playbackBufferSize);
                	
                	segments += 1;
                	
//                	if (segments % updateSegmentFreq == 0) {
//                    	Log.i(TAG, "segment " + segments + ":");
//                    	for (int i = 0; i < 101; i++) {
//                    		int avg = (counts[i] > 0) ? (values[i] / counts[i]) : 0;
//                    		Log.i(TAG, i + ": count=" + counts[i] + " avg=" + avg);
//                    	}
//
//                    	counts = new int[101];
//                    	values = new int[101];
//                	}
                    
                	/**
                	 * From Napalm
//                	for(int i = 0; i < mLoundness.length; i++)
//                		if(mLoundness[i] > 0)
//                			mLoundness[i] -= dropoutSpeed;
//                	
//                	for(int i = 0; i < mBuffer.length; i++)
//                		mLoundness[mBuffer[i]]++;
//                	
//                	long avgLoundness = 0;
//                	for(int i = 0; i < mLoundness.length; i++)
//                		avgLoundness += mLoundness[i];
//                	avgLoundness /= mLoundness.length;
//                	Log.i(TAG, "avgLoundness=" + avgLoundness);
                	 */
                	
                	
                	// But turns out we can't use 8 bit, so doing this:
                	ShortBuffer sb = ByteBuffer.wrap(mBuffer).asShortBuffer();
                	
                	int loudness = 0;
                	int numChecked = 0;
                	
                	for (int i = playOffset; i < playOffset + playbackBufferSize; i += 1) {
//                		Log.i(TAG, "sb[" + i + "]=" + sb.get(i));
                		int currentShort = (int) Math.abs(sb.get(i));
//                		int outOf100 = 100 * currentShort / 32768;
//                		counts[outOf100]++;
//                		values[outOf100] += currentShort;

            			loudness += currentShort;
            			numChecked++;
            			
//	            		if (currentShort > 20000) {
////	            			loudness += (int) (100 * currentShort / 32768);
//	            			loudness += currentShort;
//	            			numChecked++;
//	            		}
                	}

        			mouthOpenSize = 100 * (loudness / 32768) / numChecked;
//            		if (numChecked == 50) {
//            			mouthOpenSize = 100 * (loudness / 32768) / numChecked;
//            			loudness = numChecked = 0;
//            		} else {
//            			mouthOpenSize = 10;
//            		}
        			Log.i(TAG, "segment=" + segments + " mouthOpenSize=" + mouthOpenSize);
                	
                	// Instead of that we're gonna do the set playback notification thingy on the bottom...
                	
//                	if (numChecked > 0) {
//	                	loudness /= numChecked;
//	                	Log.i(TAG, "numChecked=" + numChecked + " loudness=" + loudness);
//                	}
                	
                    playOffset += playbackBufferSize;
                }
                
//            	Log.i(TAG, "Num segments = " + segments);
                stopPlaying();
                
                if (! running) {
                    return;
                }

                startRecording();
            }
    	}).start();
    }
    
    protected void stopPlaying() {
    	Log.i(TAG, "stopPlaying()");
    	
    	mouthOpenSize = 0;
    	
    	if (player != null) {
    		player.stop();
        	player.release();
        	player = null;
    	}
    	myState = Talking.STATE_NONE;
    }

	@Override
	public void onMarkerReached(AudioTrack track) {
		// TODO Auto-generated method stub
		
	}

	// Note: got this one from http://stackoverflow.com/questions/4811211/visualising-android-audiotrack-from-a-bytestream
	@Override
	public void onPeriodicNotification(AudioTrack track) {
		// TODO Auto-generated method stub
	    int pos = track.getNotificationMarkerPosition();

//	    short[] slice = Array.copy(_data, pos, _sliceSize) // pseudo-code
//	    // render the slice to the view
	    
//    	ByteBuffer bb = ByteBuffer.wrap(mBuffer);
//    	ShortBuffer sb = bb.asShortBuffer();
//    	int loudness = 0;
//    	int numChecked = 0;
//    	for (int i = pos; i < pos + playbackBufferSize; i += 1) {
////    		Log.i(TAG, "sb[" + i + "]=" + sb.get(i));
//    		int currentShort = (int) sb.get(i);
//    		if (currentShort > 20000) {
////    			loudness += (int) (100 * currentShort / 32768);
//    			loudness += currentShort;
//    			numChecked++;
//    		}
//    		
//    		if (numChecked == 50) {
//    			mouthOpenSize = 100 * (loudness / 32768) / numChecked;
//    			Log.i(TAG, "mouthOpenSize=" + mouthOpenSize);
//    			loudness = numChecked = 0;
//    		} else {
//    			mouthOpenSize = 10;
//    		}
//    	}
	}
}
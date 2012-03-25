package com.samsandberg.talkingjames;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Scene extends View {
	
	protected final String TAG = "Scene";
	
	protected Paint mPaint;

	protected Bitmap jamesTop;
	protected Bitmap jamesBottom;
	
	protected float mouthOpenSize;
	
	protected MediaRecorder mRecorder;
	protected double mEMA;
	

	public Scene(Context context) {
		super(context);
		
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.FILL);

		// 100 x 96
		jamesTop = BitmapFactory.decodeResource(context.getResources(), R.drawable.jamestop);
		
		// 100 x 29
		jamesBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.jamesbottom);
		
		// For starters
		mouthOpenSize = 0;
		
        mEMA = 0.0;

        if (! startRecording()) {
    		Toast.makeText(context, "Unable to start recording", Toast.LENGTH_SHORT).show();
        }
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mEMA = getRecordingAmplitude();
		if (mEMA != 0.0) {
			mouthOpenSize = (float) (mEMA / 300.0);
//			Log.i(TAG, "Amplitude=" + mEMA + " MouthOpenSize=" + mouthOpenSize);
		}
		
		canvas.drawPaint(mPaint);
		
		Point center = new Point(canvas.getWidth()/2, canvas.getHeight()/2);
		
		Point topTopLeft = new Point(center.x - (jamesTop.getWidth() / 2), center.y - jamesTop.getHeight());
		Point bottomTopLeft = new Point(topTopLeft.x, center.y);
		
		canvas.drawBitmap(jamesTop, topTopLeft.x, topTopLeft.y - (mouthOpenSize / 2), null);
		canvas.drawBitmap(jamesBottom, bottomTopLeft.x, bottomTopLeft.y + (mouthOpenSize / 2), null);
		
		// refresh the canvas
		invalidate();
	}
    
    protected boolean startRecording() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            
            try {
				mRecorder.prepare();
	            mRecorder.start();
	            return true;
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
		
		return false;
    }

    protected double getRecordingAmplitude() {
        if (mRecorder != null) {
//            return  (mRecorder.getMaxAmplitude()/2700.0);
        	return  (mRecorder.getMaxAmplitude());
        }
        
        return 0.0;
    }
}
package com.samsandberg.talkingjames;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.View;

public class Scene extends View {
	
	protected final String TAG = "TalkingJames_Scene";
	
	protected Paint mPaint, redPaint, greenPaint;

	protected Bitmap jamesTop;
	protected Bitmap jamesBottom;
	
	protected int myState;
	protected float mouthOpenSize;
	

	public Scene(Context context) {
		super(context);
		
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.FILL);
		
		redPaint = new Paint();
		redPaint.setColor(Color.RED);
		redPaint.setStyle(Style.FILL);
		
		greenPaint = new Paint();
		greenPaint.setColor(Color.GREEN);
		greenPaint.setStyle(Style.FILL);

		// 100 x 96
		jamesTop = BitmapFactory.decodeResource(context.getResources(), R.drawable.jamestop);
		
		// 100 x 29
		jamesBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.jamesbottom);
		
		// For starters
		myState = Talking.STATE_NONE;
		mouthOpenSize = 0;
	}
	
	public void updateMouthOpenSize(float mouthOpenSize) {
		this.mouthOpenSize = mouthOpenSize;

		// refresh the canvas
		invalidate();
	}
	
	public void updatemyState(int myState) {
		this.myState = myState;

		// refresh the canvas
		invalidate();
	}

	// TODO: Draw some text on the canvas
	// Maybe via http://www.helloandroid.com/tutorials/how-draw-multiline-text-canvas-easily
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Paint a white background
		canvas.drawPaint(mPaint);
		
		// Paint a status circle in the top left...
		if (myState == Talking.STATE_RECORD) {
			canvas.drawCircle(15, 15, 10, redPaint);
		} else if (myState == Talking.STATE_PLAYBACK) {
			canvas.drawCircle(15, 15, 10, greenPaint);
		}
		
		Point center = new Point(canvas.getWidth()/2, canvas.getHeight()/2);
		
		Point topTopLeft = new Point(center.x - (jamesTop.getWidth() / 2), center.y - jamesTop.getHeight());
		Point bottomTopLeft = new Point(topTopLeft.x, center.y);
		
		canvas.drawBitmap(jamesTop, topTopLeft.x, topTopLeft.y - (mouthOpenSize / 2), null);
		canvas.drawBitmap(jamesBottom, bottomTopLeft.x, bottomTopLeft.y + (mouthOpenSize / 2), null);
	}
}
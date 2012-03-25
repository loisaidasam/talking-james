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
	
	protected final String TAG = "Scene";
	
	protected Paint mPaint;

	protected Bitmap jamesTop;
	protected Bitmap jamesBottom;
	
	protected float mouthOpenSize;
	
	protected Talking talking;
	

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
		
		talking = new Talking(context);
	}

	// TODO: Draw some text on the canvas
	// Maybe via http://www.helloandroid.com/tutorials/how-draw-multiline-text-canvas-easily
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		talking.updateTalking();
		
		updateMouthOpenSize();
		
		canvas.drawPaint(mPaint);
		
		Point center = new Point(canvas.getWidth()/2, canvas.getHeight()/2);
		
		Point topTopLeft = new Point(center.x - (jamesTop.getWidth() / 2), center.y - jamesTop.getHeight());
		Point bottomTopLeft = new Point(topTopLeft.x, center.y);
		
		canvas.drawBitmap(jamesTop, topTopLeft.x, topTopLeft.y - (mouthOpenSize / 2), null);
		canvas.drawBitmap(jamesBottom, bottomTopLeft.x, bottomTopLeft.y + (mouthOpenSize / 2), null);
		
		// refresh the canvas
		invalidate();
	}

    protected void updateMouthOpenSize() {
    	// TODO: fill in this ish
    }
}
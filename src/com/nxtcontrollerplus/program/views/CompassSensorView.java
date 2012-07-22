package com.nxtcontrollerplus.program.views;

import com.nxtcontrollerplus.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CompassSensorView extends SensorView{
	
	private Bitmap compassBG = null, compassArrow = null;
	private Matrix matrix = null;
	private boolean compassMode = false;
	private int width, height;
	
	private void setUpComponents(Context context){
		this.compassBG = BitmapFactory.decodeResource(context.getResources(), R.drawable.compass);
		this.compassArrow =  BitmapFactory.decodeResource(context.getResources(), R.drawable.compass_arrow);
		this.matrix = new Matrix();
		this.setOnTouchListener(changeModeOnTouchListener);
	}
	
    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        this.width = width;
        this.height = height;
        Bitmap temp = Bitmap.createScaledBitmap(compassBG, width,height, true);
        this.compassBG  = temp;
        temp = Bitmap.createScaledBitmap(compassArrow, width,height, true);
        this.compassArrow = temp;
    }
	
	public CompassSensorView(Context context, AttributeSet attrSet) {
		super(context,attrSet);
		isInEditMode();
		setUpComponents(context);
	}
	
	@Override
	protected void onDraw (Canvas canvas){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		int azimuth = getSensorValue();
		int left = getLeft();
		int top = getTop();
		matrix.setTranslate(left,top);
		matrix.postRotate(azimuth, left+(width/2), top+(height/2));
		if(compassMode){
			canvas.drawBitmap(compassBG,matrix, paint);
			canvas.drawBitmap(compassArrow, 0, 0,  paint);
		}else{
			canvas.drawBitmap(compassBG, 0, 0, paint);
			canvas.drawBitmap(compassArrow, matrix,  paint);
		}
	}
	
	public OnTouchListener changeModeOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN)) {
            	compassMode = !compassMode;
            }
			return true;
		}
    };

}

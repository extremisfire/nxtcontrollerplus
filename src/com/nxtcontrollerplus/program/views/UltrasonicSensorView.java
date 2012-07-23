package com.nxtcontrollerplus.program.views;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class UltrasonicSensorView extends SensorView{

	public static final int MAX_DISTANCE = 254;
	
	private Paint paint = null;
	private int width,radius;
	private Point center = null;
	private final int bgColor = Color.argb(128, 0, 105, 0);
	private final int fgColor = Color.parseColor("#25d626");
	private HashMap<Integer, Integer> detectedObjects = null;
	private ArrayList<PointF> drawPoints = null;
	private float cmToPixels;
	private int currentAngle = 0;
	
	public int getCurrentAngle() {
		return currentAngle;
	}

	public void setCurrentAngle(int currentAngle) {
		this.currentAngle = currentAngle;
	}

	private void clearRadar(){
		detectedObjects.clear();
		this.postInvalidate();
	}
	
	public void addDetectedObject(int angle, int distance){
		if(distance <= MAX_DISTANCE && distance >= 0){
			detectedObjects.put(angle, distance);
			calculatePointsOnRadar();
		}
	}
	
	public void calculatePointsOnRadar(){
		this.drawPoints.clear();
		float angle = 0;
		float distance = 0;
		float cx = 0, cy = 0;
    	for(Integer key:detectedObjects.keySet()){
    		angle = key;
    		angle = (float) Math.toRadians(angle);
    		angle += -Math.PI/2;
    		distance = detectedObjects.get(key);
    		distance = (float) distance*cmToPixels;
    		cx = center.x;
    		cy = center.y;
    		cx += (float) (Math.cos(angle) * distance);
    		cy += (float) (Math.sin(angle) * distance);
    		PointF p = new PointF(cx,cy);
    		drawPoints.add(p);
    	}  
	}
	
	public UltrasonicSensorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.center = new Point();
		this.paint = new Paint();
		this.setOnTouchListener(turnRadarOnTouchListener);
		this.detectedObjects = new HashMap<Integer, Integer>();
		this.drawPoints = new ArrayList<PointF>();
	}
	
    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        this.width = width;
        this.radius = this.width/2;
        this.center.x = width / 2;
        this.center.y = height / 2;
        this.cmToPixels = (float)radius / (float)MAX_DISTANCE;
        clearRadar();
    }
	
    private void drawBackground(Canvas canvas){
    	paint.setStyle(Style.FILL);
    	paint.setColor(this.bgColor);
    	canvas.drawCircle(center.x, center.y, radius, paint);
    }
    
    private void drawLines(Canvas canvas){
    	paint.setStyle(Style.STROKE);
    	paint.setColor(this.fgColor);
    	int offsetY = 2;
    	int offsetX = 22;
    	canvas.drawCircle(center.x, center.y, (float) (radius*1.0), paint);
    	canvas.drawText("254", (float) (center.x+(radius*1.0)-offsetX), center.y-offsetY, paint);
    	
    	canvas.drawCircle(center.x, center.y, (float) (radius*0.66), paint);
    	canvas.drawText("168", (float) (center.x+(radius*0.66)-offsetX), center.y-offsetY, paint);
    	
    	canvas.drawCircle(center.x, center.y, (float) (radius*0.33), paint);
    	canvas.drawText("84", (float) (center.x+(radius*0.33)-offsetX), center.y-offsetY, paint);
		
    	canvas.drawLine(center.x-radius, center.y, center.x+radius, center.y, paint);
		canvas.drawLine(center.x, center.y-radius, center.x, center.y+radius, paint);
    }
    
    private void drawDetectedObject(Canvas canvas){
    	final int size = 3;
    	paint.setColor(this.fgColor);
    	paint.setStyle(Style.FILL);
    	for(PointF p:drawPoints){
    		canvas.drawCircle(p.x, p.y, size, paint);
    	}    	
    }
    
    private void drawCurrentAngle(Canvas canvas){
		float angle = getCurrentAngle();
		angle = (float) Math.toRadians(angle);
		angle += -Math.PI/2;
		float distance = 254;
		distance = (float) distance*cmToPixels;
		float cx = center.x;
		float cy = center.y;
		cx += (float) (Math.cos(angle) * distance);
		cy += (float) (Math.sin(angle) * distance);
		paint.setColor(Color.WHITE);
    	canvas.drawLine(center.x, center.y, cx, cy, paint);
    }
    
	@Override
	protected void onDraw(Canvas canvas){
		drawBackground(canvas);
		drawLines(canvas);
		drawDetectedObject(canvas);
		drawCurrentAngle(canvas);
	}
	
	public OnTouchListener turnRadarOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN)) {
            	UltrasonicSensorView.this.clearRadar();
            	return true;
            }
			return true;
		}
    };
}

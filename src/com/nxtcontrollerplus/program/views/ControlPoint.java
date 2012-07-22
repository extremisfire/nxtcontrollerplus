package com.nxtcontrollerplus.program.views;

import com.nxtcontrollerplus.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class ControlPoint extends View{
	public final int radius = 32;
	private Point center;
	private byte leftMotorSpeed,rightMotorSpeed;
	private Bitmap bitmap;
	private ControlPad controlPad;
	private double oneDegree;
	
	public Point getCenter() {
		return center;
	}

	public void setOneDegree(double oneDegree) {
		this.oneDegree = oneDegree;
	}

	public byte getLeftMotorSpeed() {
		return leftMotorSpeed;
	}

	public byte getRightMotorSpeed() {
		return rightMotorSpeed;
	}

	public void setControlPoint(int cx, int cy) {		
		double distFromCenter = distanceBetweenTwoPoints(controlPad.getCenter(),new Point(cx,cy));
		if(distFromCenter > controlPad.getRadius()) return;
		this.center.x = cx;
		this.center.y = cy;
		
		double temp = distFromCenter/this.oneDegree;
    	byte speed = (byte) ((byte) Math.round(temp)*2);
    	
    	if(center.y > controlPad.getCenter().y){
    		speed *= -1;
    	}
    	
    	double hypotenusSize = hypotenuse(center);
    	double sinangle = (hypotenusSize/distFromCenter);
    	
    	if(center.x < controlPad.getCenter().x){
    		leftMotorSpeed = (byte) (speed*sinangle);
    		rightMotorSpeed = speed;
    	}else if(center.x >= controlPad.getCenter().x){
    		rightMotorSpeed = (byte) (speed*sinangle);
    		leftMotorSpeed = speed;
    	}
	}
	
    public double distanceBetweenTwoPoints(Point a, Point b){
    	double result = 0;
    	int dx = (b.x - a.x);
    	int dy = (b.y - a.y);
    	result = Math.sqrt(dx*dx + dy*dy);
    	return result;
    }
    
    private double hypotenuse(Point touchPoint){
    	Point a = new Point(controlPad.getCenter().x,touchPoint.y);
    	return distanceBetweenTwoPoints(a, controlPad.getCenter());
    }

	public ControlPoint(Context context,int cx, int cy, ControlPad controlPad){
		super(context);
		this.controlPad = controlPad;
		Bitmap temp =  BitmapFactory.decodeResource(context.getResources(), R.drawable.nxt_robot);
		this.bitmap = Bitmap.createScaledBitmap(temp, radius*2, radius*2, true);
		temp.recycle();
		center = new Point(cx,cy);
		leftMotorSpeed = rightMotorSpeed = 0;
	}
	
	@Override
	protected void onDraw (Canvas canvas){
		canvas.drawBitmap(bitmap, center.x-radius, center.y-radius, null);		
	}
}

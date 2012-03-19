package nxtcontroller.program;

import nxtcontroller.activity.MainActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

public class ControlPad extends View implements SensorEventListener{
	
	public static final int degreesCount = 50;
	private static final int delay = SensorManager.SENSOR_DELAY_UI;

	/* private class properties declaration */
	private int width,radius;
	private Point center;
	private final int color = Color.argb(128, 255, 255, 255);
	private final int circleOffSet = 2;
	private Paint paint;
	private ControlPoint controlPoint;
	private NXTCommunicator nxtCommnunicator = null;
	@SuppressWarnings("unused")
	private Display display;

	private float[] tilt_data = {0, 0, 0}, gravity = {0, 0, 0}, magnet = {0, 0, 0};
	private SensorManager manager;
	private Sensor magnetic,accelerometer;
	
	/* Getters and Setter declaration */
    public void setNxtCommnunicator(NXTCommunicator nxtCommnunicator) {
		this.nxtCommnunicator = nxtCommnunicator;
	}

	public Point getCenter() {
		return center;
	}

	public int getRadius() {
		return radius;
	}

	public ControlPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        center = new Point();
        paint = new Paint();
        controlPoint = new ControlPoint(context, center.x, center.y,this);
        WindowManager windowManager = (WindowManager) context.getSystemService(MainActivity.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
	
	private float[] getTiltValues() {
		float[] R={0,0,0,0,0,0,0,0,0};
		if(SensorManager.getRotationMatrix(R, null, gravity, magnet)){
			SensorManager.getOrientation(R, tilt_data);
		}
		float[] values = new float[3];
	    System.arraycopy(tilt_data, 0, values, 0, 3);
	    return values;
	}
	
	public void turnOnTiltControl(){
	    if( manager.registerListener(this, magnetic, ControlPad.delay) && manager.registerListener(this, accelerometer, ControlPad.delay) ) {
	           Log.d(MainActivity.TAG, "accelerometer+magnetic successfully register");
	    }else {
	      Log.d("TiltCalc", "No acceptable hardware found.");
	      manager.unregisterListener(this);
	   }
	}
	
	public void turnOffTiltControl(){
		manager.unregisterListener(this);
	}
	
	public void turnOnTouchControl(){
		this.setOnTouchListener(TouchPadControlOnTouchListener);
	}
	
	public void turnOffTouchControl(){
		this.setOnTouchListener(null);
	}

	@Override
	protected void onDraw (Canvas canvas){
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(center.x, center.y, radius, paint);
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawCircle(center.x, center.y, radius-circleOffSet, paint);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(2.0f);
		canvas.drawLine(center.x-radius, center.y, center.x+radius, center.y, paint);
		canvas.drawLine(center.x, center.y-radius, center.x, center.y+radius, paint);
		controlPoint.onDraw(canvas);
	}
	
    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        this.width = width-controlPoint.radius;
        this.radius = this.width/2;
        this.center.x = width / 2;
        this.center.y = height / 2;
        controlPoint.setCenter( this.center.x, this.center.y);
        double oneDegree = (double)radius / (double)degreesCount;
        controlPoint.setOneDegree(oneDegree);
    }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	
	}

    @Override
	public void onSensorChanged(SensorEvent event) {
        
		final float[] vals = event.values; 
        final float[] target;
        
        target = (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? gravity : magnet;

        System.arraycopy(vals, 0, target, 0, 3);   
        /*Log.d(MainActivity.TAG,"X: "+Math.round(getTiltValues()[0]*100)/(float)100);
        Log.d(MainActivity.TAG,"Y: "+Math.round(getTiltValues()[1]*100)/(float)100);
        Log.d(MainActivity.TAG,"Z: "+Math.round(getTiltValues()[2]*100)/(float)100);
    	*/
        tiltMoves(getTiltValues()[1], getTiltValues()[2]);
	}
    
    public void tiltMoves(float axisY, float axisZ){
        axisY = Math.round(getTiltValues()[1]*100)/(float)100;
    	axisZ = Math.round(getTiltValues()[2]*100)/(float)100;
    	
    	if(axisY > 1) axisY = 1;
    	if(axisZ > 1) axisZ = 1;
    	if(axisY < -1) axisY = -1;
    	if(axisZ < -1) axisZ = -1;
    	
    	byte leftSpeed = 0,rightSpeed = 0;
    	if(axisY < 0){//tilt right	
        	if(axisZ < 0){ //backward
        		leftSpeed = (byte) ((axisZ*100) - Math.abs((axisY*100)));
        		rightSpeed = (byte) (axisZ*100);
        	}else{ //forward
        		leftSpeed = (byte) (axisZ*100);
        		rightSpeed = (byte) ((axisZ*100) - Math.abs((axisY*100)));
        	}
    	}else if(axisY >= 0){ //tilt left
        	if(axisZ < 0){ //backward
        		leftSpeed = (byte) (axisZ*100);
        		rightSpeed = (byte) ((axisZ*100) - Math.abs((axisY*100)));
        	}else{ //forward
        		leftSpeed = (byte) ((axisZ*100) - Math.abs((axisY*100)));
        		rightSpeed = (byte) (axisZ*100);
        	}
    	}
    	Log.d(MainActivity.TAG,"Lspeed:"+Byte.toString(leftSpeed));
    	Log.d(MainActivity.TAG,"Rspeed:"+Byte.toString(rightSpeed));
    	
    	nxtCommnunicator.move2Motors(leftSpeed, rightSpeed);
    }

    
	public OnTouchListener TouchPadControlOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if ((action == MotionEvent.ACTION_DOWN) || (action == MotionEvent.ACTION_MOVE)) {
            	controlPoint.setCenter(x,y);
            	
            	byte leftSpeed = controlPoint.getLeftMotorSpeed();
            	byte rightSpeed = controlPoint.getRightMotorSpeed();
            	
            	Log.d(MainActivity.TAG,"Lspeed:"+Byte.toString(leftSpeed));
            	Log.d(MainActivity.TAG,"Rspeed:"+Byte.toString(rightSpeed));
            	
            	nxtCommnunicator.move2Motors(leftSpeed, rightSpeed);	
            }else if((action == MotionEvent.ACTION_UP) || (action == MotionEvent.ACTION_CANCEL)){
            	
            	controlPoint.setCenter(center.x,center.y);
            	nxtCommnunicator.stopMove();
            }
            ControlPad.this.postInvalidate();
			return true;
		}
    };
}

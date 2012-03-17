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
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

public class ControlPad extends View implements SensorEventListener{
	
	public static final int degreesCount = 50;

	/* private class properties declaration */
	private int width,radius;
	private Point center;
	private final int color = Color.argb(128, 255, 255, 255);
	private final int circleOffSet = 2;
	private Paint paint;
	private ControlPoint controlPoint;
	private NXTCommunicator nxtCommnunicator = null;
	private Display display;
	
	/* Getters and Setter declaration */
    public void setNxtCommnunicator(NXTCommunicator nxtCommnunicator) {
		this.nxtCommnunicator = nxtCommnunicator;
	}

	public Point getCenter() {
		return center;
	}

	public ControlPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        center = new Point();
        paint = new Paint();
        controlPoint = new ControlPoint(context, center.x, center.y,this);
        WindowManager windowManager = (WindowManager) context.getSystemService(MainActivity.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }
	
	public void turnOnListener(){
		this.setOnTouchListener(TouchPadControlOnTouchListener);
	}
	
	public void turnOffListener(){
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
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			//setAxisX(-event.values[0]);
			//setAxisY(event.values[1]);
			break;
		case Surface.ROTATION_90:
			//setAxisX(event.values[1]);
			//setAxisY(event.values[0]);
			break;
		case Surface.ROTATION_180:
			//setAxisX(event.values[0]);
			//setAxisY(-event.values[1]);
			break;
		case Surface.ROTATION_270:
			//setAxisX(-event.values[1]);
			//setAxisY(-event.values[0]);
			break;
		}
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

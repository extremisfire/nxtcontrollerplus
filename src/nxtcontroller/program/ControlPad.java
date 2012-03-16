package nxtcontroller.program;

import nxtcontroller.activity.MainActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

public class ControlPad extends View {
	
	public static final int degreesCount = 50;

	/* private class properties declaration */
	private int width,radius;
	private Point center;
	private final int color = Color.argb(128, 255, 255, 255);
	private final int circleOffSet = 2;
	private Paint paint;
	private ControlPoint controlPoint;
	private double oneDegree;
	private NXTCommunicator nxtCommnunicator = null;
	
	/* Getters and Setter declaration */
    public void setNxtCommnunicator(NXTCommunicator nxtCommnunicator) {
		this.nxtCommnunicator = nxtCommnunicator;
	}

	public ControlPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        center = new Point();
        paint = new Paint();
        controlPoint = new ControlPoint(context, center.x, center.y);
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
        this.oneDegree = (double)radius / (double)degreesCount;
    }
    
    public double distanceBetweenTwoPoints(Point a, Point b){
    	double result = 0;
    	int dx = (b.x - a.x);
    	int dy = (b.y - a.y);
    	result = Math.sqrt(dx*dx + dy*dy);
    	return result;
    }
    
    public double hypotenuse(Point touchPoint){
    	Point a = new Point(center.x,touchPoint.y);
    	return distanceBetweenTwoPoints(a, center);
    }
    
	public OnTouchListener TouchPadControlOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if ((action == MotionEvent.ACTION_DOWN) || (action == MotionEvent.ACTION_MOVE)) {
            	controlPoint.setCenter(x,y);
            	double distFromCenter = distanceBetweenTwoPoints(new Point(center.x,center.y),new Point(x,y));
            	if(distFromCenter > radius) return false;
            	
            	double temp = distFromCenter/oneDegree;
            	byte speed = (byte) ((byte) Math.round(temp)*2);
            	
            	if(y>center.y){
            		speed *= -1;
            	}
            	
            	double angle = hypotenuse(new Point(x,y));
            	double sinangle = (angle/distFromCenter);
            	
            	Log.d(MainActivity.TAG,Byte.toString(speed));
            	if(x < center.x){
            		byte leftSpeed = (byte) (speed*sinangle);
            		Log.d(MainActivity.TAG,"Lspeed:"+Byte.toString(leftSpeed));
            		nxtCommnunicator.move2Motors(leftSpeed, (byte) (speed));
            	}else if(x >= center.x){
            		byte rightSpeed = (byte) (speed*sinangle);
            		Log.d(MainActivity.TAG,"Rspeed:"+Byte.toString(rightSpeed));
            		nxtCommnunicator.move2Motors((byte) (speed), rightSpeed);
            	}
            	invalidate();
            }else if((action == MotionEvent.ACTION_UP) || (action == MotionEvent.ACTION_CANCEL)){
            	controlPoint.setCenter(center.x,center.y);
            	nxtCommnunicator.stopMove();
            }
            ControlPad.this.postInvalidate();
			return true;
		}
    };
}

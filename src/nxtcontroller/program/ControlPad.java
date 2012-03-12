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
    
    public double distanceFromCenter(){
    	double result = 0;
    	int dx = (controlPoint.getCenter().x - center.x);
    	int dy = (controlPoint.getCenter().y - center.y);
    	result = Math.sqrt(dx*dx + dy*dy);
    	return result;
    }
    
    public double angle(Point touchPoint){
    	Point v1 = new Point();
    	Point v2 = new Point();
    	Point end = new Point(center.x+radius,center.y);
    	v1.x = end.x - center.x;
    	v1.y = end.y - center.y;
    	v2.x = touchPoint.x - center.x;
    	v2.y = touchPoint.y - center.y;
    	double cosa = (v1.x*v2.x + v1.y*v2.y)/(Math.sqrt(v1.x*v1.x + v1.y*v1.y) * Math.sqrt(v2.x*v2.x + v2.y*v2.y));
    	//return Math.acos(cosa);
    	return Math.round(cosa*100)/2;
    }
    
	public OnTouchListener TouchPadControlOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if ((action == MotionEvent.ACTION_DOWN) || (action == MotionEvent.ACTION_MOVE)) {
            	controlPoint.setCenter(x,y);
            	if(distanceFromCenter() > radius) return false;
            	double temp = distanceFromCenter()/oneDegree;
            	byte speed = (byte) ((byte) Math.round(temp)*2);
            	if(y>center.y){
            		speed *= -1;
            	}
            	
            	double angle = angle(new Point(x,y));
            	Log.d(MainActivity.TAG,"uhol:"+Double.toString(angle));
            	//Log.d(MainActivity.TAG,Byte.toString(speed));
            	
            	nxtCommnunicator.move2Motors(speed, (byte) (speed-angle));
            	invalidate();
            }else if(action == MotionEvent.ACTION_UP){
            	controlPoint.setCenter(center.x,center.y);
            	nxtCommnunicator.stopMove();
            }
            ControlPad.this.postInvalidate();
			return true;
		}
    };
}

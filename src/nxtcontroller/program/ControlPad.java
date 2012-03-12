package nxtcontroller.program;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
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
        this.width = width;
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
            	nxtCommnunicator.move2Motors((byte)0, (byte) speed, (byte) 1, speed);
            	invalidate();
            }else if(action == MotionEvent.ACTION_UP){
            	controlPoint.setCenter(center.x,center.y);
            	byte speed=0;
            	nxtCommnunicator.move2Motors((byte)0, (byte) speed, (byte) 1, speed);
            }
            ControlPad.this.postInvalidate();
			return true;
		}
    };
}

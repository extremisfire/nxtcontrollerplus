package nxtcontroller.program.views;

import nxtcontroller.program.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class UltrasonicSensorView extends SensorView{

	private final int STROKE_WIDTH = 2;
	
	private Paint paint = null;
	private TextView distanceLabel = null;
	private String distanceText = null;
	private int width,radius;
	private Point center = null;
	private final int bgColor = Color.argb(128, 0, 105, 0);
	private final int fgColor = Color.parseColor("#25d626");
	
	public TextView getDistanceLabel() {
		return distanceLabel;
	}

	public void setDistanceLabel(TextView distanceLabel) {
		this.distanceLabel = distanceLabel;
	}

	private void setUpComponents(Context context){
		distanceText = context.getResources().getString(R.string.distanceText);
		center = new Point();
		paint = new Paint();
	}
	
	public UltrasonicSensorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUpComponents(context);
		this.setOnTouchListener(turnRadarOnTouchListener);
	}
	
    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        this.width = width;
        this.radius = this.width/2;
        this.center.x = width / 2;
        this.center.y = height / 2;
    }
	
    private void drawBackground(Canvas canvas){
    	paint.setStyle(Style.FILL);
    	paint.setColor(this.bgColor);
    	canvas.drawCircle(center.x, center.y, radius, paint);
    }
    
    private void drawLines(Canvas canvas){
    	paint.setStyle(Style.STROKE);
    	paint.setStrokeWidth(STROKE_WIDTH);
    	paint.setColor(this.fgColor);
    	canvas.drawCircle(center.x, center.y, (float) (radius*1.0), paint);
    	canvas.drawCircle(center.x, center.y, (float) (radius*0.66), paint);
    	canvas.drawCircle(center.x, center.y, (float) (radius*0.33), paint);
		canvas.drawLine(center.x-radius, center.y, center.x+radius, center.y, paint);
		canvas.drawLine(center.x, center.y-radius, center.x, center.y+radius, paint);
    }
    
	@Override
	protected void onDraw (Canvas canvas){
		int distance = getSensorValue();
		drawBackground(canvas);
		drawLines(canvas);
		if(getDistanceLabel() != null){
			String temp = this.distanceText+" ";
			temp += distance + " cm";
			getDistanceLabel().setText(temp);
		}
	}
	
	public OnTouchListener turnRadarOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN)) {
            	//TODO
            }
			return true;
		}
    };
}

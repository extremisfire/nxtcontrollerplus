package nxtcontroller.program.views;

import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.R;
import nxtcontroller.program.sensors.LightSensor;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

public class LightSensorView extends SensorView{
	
	private final int WIDTH = 120;
	private final int HEIGHT = 80;
	private LightSensor pairedSensor = null;
	
	public LightSensor getPairedSensor() {
		return pairedSensor;
	}

	public LightSensorView(Context context,LightSensor pairedSensor) {
		super(context);
		this.setMeasuredDimension(WIDTH, HEIGHT);
		this.setOnTouchListener(changeModeOnTouchListener);
		this.pairedSensor = pairedSensor;
	}
	
	@Override
	protected void onDraw (Canvas canvas){
		Paint paint = new Paint();
		paint.setStyle(Style.FILL); 
		int value = getSensorValue();
		float[] hsv = new float[3];
		hsv[0] = (float) 203.9;
		hsv[1] = 0;
		hsv[2] = (float)(value/(float)100);
		paint.setColor(Color.HSVToColor(hsv));
		canvas.drawRect(0, 0, WIDTH, HEIGHT, paint);
		
		paint.setTextSize(20);
		paint.setColor(Color.WHITE);
		String text = "";
		if(getPairedSensor().getType() == SensorType.LIGHT_INACTIVE){
			text = getResources().getString(R.string.ledOFFLabel);
		}else{
			text = getResources().getString(R.string.ledONLabel);
		}
		canvas.drawText(text, (WIDTH/2-35), (HEIGHT/2+10), paint);
		text = Integer.toString(value)+" % ";
		canvas.drawText(text, WIDTH+20, (HEIGHT/2+10), paint);
	}
	
	public OnTouchListener changeModeOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN)) {
            	LightSensor sensor = getPairedSensor();
            	if(sensor.getType() == SensorType.LIGHT_ACTIVE){
            		sensor.setAmbientMode();
            	}else{
            		sensor.setLightReflectionMode();
            	}
            	sensor.initialize();
            }
			return true;
		}
    };
}

package com.nxtcontrollerplus.program.views;

import com.nxtcontrollerplus.R;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorType;
import com.nxtcontrollerplus.program.sensors.LightSensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class LightSensorView extends SensorView{
	
	private String lightActiveText, lightInActiveText;
	
	public LightSensorView(Context context) {
		super(context);
		this.lightActiveText = context.getResources().getString(R.string.lightActiveText);
		this.lightInActiveText = context.getResources().getString(R.string.lightInActiveText);
		this.setOnTouchListener(changeModeOnTouchListener);
	}
	
	@Override
	protected void onDraw (Canvas canvas){
		final int OFFSET = 3; 
		Paint paint = new Paint();
		//stroke
		paint.setColor(Color.WHITE); 
		canvas.drawRect(0, 0, super.getWidth(), super.getHeight(), paint);
		//rectangle
		int value = getSensorValue();
		float[] hsv = new float[3];
		hsv[0] = (float) 203.9;
		hsv[1] = 0;
		hsv[2] = (float)(value/(float)100);
		paint.setColor(Color.HSVToColor(hsv));
		canvas.drawRect(0 + OFFSET, 0 + OFFSET, super.getWidth() - OFFSET, super.getHeight() - OFFSET, paint);
	}
	
	public OnTouchListener changeModeOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN)) {
            	LightSensor sensor = (LightSensor)getPairedSensor();
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
    
	public String toString(){
		String result = Byte.toString((byte) (getPairedSensor().getPort()+1));
		result += ": ";
		result += getSensorName();
		result += "\n";
		LightSensor sensor = (LightSensor)getPairedSensor();
		result += (sensor.getType() == SensorType.LIGHT_ACTIVE) ? lightActiveText : lightInActiveText;
		result += ": " + sensor.toString();
		return result;
	}
}

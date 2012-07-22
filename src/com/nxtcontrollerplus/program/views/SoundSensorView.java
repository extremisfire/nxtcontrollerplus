package com.nxtcontrollerplus.program.views;

import com.nxtcontrollerplus.R;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorType;
import com.nxtcontrollerplus.program.sensors.SoundSensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class SoundSensorView extends SensorView{
	
	private int percentToPixel = 0;
	private int width, height;
	private Bitmap soundLevel;
	private String loudnessText;

	public SoundSensorView(Context context) {
		super(context);
		this.loudnessText = context.getResources().getString(R.string.loudnessText);
		this.setOnTouchListener(changeModeOnTouchListener);
		this.soundLevel=  BitmapFactory.decodeResource(context.getResources(), R.drawable.sound_level);
	}
	
    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        this.width = width;
        this.height = height;
        this.percentToPixel = (this.width / 100);
        Bitmap temp = Bitmap.createScaledBitmap(soundLevel, this.width, this.height, true);
        this.soundLevel = temp;
    }
	
	@Override
	protected void onDraw(Canvas canvas){
		int loudness = getPairedSensor().getMeasuredData();
		int offsetX = loudness * percentToPixel;
		Rect src = new Rect(0, 0, offsetX+15, height); 
		Rect dst = new Rect(0, 0, offsetX+15, height); 
		canvas.drawBitmap(this.soundLevel, src, dst, null); 
	}
	
	public OnTouchListener changeModeOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN)) {
            	SoundSensor sensor = (SoundSensor)getPairedSensor();
            	if(sensor.getType() == SensorType.SOUND_DB){
            		sensor.setDBAMode();
            	}else{
            		sensor.setDBMode();
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
		SoundSensor sensor = (SoundSensor)getPairedSensor();
		result += loudnessText + ": " + sensor.toString();
		return result;
	}
}

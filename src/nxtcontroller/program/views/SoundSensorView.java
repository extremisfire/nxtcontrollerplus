package nxtcontroller.program.views;

import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.R;
import nxtcontroller.program.sensors.SoundSensor;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class SoundSensorView extends SensorView{
	
	private final int WIDTH = 200;
	private final int HEIGHT = 50;
	
	private Bitmap slider;
	private Bitmap soundLevel;
	private SoundSensor pairedSensor = null;
	
	public SoundSensor getPairedSensor() {
		return pairedSensor;
	}

	public SoundSensorView(Context context,SoundSensor pairedSensor) {
		super(context);
		this.pairedSensor = pairedSensor;
		this.setOnTouchListener(changeModeOnTouchListener);
		slider=  BitmapFactory.decodeResource(context.getResources(), R.drawable.sound_level_slider);
		soundLevel=  BitmapFactory.decodeResource(context.getResources(), R.drawable.sound_level);
		this.setMeasuredDimension(WIDTH, HEIGHT);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		canvas.drawBitmap(soundLevel, 0, 0, null);
		int offsetX = getSensorValue()*2;
		canvas.drawBitmap(slider, offsetX, 0, null);
		Paint paint = new Paint();
		paint.setTextSize(20);
		paint.setColor(Color.WHITE);
		String text = Integer.toString(getSensorValue())+" % ";
		text += (getPairedSensor().getType() == SensorType.SOUND_DB) ? "dB" : "dBA";
		canvas.drawText(text, (WIDTH/2)-20, (HEIGHT/2+5), paint);
	}
	
	public OnTouchListener changeModeOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN)) {
            	SoundSensor sensor = getPairedSensor();
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
}

package com.nxtcontrollerplus.program.views;

import com.nxtcontrollerplus.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class TouchSensorView extends SensorView{
		
	private Bitmap touchONbmp = null, touchOFFbmp = null;
	private int width, height;
	private String touchedText, notTouchedText;

	public TouchSensorView(Context context) {
		super(context);
		this.touchedText = getResources().getString(R.string.touchedText);
		this.notTouchedText = getResources().getString(R.string.notTouchedText);
		this.touchONbmp =  BitmapFactory.decodeResource(context.getResources(), R.drawable.touch_on);
		this.touchOFFbmp =  BitmapFactory.decodeResource(context.getResources(), R.drawable.touch_off);
	}

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        this.width = width;
        this.height = height;
        
		Bitmap temp = Bitmap.createScaledBitmap(this.touchONbmp, this.width, this.height, true);
		this.touchONbmp = temp;
		temp =  Bitmap.createScaledBitmap(this.touchOFFbmp, this.width, this.height , true);
		this.touchOFFbmp = temp;
    }
	
	@Override
	protected void onDraw (Canvas canvas){
		if(getSensorValue() == 1){
			canvas.drawBitmap(touchONbmp, 0, 0, null);
		}else{
			canvas.drawBitmap(touchOFFbmp, 0, 0, null);
		}
	}
	
	public String toString(){
		String result = Byte.toString((byte) (getPairedSensor().getPort()+1));
		result += ": ";
		result += getSensorName();
		result += "\n";
		result += (getSensorValue() == 1) ? touchedText : notTouchedText;
		return result;
	}

}

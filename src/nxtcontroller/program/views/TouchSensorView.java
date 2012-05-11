package nxtcontroller.program.views;

import nxtcontroller.program.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class TouchSensorView extends SensorView{
	
	private final int WIDTH = 128;
	private final int HEIGHT = 80;
	
	private Bitmap touchONbmp = null, touchOFFbmp = null;

	public TouchSensorView(Context context) {
		super(context);
		Bitmap temp =  BitmapFactory.decodeResource(context.getResources(), R.drawable.touch_on);
		this.touchONbmp = Bitmap.createScaledBitmap(temp, WIDTH, HEIGHT, true);
		temp =  BitmapFactory.decodeResource(context.getResources(), R.drawable.touch_off);
		this.touchOFFbmp =  Bitmap.createScaledBitmap(temp, WIDTH, HEIGHT, true);
		temp.recycle();
		this.setMeasuredDimension(WIDTH, HEIGHT);
	}
	
	@Override
	protected void onDraw (Canvas canvas){
		if(getSensorValue() == 1){
			canvas.drawBitmap(touchONbmp, 0, 0, null);
		}else{
			canvas.drawBitmap(touchOFFbmp, 0, 0, null);
		}
	}

}

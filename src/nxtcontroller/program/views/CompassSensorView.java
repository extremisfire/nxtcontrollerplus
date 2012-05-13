package nxtcontroller.program.views;

import nxtcontroller.program.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.TextView;

public class CompassSensorView extends SensorView{
	
	private Bitmap compassBG = null, compassArrow = null;
	private TextView azimuthLabel = null;
	private String azimuthText = null;
	private Matrix matrix = null;
	
	public TextView getAzimuthLabel() {
		return azimuthLabel;
	}

	public void setAzimuthLabel(TextView azimuthLabel) {
		this.azimuthLabel = azimuthLabel;
	}

	private void setUpComponents(Context context){
		this.compassBG = BitmapFactory.decodeResource(context.getResources(), R.drawable.compass);
		this.compassArrow =  BitmapFactory.decodeResource(context.getResources(), R.drawable.compass_arrow);
		this.azimuthText = context.getResources().getString(R.string.azimuthText);
		this.matrix = new Matrix();
	}
	
	public CompassSensorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUpComponents(context);
	}
	
	@Override
	protected void onDraw (Canvas canvas){
		int azimuth =getSensorValue();
		canvas.drawBitmap(compassBG, 0, 0, null);
		
		int left = getLeft();
		int top = getTop();
		matrix.setTranslate(left,top);
		matrix.postRotate(azimuth, left+(getWidth()/2), top+(getHeight()/2));
		
		canvas.drawBitmap(compassArrow, matrix, null);
		if(getAzimuthLabel() != null){
			String temp = azimuthText+" ";
			temp += azimuth;
			getAzimuthLabel().setText(temp);
		}
	}

}

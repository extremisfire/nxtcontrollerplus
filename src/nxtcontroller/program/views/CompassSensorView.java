package nxtcontroller.program.views;

import nxtcontroller.program.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CompassSensorView extends SensorView{
	
	private Bitmap compassBG = null, compassArrow = null;
	private TextView azimuthLabel = null;
	private String azimuthText = null;
	private Matrix matrix = null;
	private boolean compassMode = false;
	
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
		this.setOnTouchListener(changeModeOnTouchListener);
	}
	
	public CompassSensorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUpComponents(context);
	}
	
	@Override
	protected void onDraw (Canvas canvas){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		int azimuth = getSensorValue();
		int left = getLeft();
		int top = getTop();
		matrix.setTranslate(left,top);
		matrix.postRotate(azimuth, left+(getWidth()/2), top+(getHeight()/2));
		if(compassMode){
			canvas.drawBitmap(compassBG,matrix, paint);
			canvas.drawBitmap(compassArrow, 0, 0,  paint);
		}else{
			canvas.drawBitmap(compassBG, 0, 0, paint);
			canvas.drawBitmap(compassArrow, matrix,  paint);
		}
		if(getAzimuthLabel() != null){
			String temp = azimuthText+" ";
			temp += azimuth;
			getAzimuthLabel().setText(temp);
		}
	}
	
	public OnTouchListener changeModeOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN)) {
            	compassMode = !compassMode;
            }
			return true;
		}
    };

}

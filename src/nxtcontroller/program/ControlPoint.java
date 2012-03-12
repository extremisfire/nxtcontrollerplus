package nxtcontroller.program;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class ControlPoint extends View{
	public final int radius = 32;
	private Point center;
	private Bitmap bitmap;
	
	public Point getCenter() {
		return center;
	}

	public void setCenter(int cx, int cy) {
		this.center.x = cx;
		this.center.y = cy;
		this.postInvalidate();
	}

	public ControlPoint(Context context,int cx, int cy){
		super(context);
		Bitmap temp =  BitmapFactory.decodeResource(context.getResources(), R.drawable.walle);
		this.bitmap = Bitmap.createScaledBitmap(temp, radius*2, radius*2, true);
		center = new Point(cx,cy);
	}
	
	@Override
	protected void onDraw (Canvas canvas){
		canvas.drawBitmap(bitmap, center.x-radius, center.y-radius, null);		
	}
}

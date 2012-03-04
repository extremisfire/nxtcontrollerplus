package nxtcontroller.program;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

public class ControlPoint {
	public final int radius = 32;
	private Point center;
	private Bitmap bitmap;
	
	public Point getCenter() {
		return center;
	}

	public void setCenter(int cx, int cy) {
		this.center.x = cx;
		this.center.y = cy;
	}

	public ControlPoint(Context context,int cx, int cy){
		Bitmap temp =  BitmapFactory.decodeResource(context.getResources(), R.drawable.walle);
		this.bitmap = Bitmap.createScaledBitmap(temp, radius*2, radius*2, true);
		center = new Point(cx,cy);
	}
	
	public void draw(Canvas canvas){
		canvas.drawBitmap(bitmap, center.x-radius, center.y-radius, null);		
	}
}

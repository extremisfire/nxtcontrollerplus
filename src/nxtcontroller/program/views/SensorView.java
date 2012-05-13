package nxtcontroller.program.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SensorView extends View{

	private int sensorValue = 0;
	
	protected int getSensorValue() {
		return sensorValue;
	}

	public void setSensorValue(int sensorValue) {
		this.sensorValue = sensorValue;
	}

	public SensorView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public SensorView(Context context) {
		super(context);
	}
	
	

}

package nxtcontroller.program.views;

import android.content.Context;
import android.view.View;

public abstract class SensorView extends View{

	private int sensorValue = 0;
	
	protected int getSensorValue() {
		return sensorValue;
	}

	public void setSensorValue(int sensorValue) {
		this.sensorValue = sensorValue;
	}

	public SensorView(Context context) {
		super(context);
	}

}

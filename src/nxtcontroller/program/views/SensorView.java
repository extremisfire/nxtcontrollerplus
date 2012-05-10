package nxtcontroller.program.views;

import android.content.Context;
import android.view.View;

public abstract class SensorView extends View{

	protected int value = 0;
	
	public SensorView(Context context) {
		super(context);
	}
	
	public void setValue(int value) {
		this.value = value;
	}

}

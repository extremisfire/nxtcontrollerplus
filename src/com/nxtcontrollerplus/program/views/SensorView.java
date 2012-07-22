package com.nxtcontrollerplus.program.views;

import com.nxtcontrollerplus.R;
import com.nxtcontrollerplus.program.sensors.Sensor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SensorView extends View{

	private int sensorValue = 0;
	private String[] sensorNames;
	private String sensorName;
	private Sensor pairedSensor = null;
	
	public String getSensorName() {
		return sensorName;
	}

	public Sensor getPairedSensor() {
		return pairedSensor;
	}

	public void setPairedSensor(Sensor pairedSensor) {
		this.pairedSensor = pairedSensor;
		this.sensorName = this.sensorNames[pairedSensor.getId()];
	}

	protected int getSensorValue() {
		return sensorValue;
	}

	public void setSensorValue(int sensorValue) {
		this.sensorValue = sensorValue;
	}

	public SensorView(Context context, AttributeSet attrs) {
		super(context,attrs);
		this.sensorNames = context.getResources().getStringArray(R.array.sensorNames);
	}
	
	public SensorView(Context context) {
		super(context);
		this.sensorNames = context.getResources().getStringArray(R.array.sensorNames);
	}
	
}

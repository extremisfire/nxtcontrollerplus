package com.nxtcontrollerplus.program.sensors;

import com.nxtcontrollerplus.enums.nxtbuiltin.SensorID;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorMode;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorType;

public class TouchSensor extends Sensor{
	
	private final int id = SensorID.TOUCH_SENSOR;
	
	public int getId() {
		return id;
	}	

	public boolean isTouched(){
		return (super.getMeasuredData() == 1) ? true : false;
	}
	
	public TouchSensor(byte port) {
		super(port);
		super.type = SensorType.SWITCH;
		super.mode = SensorMode.BOOLEAN_MODE;
	}
	
	@Override
	public String toString(){
		String ret="";
		return ret+Integer.toString(getMeasuredData());
	}

	
}

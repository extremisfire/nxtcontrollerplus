package nxtcontroller.program.sensors;

import nxtcontroller.enums.nxtbuiltin.SensorMode;
import nxtcontroller.enums.nxtbuiltin.SensorType;

public class TouchSensor extends Sensor{

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
		String ret="TOUCH SENSOR: ";
		return ret+Boolean.toString(isTouched());
	}

	
}

package nxtcontroller.program.sensors;

import nxtcontroller.enums.nxtbuiltin.SensorMode;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.NXTCommunicator;

public class TouchSensor extends Sensor{

	public boolean isTouched(){
		return (super.getMeasuredData() == 1) ? true : false;
	}
	
	public TouchSensor(NXTCommunicator nxt, byte port) {
		super(nxt, port);
		super.type = SensorType.SWITCH;
		super.mode = SensorMode.BOOLEAN_MODE;
	}
	
	@Override
	public String toString(){
		String ret="TOUCH SENSOR: ";
		return ret+Boolean.toString(isTouched());
	}

	
}

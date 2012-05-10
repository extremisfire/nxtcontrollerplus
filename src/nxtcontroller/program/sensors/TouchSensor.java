package nxtcontroller.program.sensors;

import nxtcontroller.enums.nxtbuiltin.SensorID;
import nxtcontroller.enums.nxtbuiltin.SensorMode;
import nxtcontroller.enums.nxtbuiltin.SensorType;

public class TouchSensor extends Sensor{
	
	private final int id = SensorID.SOUND_SENSOR;
	
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

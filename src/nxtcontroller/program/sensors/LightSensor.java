package nxtcontroller.program.sensors;

import nxtcontroller.enums.nxtbuiltin.SensorMode;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.NXTCommunicator;

/**
 * The Light Sensor enables the robot to distinguish
	between light and darkness, to read the light intensity
	in a room, and to measure the light intensity on
	colored surfaces
 * @author Lukas Dilik
 *
 */
public class LightSensor extends Sensor{

	public void setAmbientMode(){
		this.type = SensorType.LIGHT_INACTIVE;
	}
	
	public void setLightReflectionMode(){
		this.type = SensorType.LIGHT_ACTIVE;
	}
	
	public LightSensor(NXTCommunicator nxt, byte port) {
		super(nxt, port);
		setLightReflectionMode();
		this.mode = SensorMode.PCT_FULL_SCALE_MODE;
	}
	
	/**
	 * @return reflection from surface in % 
	 * if incorrect mode then return -1
	 */
	public int getReflection(){
		if(this.type == SensorType.LIGHT_INACTIVE)
			return -1;
		else
			return getMeasuredData();
	}
	
	/**
	 * @return intensity of ambient light in room in % 
	 * if incorrect mode then return -1
	 */
	public int getAmbientLight(){
		if(this.type == SensorType.LIGHT_INACTIVE)
			return getMeasuredData();
		else
			return -1;
	}
	
	@Override
	public String toString(){
		String ret="LIGHT SENSOR: ";
		if(this.type == SensorType.LIGHT_INACTIVE){
			return ret+"Ambient: "+Integer.toString(getAmbientLight())+" %" ;
		}else{
			return ret+"Reflected light: "+Integer.toString(getReflection())+" %";
		}
		
	}
}

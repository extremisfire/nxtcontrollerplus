package com.nxtcontrollerplus.program.sensors;

import com.nxtcontrollerplus.enums.nxtbuiltin.SensorID;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorMode;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorType;

/**
 * The Sound Sensor detects the decibel level: the softness or
	loudness of a sound. The Sound Sensor detects both dB and dBA.
	dBA: the sounds human ears are able to hear.
	dB: all actual sound, including sounds too high or low for the
	human ear to hear.
	The Sound Sensor can measure sound pressure levels up
	to 90 dB – about the level of a lawnmower. 
	the percentage [%] of sound the sensor is capable of reading.
	For comparison, 4-5% is like a silent living room and 5-10%
	is about the level of someone talking some distance away.
	From 10-30% is normal conversation close to the sensor or
	music played at a normal level and  30-100% represents a
	range from people shouting to music playing at high volumes. 
	These ranges are assuming a distance of about 1 meter
	between the sound source and the Sound Sensor
 * @author Lukas Dilik
 *
 */
public class SoundSensor extends Sensor{
	
	private final int id = SensorID.SOUND_SENSOR;
	
	public int getId() {
		return id;
	}

	public void setDBAMode(){
		this.type = SensorType.SOUND_DBA;
	}
	
	public void setDBMode(){
		this.type = SensorType.SOUND_DB;
	}
	
	public SoundSensor(byte port) {
		super(port);
		this.mode = SensorMode.PCT_FULL_SCALE_MODE;
		setDBAMode();
	}
	
	@Override
	public String toString() {
		String ret="";
		ret += Integer.toString(super.getMeasuredData()) +" %";
		if(this.type == SensorType.SOUND_DB)
			ret += " [dB]";
		else
			ret += " [dBA]";
		return ret;
	}

}

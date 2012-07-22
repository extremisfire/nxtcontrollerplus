package com.nxtcontrollerplus.program.sensors;

import com.nxtcontrollerplus.activity.MainActivity;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorID;
import com.nxtcontrollerplus.program.btmessages.commands.LSWrite;

import android.util.Log;

public class UltrasonicSensor extends I2CSensor{
	
	private final int id = SensorID.ULTRASONIC_SENSOR;
	
	public int getId() {
		return id;
	}

	public UltrasonicSensor( byte port) {
		super(port);
	}
	
	@Override
	public void initialize(){
		super.initialize();
		Log.d(MainActivity.TAG, "initializing UltraSonic as LS_9V");
	}

	@Override
	public String toString() {
		String ret="ULTRASONIC SENSOR: ";
	    ret += Integer.toString(getMeasuredData())+" cm";
		return ret;
	}

	@Override
	public int getMeasuredData() {
		return (int)(0x000000FF & (int)super.lsdata.getRxData()[0]);
	}

	@Override
	public LSWrite getLSWriteCommand() {
		byte result = 0x42; //adress of register where 1st result is stored
		byte[] tx = {I2CSensor.DEFAULT_REGISTER_ADDRESS, result}; //transmitted data from NXT written in NXT register
		byte recievedDataLength = 0x01; // constant measurement mode is default this means only one value is expected to return
		LSWrite lw = new LSWrite(super.getPort(), tx, recievedDataLength);
		
		return lw;
	}

}

package com.nxtcontrollerplus.program.btmessages.returnpackages;

import com.nxtcontrollerplus.program.utils.Converter;

/**
 * Structure:
 * Byte[0-2] @see ReturnPackage
 * Byte[3] - current voltage of NXT battery in mV
 * this is special return package for GET_BATTERY_LEVEL command
 * this class contains miliVolts 
 * this class count percentage of battery Level
 * @author Lukas Dilik
 * @see ReturnPackage
 */
public class GetBatteryLevelReturnPackage extends ReturnPackage{

	private static final int maxVoltage = 8200; // full battery voltage in mV
	
	private float currentMiliVolts;
	
	public float getBatteryLevel() {
		float temp = ((float)currentMiliVolts/(float)maxVoltage)*100;
		temp = (temp >= 100) ? 100 : temp;
		return temp;
	}

	public GetBatteryLevelReturnPackage(byte[] bytes) {
		super(bytes);
		byte[] b =new byte[2];
		b[0] = super.returnBytes[3];
		b[1] = super.returnBytes[4];
		this.currentMiliVolts = Converter.fromBytes(b);
	}

	public String toString(){
		String temp = super.toString();
		temp += "miliVolts: "+ this.currentMiliVolts +"\n";
		temp += this.getClass().toString()+"_END";
		return temp;
	}
}

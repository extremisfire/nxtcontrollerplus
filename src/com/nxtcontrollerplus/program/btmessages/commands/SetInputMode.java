package com.nxtcontrollerplus.program.btmessages.commands;

import com.nxtcontrollerplus.enums.nxtbuiltin.CommandType;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorMode;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorType;
import com.nxtcontrollerplus.program.utils.Converter;

/**
 * generate a specific byte array command for NXT
 * Command name:  SET_INPUT_MODE 
 * Byte [0-1] @see DirectCommand
 * Byte 2: Input port
 * Byte 3: Sensor Type
 * Byte 4: Sensor Mode
 * @see DirectCommand
 * @author Lukas Dilik
 * 
 * */
public class SetInputMode extends DirectCommandInput{
	
	private static final byte COMMAND_LENGTH = 5;
	
	/**
	 * @param sensorType
	 * @see SensorType
	 */
	public void setSensorType(byte sensorType){
		super.command[3] = sensorType;
		super.refreshCommand();
	}
	
	/**
	 * @param sensorMode
	 * @see SensorMode
	 */
	public void setSensorMode(byte sensorMode){
		super.command[4] = sensorMode;
		super.refreshCommand();
	}
	
	public SetInputMode(byte portNumber,byte sensorType,byte sensorMode) {
		super(COMMAND_LENGTH, CommandType.SET_INPUT_MODE);
		setInputPort(portNumber);
		setSensorType(sensorType);
		setSensorMode(sensorMode);
	}
	
	public SetInputMode(byte portNumber,byte sensorType) {
		super(COMMAND_LENGTH, CommandType.SET_INPUT_MODE);
		setInputPort(portNumber);
		setSensorType(sensorType);
		setSensorMode(SensorMode.RAW_MODE);
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "SET_INPUT_MODE_START\n";
		temp += Converter.bytesToString(bytes)+"\n";
		temp += "SET_INPUT_MODE_END\n";
		return temp;
	}
	
}

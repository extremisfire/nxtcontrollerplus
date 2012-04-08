package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.SensorMode;;

public class SetInputMode extends DirectCommand{
	
	private static final byte COMMAND_LENGTH = 4;
	
	/**
	 * port where sensor is connected
	 * @param portNumber [0..3]
	 */
	public void setInputPort(byte portNumber){
		super.command[2] = portNumber;
		super.refreshCommand();
	}
	
	public void setSensorType(byte sensorType){
		super.command[3] = sensorType;
		super.refreshCommand();
	}
	
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
	
}

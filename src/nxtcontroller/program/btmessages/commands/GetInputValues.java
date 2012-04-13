package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.program.utils.Converter;

/**
 * generate a byte array command for NXT
 * Command name:  GET_INPUT_VALUES 
 * Byte [0-1] @see DirectCommand
 * Byte [2] - Input port
 * @author Lukas Dilik
 *
 */
public class GetInputValues extends DirectCommand{

	private static final byte COMMAND_LENGTH = 3;
	
	/**
	 * port where sensor is connected
	 * @param portNumber [0..3], on NXT: [portNumber+1]
	 */
	public void setInputPort(byte portNumber){
		super.command[2] = portNumber;
		super.refreshCommand();
	}
	
	public GetInputValues(byte portNumber) {
		super(COMMAND_LENGTH, CommandType.GET_INPUT_VALUES);
		setRequireResponseToOn();
		setInputPort(portNumber);
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "GET_INPUT_VALUES_START\n";
		temp += Converter.bytesToString(bytes)+"\n";
		temp += "GET_INPUT_VALUES_END\n";
		return temp;
	}
}

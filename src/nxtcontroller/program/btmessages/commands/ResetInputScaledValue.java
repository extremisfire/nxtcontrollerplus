package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.program.utils.Converter;

/**
 * generate a byte array command for NXT
 * Command name:  RESET_INPUT_SCALED_VALUES 
 * Byte [0-1] @see DirectCommand
 * Byte [2] - Input port
 * @author Lukas Dilik
 *
 */
public class ResetInputScaledValue  extends DirectCommandInput{

	private final static byte COMMAND_LENGTH = 3;
	
	public ResetInputScaledValue(byte portNumber) {
		super(COMMAND_LENGTH, CommandType.RESET_INPUT_SCALED_VALUE);
		setInputPort(portNumber);
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "RESET_INPUT_SCALED_VALUES_START\n";
		temp += Converter.bytesToString(bytes)+"\n";
		temp += "RESET_INPUT_SCALED_VALUES_END\n";
		return temp;
	}

}

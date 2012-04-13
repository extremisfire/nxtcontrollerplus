package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.program.utils.Converter;

/**
 * generate a specific byte array command for NXT
 * Command name:  GET_BATTERY_LEVEL 
 * Byte [0-1] @see DirectCommand
 * @author Lukas Dilik
 *
 */
public class GetBatteryLevel extends DirectCommand{
	
	private final static byte COMMAND_LENGTH = 2;

	public GetBatteryLevel(){
		super(COMMAND_LENGTH,CommandType.GET_BATTERY_LEVEL);
		super.setRequireResponseToOn();
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "GET_BATTERY_LEVEL"; 
		temp +=  Converter.bytesToString(bytes)+"\n";
		temp += "GET_BATTERY_LEVEL_END\n";
		return temp;
	}

}

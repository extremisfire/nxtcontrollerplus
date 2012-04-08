package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.program.utils.Converter;

public class GetBatteryLevel extends DirectCommand{
	
	private final static byte COMMAND_LENGTH = 2;

	public GetBatteryLevel(){
		super(COMMAND_LENGTH,CommandType.GET_BATTERY_LEVEL);
		super.setRequireResponseToOn();
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "GET_BATTERY_LEVEL" + Converter.bytesToString(bytes)+"\n";
		temp += "GET_BATTERY_LEVEL_END\n";
		return temp;
	}

}

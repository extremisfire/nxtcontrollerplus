package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.TelegramType;
import nxtcontroller.program.btmessages.BluetoothMessage;
import nxtcontroller.program.utils.Converter;

public class GetBatteryLevel extends BluetoothMessage{
	
	private final static byte COMMAND_LENGTH = 13;
	private byte[] command;

	public GetBatteryLevel(){
		super(COMMAND_LENGTH);
		command = new byte[COMMAND_LENGTH];
		command[0] = TelegramType.DIRECT_COMMAND_RRQ;
		command[1] = CommandType.GET_BATTERY_LEVEL;
		appendCommand(command);
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "GET_BATTERY_LEVEL" + Converter.bytesToString(bytes)+"\n";
		temp += "GET_BATTERY_LEVEL_END\n";
		return temp;
	}

}

package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.TelegramType;
import nxtcontroller.program.btmessages.BluetoothMessage;

public class DirectCommand extends BluetoothMessage{

	/**
	 * this contains command byte array must initialized
	 */
	protected byte[] command = null;
	
	/**
	 * set first command byte to 0x80
	 * this indicates that BlueTooth message 
	 * do not require response from NXT
	 */
	private void setRequireResponseToOff(){
		command[0] = TelegramType.DIRECT_COMMAND_NORRQ;
		super.appendCommand(command);
	}
	
	/**
	 * set a byte value command type
	 * @param commandType 
	 * see CommandType
	 */
	private void setCommandType(byte commandType){
		command[1] = commandType;
		super.appendCommand(command);
	}
	
	/**
	 * creates a direct command byte array
	 * default telegram type is no response required
	 * @param commandLength
	 * @param commandType
	 * @see CommandType
	 */
	protected DirectCommand(byte commandLength,byte commandType) {
		super(commandLength);
		command = new byte[commandLength];
		setRequireResponseToOff();
		setCommandType(commandType);
	}

	
	/**
	 * set first command byte to 0x00
	 * this indicates that BlueTooth message 
	 * requires a response from NXT
	 */
	protected void setRequireResponseToOn(){
		command[0] = TelegramType.DIRECT_COMMAND_RRQ;
		super.appendCommand(command);
	}
	
	/**
	 * we must call this every when set something via setters in command byte array defined here
	 * @see BluetoothMessage
	 */
	protected void refreshCommand(){
		super.appendCommand(this.command);
	}

}

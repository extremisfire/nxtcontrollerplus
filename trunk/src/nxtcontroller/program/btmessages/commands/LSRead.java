package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;

public class LSRead extends DirectCommandInput{

	private final static byte COMMAND_LENGTH = 3;
	
	public LSRead(byte portNumber) {
		super(COMMAND_LENGTH, CommandType.LS_READ);
		setRequireResponseToOn();
		setInputPort(portNumber);
	}

}

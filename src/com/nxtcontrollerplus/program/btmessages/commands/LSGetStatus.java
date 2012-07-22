package com.nxtcontrollerplus.program.btmessages.commands;

import com.nxtcontrollerplus.enums.nxtbuiltin.CommandType;

public class LSGetStatus extends DirectCommandInput{

	private final static byte COMMAND_LENGTH = 3;
	
	public LSGetStatus(byte portNumber) {
		super(COMMAND_LENGTH, CommandType.LS_GET_STATUS);
		super.setRequireResponseToOn();
		super.setInputPort(portNumber);
	}

}

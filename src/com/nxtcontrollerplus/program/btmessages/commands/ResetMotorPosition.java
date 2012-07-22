package com.nxtcontrollerplus.program.btmessages.commands;

import com.nxtcontrollerplus.enums.nxtbuiltin.CommandType;

public class ResetMotorPosition extends DirectCommandOutput{

	private final static byte COMMAND_LENGTH = 4;

	public void setRelative(boolean enabled){
		super.command[3] = (byte) ((enabled) ? 1 : 0);
	}
	
	public ResetMotorPosition(byte portNumber) {
		super(COMMAND_LENGTH, CommandType.RESET_MOTOR_POSTITION);
		setOutputPort(portNumber);
		setRelative(false);
	}

}

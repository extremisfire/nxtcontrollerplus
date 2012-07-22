package com.nxtcontrollerplus.program.btmessages.commands;

public abstract class DirectCommandInput extends DirectCommand{

	/**
	 * port where sensor is connected
	 * @param portNumber [0..3], on NXT: [portNumber+1]
	 */
	protected void setInputPort(byte portNumber) throws UnsupportedOperationException{
		super.command[2] = portNumber;
		super.refreshCommand();
	}
	
	protected DirectCommandInput(byte commandLength, byte commandType) {
		super(commandLength, commandType);
	}
	
	public String toString(){
		String temp = super.toString()+"\n";
		temp += "Input port: "+Integer.toString(command[2])+"\n";
		return temp;
	}
}

package com.nxtcontrollerplus.program.btmessages.commands;

import com.nxtcontrollerplus.enums.nxtbuiltin.CommandType;
import com.nxtcontrollerplus.program.utils.Converter;

public class LSWrite extends DirectCommandInput{

	private final static byte COMMAND_LENGTH = 5;
	
	public void setTransmittedData(byte[] txData){
		super.command[3] = (byte)txData.length;
		System.arraycopy(txData, 0, super.command, 5, txData.length);
		super.refreshCommand();
	}
	
	public void setRxDataLength(byte rxDataLength){
		super.command[4] = rxDataLength;
		super.refreshCommand();
	}
	
	public LSWrite(byte portNumber,byte[] txData, byte rxDataLength) {
		super((byte) (COMMAND_LENGTH+txData.length), CommandType.LS_WRITE);
		setInputPort(portNumber);
		setTransmittedData(txData);
		setRxDataLength(rxDataLength);
	}
	
	public String toString(){
		String ret = super.toString();
		ret += "command: "+Converter.bytesToString(command);
		return ret;
	}

}

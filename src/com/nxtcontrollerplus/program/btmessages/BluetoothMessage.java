package com.nxtcontrollerplus.program.btmessages;

import com.nxtcontrollerplus.program.btmessages.commands.DirectCommand;

/**
* Valid BlueTooth Messages for NXT device: 
* 0.byte - command length LSB, !the length of the packages is counted without two length bytes
* 1.byte - command length MSB
* 3.byte..(length-1). byte - command 
* @see DirectCommand
* @author Lukas Dilik
*/
public abstract class BluetoothMessage {
	
	protected byte[] bytes = null;
	
	protected BluetoothMessage(byte commandLength){
		this.bytes = new byte[commandLength+2];
		this.bytes[0] = commandLength;
		this.bytes[1] = 0x00;
	}
	
	protected BluetoothMessage(byte[] existBytes){
		this.bytes = existBytes;
	}
	
	protected void appendCommand(byte[] command){
		System.arraycopy(command,0, bytes, 2,command.length);
	}

	public byte[] getBytes() {
		return this.bytes;
	}
	
	public String toString(){
		String temp= "Bluetooth Message:\n";
		temp += "length LSB: " + bytes[0]+"\n";
		temp += "length MSB: " + bytes[1]+"\n";
		return temp;
	}
}

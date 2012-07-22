package com.nxtcontrollerplus.program.btmessages.returnpackages;

/**
 * Structure:
 * Byte[0-2] @see ReturnPackage
 * Byte[3] - Bytes ready
 * @author Lukas Dilik
 * @see ReturnPackage
 */
public class LSGetStatusReturnPackage extends ReturnPackage{

	private byte availableBytesToRead;
	
	public byte getAvailableBytesToRead() {
		return availableBytesToRead;
	}

	public LSGetStatusReturnPackage(byte[] bytes) {
		super(bytes);
		this.availableBytesToRead = super.returnBytes[3];
	}

	public String toString(){
		String temp = super.toString();
		temp += "count of bytes available to read: "+ getAvailableBytesToRead() +"\n";
		temp += this.getClass().toString()+"_END";
		return temp;
	}
}

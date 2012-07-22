package com.nxtcontrollerplus.program.btmessages.returnpackages;

import com.nxtcontrollerplus.program.utils.Converter;

public class LSReadReturnPackage extends ReturnPackage{

	public static final byte LS_COMMUNICATION_MAX_LENGTH = 16; 
	private byte bytesRead;
	private byte[] rxData;
	
	public byte getBytesRead() {
		return bytesRead;
	}
	
	public byte[] getRxData() {
		return rxData;
	}

	private void loadRxData() {
		System.arraycopy(returnBytes, 4, rxData, 0, rxData.length);
	}

	public LSReadReturnPackage(byte[] bytes) {
		super(bytes);
		rxData = new byte[LS_COMMUNICATION_MAX_LENGTH];
		this.bytesRead = super.returnBytes[3];
		loadRxData();
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "bytes read: "+ getBytesRead() +"\n";
		temp += "Rx data(padded): "+ Converter.bytesToString(getRxData()) +"\n";
		temp += this.getClass().toString()+"_END";
		return temp;
	}

}

package nxtcontroller.program.btmessages.returnpackages;

import nxtcontroller.program.btmessages.BluetoothMessage;
import nxtcontroller.program.utils.ErrorDecoder;

/**
 * this class represent return package array sends 
 * by NXT if response is required
 * Byte[0] - telegram type
 * Byte[1] - type command which requires this return package @see CommandType
 * Byte[2] - status @see ErrorDecoder 
 * @author Lukas Dilik
 * @see BluetoothMessage
 */
public class ReturnPackage extends BluetoothMessage{
	private byte telegramType; //every return package has same telegram type 
	private byte type; //what command type requested this return package
	private byte status; //status info OK or some errorMessage
	
	/**
	 * this contains byte array without first two bytes
	 * @see BluetoothMessage
	 */
	protected byte[] returnBytes = null;
	
	public byte getTelegramType() {
		return telegramType;
	}
	
	public byte getType() {
		return type;
	}

	public byte getStatus() {
		return status;
	}

	public ReturnPackage(byte[] bytes){
		super(bytes);
		returnBytes = new byte[bytes.length-2];
		System.arraycopy(bytes, 2, returnBytes, 0, returnBytes.length);
		this.telegramType = this.returnBytes[0]; 
		this.type = this.returnBytes[1];
		this.status = this.returnBytes[2];
	}

	public String toString(){
		String temp="RETURN_PACKAGE:\n";
		temp += "Telegram type: "+ Integer.toHexString(getTelegramType()) +"\n";
		temp += "Type: "+ Integer.toHexString(getType())+"\n";
		ErrorDecoder e = new ErrorDecoder();
		temp += "Status: "+ e.getErrorDescription(getStatus())+"\n";
		return temp;
	}

}

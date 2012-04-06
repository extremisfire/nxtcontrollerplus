package nxtcontroller.program.btmessages.returnpackages;

import nxtcontroller.program.btmessages.BluetoothMessage;
import nxtcontroller.program.utils.ErrorDecoder;

public class ReturnPackage extends BluetoothMessage{
	private byte telegramType; //every return package has same telegram type 
	private byte type; //what command type requested this return package
	private byte status; //status info OK or some errorMessage
	
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
		this.telegramType = super.bytes[2]; 
		this.type = super.bytes[3];
		this.status = super.bytes[4];
	}

	public String toString(){
		String temp="RETURN_PACKAGE:\n";
		temp += "Telegram type: "+ this.telegramType+"\n";
		temp += "Type: "+ this.type+"\n";
		ErrorDecoder e = new ErrorDecoder();
		temp += "Status: "+ e.getErrorDescription(this.status)+"\n";
		return temp;
	}

}

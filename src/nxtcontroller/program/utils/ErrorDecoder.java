package nxtcontroller.program.utils;

import java.util.HashMap;

/**
 * this class decodes byte coded error 
 * from NXT to its String human-readable representation
 * @author Lukas Dilik
 */
public class ErrorDecoder {
	
	private  HashMap<Byte,String> errorCodes;
	
	public ErrorDecoder(){
		errorCodes = new HashMap<Byte,String>();
		errorCodes.put((byte)0x00, "OK");
		errorCodes.put((byte)0x20, "pending communication transaction in progress");
		errorCodes.put((byte)0x40, "specified mailbox queue is empty");
		errorCodes.put((byte)0xBD, "request failed (i.e specified file not found)");
		errorCodes.put((byte)0xBE, "unknown command opcode");		
		errorCodes.put((byte)0xBF, "insane packet");		
		errorCodes.put((byte)0xC0, "data contains out-of-range-values");		
		errorCodes.put((byte)0xDD, "communication bus error");		
		errorCodes.put((byte)0xDF, "specified channel/connection is not valid");		
		errorCodes.put((byte)0xE0, "specified channel/connection is not configured or busy");		
		errorCodes.put((byte)0xEC, "no active program");		
		errorCodes.put((byte)0xED, "illegal size specified");		
		errorCodes.put((byte)0xEE, "illegal mailbox queue ID specified");		
		errorCodes.put((byte)0xF0, "bad input or output specified");	
		errorCodes.put((byte)0xFB, "insufficient memory available");	
		errorCodes.put((byte)0xFF, "bad arguments");
	}
	
	public String getErrorDescription(byte errorCode){
		String res="";
		if(errorCodes.containsKey((byte)errorCode)){
			res = errorCodes.get((byte)errorCode);
		}
		return res;
	}
}

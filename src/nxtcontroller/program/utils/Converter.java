package nxtcontroller.program.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * convert LITTLE-ENDIAN encoded variables
 * from bytes array to java integer(32bit)
 * to bytes array from java integer(32bit)
 * @author Lukas Dilik
 */
public class Converter {
	
	private static final byte BITS = 8; //number of bits in byte variable
	
	/**
	 * decode byte array to integer value
	 * @param bytes
	 * @return integer value
	 */
	public static int fromBytes(byte[] bytes){
		int result = 0;
		for(int i = 0;i < bytes.length; i++){
			result += (bytes[i] << (BITS*i)); 
		}
		return result;
	}
	
	/**
	 * encode short variable to byte array
	 * compatible with NXT
	 * @param var
	 * @return 4-elements byte array
	 */
	public static byte[] toBytes(int var){
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(var);
		return buffer.array();
	}
	
	/**
	 * encode short variable to byte array
	 * compatible with NXT
	 * @param var
	 * @return 2-elements byte array
	 */
	public static byte[] toBytes(short var){
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort(var);
		return buffer.array();
	}
	
	public static String bytesToString(byte[] bytes){
		String temp="[";
		int i = 0;
		for(byte b:bytes){
			temp+=Short.toString(b);
			temp+= (i==bytes.length-1)? "" : ", ";
			i++;
		}
		return temp+"]";
	}

}

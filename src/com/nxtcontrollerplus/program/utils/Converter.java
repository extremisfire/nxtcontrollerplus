package com.nxtcontrollerplus.program.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * convert LITTLE-ENDIAN encoded variables
 * from bytes array to java int(32bits)
 * to bytes array from java int(32bits) or short(16bits)
 * @author Lukas Dilik
 */
public class Converter {
	
	/**
	 * decode byte array to integer value
	 * LITTLE-ENDIAN
	 * @param bytes
	 * @return integer value if byte-array not 2 or 4 bytes size return zero
	 */
	public static int fromBytes(byte[] bytes){
		ByteBuffer b = ByteBuffer.wrap(bytes);
		b.order(ByteOrder.LITTLE_ENDIAN);
		if(bytes.length==2){
			return b.getShort();
		}else if (bytes.length == 4){
			return b.getInt();
		}else
			return 0;
	}
	
	/**
	 * encode uword(short) 2-bytes variable to byte array
	 * compatible with NXT LITTLE-ENDIAN
	 * @param var
	 * @return 2-elements byte array
	 */
	public static byte[] to2BytesArray(short var){
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort(var);
		return buffer.array();
	}
	
	/**
	 * encode ulong(int) 4-bytes variable to byte array
	 * compatible with LITTLE-ENDIAN
	 * @param var
	 * @return 4-elements byte array
	 */
	public static byte[] to4BytesArrays(int var){
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(var);
		return buffer.array();
	}
	
	/**
	 * if byte value is higher than max positive value of byte in java
	 * 127, then print byte value as unsigned ubyte from cpp
	 * @param bytes-array
	 * @return readable byte array values
	 */
	public static String bytesToString(byte[] bytes){
		String temp="[";
		int i = 0;
		for(byte b:bytes){
			int ubyte = (int)(b & 0xFF);
			temp+= "0x"+ Integer.toHexString(ubyte).toUpperCase();
			temp+= (i==bytes.length-1)? "" : ", ";
			i++;
		}
		return temp+"]";
	}
	
	public static String bytesToString(Byte[] bytes){
		String temp="[";
		int i = 0;
		for(byte b:bytes){
			int ubyte = (int)(b & 0xFF);
			temp+= "0x"+ Integer.toHexString(ubyte).toUpperCase();
			temp+= (i==bytes.length-1)? "" : ", ";
			i++;
		}
		return temp+"]";
	}
	
	public static byte[] bytesArrayConverter(Byte[] source){
		byte[] res = new byte[source.length];
		for(int i=0;i<source.length;i++){
			res[i] = source[i];
		}
		return res;
	}
	
	public static Byte[] bytesArrayConverter(byte[] source){
		Byte[] res = new Byte[source.length];
		for(int i=0;i<source.length;i++){
			res[i] = source[i];
		}
		return res;
	}
	
	public static byte[] mergeByteArrays(byte[] arrayOne, byte[] arrayTwo){
		byte[] temp = new byte[arrayOne.length+arrayTwo.length];
		System.arraycopy(arrayOne, 0, temp, 0, arrayOne.length);
		System.arraycopy(arrayTwo, 0, temp, arrayOne.length, arrayTwo.length);
		return temp;
	}
}

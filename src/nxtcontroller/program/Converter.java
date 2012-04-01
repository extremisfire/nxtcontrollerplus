package nxtcontroller.program;

/**
 * convert LITTLE-ENDIAN encoded variables
 * from bytes array to java integer(32bit)
 * to bytes array from java integer(32bit)
 * @author Lukas Dilik
 */
public class Converter {
	
	private static final byte BITS = 8; //number of bits in byte variable
	
	public static int fromBytes(byte[] bytes){
		int result = 0;
		for(int i = 0;i < bytes.length; i++){
			result += (bytes[i] << (BITS*i)); 
		}
		return result;
	}
	
	public static byte[] toBytes(int var){
		byte[] result = new byte[BITS*4];
		return null;
		//TODO
	}

}

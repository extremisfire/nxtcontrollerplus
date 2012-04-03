package nxtcontroller.enums.nxtbuiltin;

public class TelegramType {
	public static final byte DIRECT_COMMAND_RRQ = 0x00; //direct command telegram, response required
	public static final byte SYSTEM_COMMAND_RRQ = 0x01; //system command telegram, response required
	public static final byte DIRECT_COMMAND_NORRQ = (byte)0x80; //direct command telegram, no response
	public static final byte SYSTEM_COMMAND_NORRQ = (byte)0x81; //system command telegram,no response 
	public static final byte REPLY_TELEGRAM= 0x02; //return package
}

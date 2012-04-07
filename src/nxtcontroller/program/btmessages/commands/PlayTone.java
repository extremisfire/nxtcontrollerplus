package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.TelegramType;
import nxtcontroller.program.btmessages.BluetoothMessage;
import nxtcontroller.program.utils.Converter;

public class PlayTone extends BluetoothMessage{

	private final static byte COMMAND_LENGTH = 6;
	private byte[] command;
	
	public void setFrequency(short frequency){
		byte[] f = new byte[2];
		f = Converter.toBytes(frequency);
		System.arraycopy(f, 0, command, 2, f.length);
		appendCommand(command);
	}
	
	public void setDuration(short duration){
		byte[] d = new byte[2];
		d = Converter.toBytes(duration);
		System.arraycopy(d, 0, command, 4, d.length);
		appendCommand(command);
	}
	
	public void setRequireResponseFLag(){
		command[0] = TelegramType.DIRECT_COMMAND_RRQ;
		appendCommand(command);
	}

	public PlayTone(short frequency, short duration) {
		super(COMMAND_LENGTH);
		command = new byte[COMMAND_LENGTH];
		command[0] = TelegramType.DIRECT_COMMAND_NORRQ;
		command[1] = CommandType.PLAY_TONE;
		byte[] f = new byte[2];
		byte[] d = new byte[2];
		f = Converter.toBytes(frequency);
		d = Converter.toBytes(duration);
		System.arraycopy(f, 0, command, 2, f.length);
		System.arraycopy(d, 0, command, 4, d.length);
		appendCommand(command);
	}
		
	public String toString(){
			String temp = super.toString();
			temp += "PLAY_TONE_START" + Converter.bytesToString(bytes)+"\n";
			byte[] f = new byte[2];
			byte[] d = new byte[2];
			System.arraycopy(command, 2, f, 0, f.length);
			System.arraycopy(command, 4, d, 0, d.length);
			temp += "FREQUENCY: " + Integer.toString(Converter.fromBytes(f)) +" Hz\n";
			temp += "DURATION: "  + Integer.toString(Converter.fromBytes(d)) +" ms\n";
			temp += "PLAY_TONE_END\n";
			return temp;
	}

}

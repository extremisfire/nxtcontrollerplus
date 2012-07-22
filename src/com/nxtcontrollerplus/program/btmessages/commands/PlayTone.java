package com.nxtcontrollerplus.program.btmessages.commands;

import com.nxtcontrollerplus.enums.nxtbuiltin.CommandType;
import com.nxtcontrollerplus.program.utils.Converter;

public class PlayTone extends DirectCommand{

	private final static byte COMMAND_LENGTH = 6;
	
	public void setFrequency(short frequency){
		byte[] f = new byte[2];
		f = Converter.to2BytesArray(frequency);
		System.arraycopy(f, 0, command, 2, f.length);
		super.refreshCommand();
	}
	
	public void setDuration(short duration){
		byte[] d = new byte[2];
		d = Converter.to2BytesArray(duration);
		System.arraycopy(d, 0, command, 4, d.length);
		super.refreshCommand();
	}
	

	public PlayTone(short frequency, short duration) {
		super(COMMAND_LENGTH,CommandType.PLAY_TONE);
		setFrequency(frequency);
		setDuration(duration);
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

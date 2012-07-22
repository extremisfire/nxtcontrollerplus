package com.nxtcontrollerplus.enums.nxtbuiltin;

/**
 * contains byte code for all message types 
 * supported in NXT 2.0 BlueTooth commands
 * @author Lukas Dilik
 */
public class CommandType {
	public static final byte START_PROGRAM = 0x00;
	public static final byte STOP_PROGRAM = 0x01;
	public static final byte PLAY_SOUND_FILE = 0x02;
	public static final byte PLAY_TONE = 0x03;
	public static final byte SET_OUTPUT_STATE = 0x04;
	public static final byte SET_INPUT_MODE = 0x05;
	public static final byte GET_OUTPUT_STATE = 0x06;
	public static final byte GET_INPUT_VALUES = 0x07;
	public static final byte RESET_INPUT_SCALED_VALUE = 0x08 ;
	public static final byte MESSAGE_WRITE = 0x09;
	public static final byte RESET_MOTOR_POSTITION = 0x0A;
	public static final byte GET_BATTERY_LEVEL = 0x0B;
	public static final byte STOP_SOUND_PLAYBACK = 0x0C;
	public static final byte KEEP_ALIVE = 0x0D;
	public static final byte LS_GET_STATUS = 0x0E;
	public static final byte LS_WRITE = 0x0F;
	public static final byte LS_READ = 0x10;
	public static final byte GET_CURRENT_PROGRAM_NAME = 0x11;
	public static final byte MESSAGE_READ = 0x13;
}

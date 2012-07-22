package com.nxtcontrollerplus.enums.nxtbuiltin;

public class SensorType {
	public static final byte NO_SENSOR = 0x00;
	public static final byte SWITCH = 0x01; //touch sensor
	public static final byte TEMPERATURE = 0x02;
	public static final byte REFLECTION = 0x03; //ultra-sonic sensor
	public static final byte ANGLE = 0x04;
	public static final byte LIGHT_ACTIVE = 0x05;
	public static final byte LIGHT_INACTIVE = 0x06;
	public static final byte SOUND_DB = 0x07;
	public static final byte SOUND_DBA = 0x08 ;
	public static final byte CUSTOM = 0x09; //custom sensor
	public static final byte LOW_SPEED = 0x0A;
	public static final byte LOW_SPEED_9V = 0x0B;
	public static final byte NO_OF_SENSOR_TYPES = 0x0C;
}

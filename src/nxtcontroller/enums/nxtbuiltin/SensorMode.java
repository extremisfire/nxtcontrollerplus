package nxtcontroller.enums.nxtbuiltin;

public class SensorMode {
	public static final byte RAW_MODE = 0x00;
	public static final byte BOOLEAN_MODE = 0x20;
	public static final byte TRANSITION_CNT_MODE = 0x40;
	public static final byte PERIOD_COUNTER_MODE = 0x60; 
	public static final byte PCT_FULL_SCALE_MODE = (byte)0x80;
	public static final byte CELSIUS_MODE = (byte)0xA0;
	public static final byte FAHRENHEIT_MODE = (byte)0xC0;
	public static final byte ANGLE_STEPS_MODE = (byte)0xE0;
	public static final byte SLOP_MASK = 0x1F;
	public static final byte MODE_MASK = (byte)0xE0; 
}

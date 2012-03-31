package nxtcontroller.enums.nxtbuiltin;

public class RegulationMode {
	public static final byte REGULATION_MODE_IDLE = 0x00; // No regulation will be enabled
	public static final byte REGULATION_MODE_MOTOR_SPEED = 0x01; //power control will be enabled on specified output
	public static final byte REGULATION_MODE_MOTOR_SYNC  = 0x02; // synchronization will be enabled (needs enabled on 2 output)
}

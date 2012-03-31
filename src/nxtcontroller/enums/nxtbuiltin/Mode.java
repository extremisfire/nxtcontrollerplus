package nxtcontroller.enums.nxtbuiltin;

public class Mode {
	public static final byte MOTOR_ON = 0x01; //turn on specified motor
	public static final byte BRAKE = 0x02; //use run/brake instead of run/float in PWM
	public static final byte REGULATED = 0x04; //turns on regulation
}

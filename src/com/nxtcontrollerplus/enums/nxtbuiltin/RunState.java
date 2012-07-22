package com.nxtcontrollerplus.enums.nxtbuiltin;

public class RunState {
	public static final byte MOTOR_RUN_STATE_IDLE = 0x00; // output will be idle
	public static final byte MOTOR_RUN_STATE_RAMPUP = 0x10; // output will be ram p-up
	public static final byte MOTOR_RUN_STATE_RUNNING = 0x20; // output will be running
	public static final byte MOTOR_RUN_STATE_RAMPDOWN = 0x40; // output will be ram p-down
}

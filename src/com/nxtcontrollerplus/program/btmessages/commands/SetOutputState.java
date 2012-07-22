package com.nxtcontrollerplus.program.btmessages.commands;

import com.nxtcontrollerplus.enums.nxtbuiltin.CommandType;
import com.nxtcontrollerplus.enums.nxtbuiltin.Mode;
import com.nxtcontrollerplus.enums.nxtbuiltin.RegulationMode;
import com.nxtcontrollerplus.enums.nxtbuiltin.RunState;
import com.nxtcontrollerplus.enums.nxtbuiltin.TelegramType;
import com.nxtcontrollerplus.program.utils.Converter;

/**
 * generate a byte array command for NXT
 * Command name:  SET_OUTPUT_STATE 
 * Byte [0-1] @see DirectCommand
 * Byte 2: Output port (range 0-2;0xFF is special value meaning 'all' for simple control purposes)
 * Byte 3: Power set point alias SPEED (range:-100 to 100) negative sing means counter-clockwise
 * Byte 4: Mode byte (bit-field)
 * Byte 5: Regulation mode (UBYTE;enumerated)
 * Byte 6: Turn ratio (SBYTE;-100 to 100)
 * Byte 7: RunState (UBYTE;enumerated)
 * Byte 8-12: TachoLmit (ULONG;0:run forever) in degrees rotation of motor
 * @see DirectCommand
 * @author Lukas Dilik
 * */

public class SetOutputState extends DirectCommandOutput{

	private static final byte COMMAND_LENGTH = 12;
	
	/**
	 * call this when you want to get ReturnPackage from this command
	 */
	public void setRequireReponseFlag(){
		this.command[0] = TelegramType.DIRECT_COMMAND_RRQ;
		super.refreshCommand();
	}
	
	/**
	 * @param power of motor <-100,100> negative sing means counter-clockwise
	 */
	public void setPower(byte power){
		this.command[3] = power;
		super.refreshCommand();
	}
	
	/**
	 * @param modeBitMask
	 * @see Mode
	 */
	public void setMode(byte modeBitMask){
		this.command[4] = modeBitMask;
		super.refreshCommand();
	}
	
	/**
	 * @param regulationMode
	 * @see RegulationMode
	 */
	public void setRegulationMode(byte regulationMode){
		this.command[5] = regulationMode;
		super.refreshCommand();
	}
	
	/**
	 * @param turnRatio <-100,100> negative sing means counter-clockwise
	 */
	public void setTurnRatio(byte turnRatio){
		this.command[6] = turnRatio;
		super.refreshCommand();
	}
	
	/**
	 * @param runState
	 * @see RunState
	 */
	public void setRunState(byte runState){
		this.command[7] = runState;
		super.refreshCommand();
	}
	
	/**
	 * rotate motor for specified angle, 0 means run forever
	 * @param degrees
	 */
	public void setTachoLimit(int degrees){
		byte[] temp  = new byte[4];
		temp = Converter.to4BytesArrays(degrees);
		System.arraycopy(temp, 0, command, 8, temp.length);
		super.refreshCommand();
	}
	
	public SetOutputState() {
		super(COMMAND_LENGTH,CommandType.SET_OUTPUT_STATE);
		setOutputPort((byte)0);
		setPower((byte)0);
		setMode((byte)(Mode.MOTOR_ON+Mode.BRAKE+Mode.REGULATED));
		setRegulationMode(RegulationMode.REGULATION_MODE_IDLE);
		setTurnRatio((byte) 0);
		setRunState(RunState.MOTOR_RUN_STATE_RUNNING);
		setTachoLimit(0);
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "SET_OUTPUTSTATE_START\n";
		temp += Converter.bytesToString(bytes)+"\n";
		temp += "SET_OUTPUTSTATE_END\n";
		return temp;
	}

}

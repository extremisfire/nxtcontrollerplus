package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.Mode;
import nxtcontroller.enums.nxtbuiltin.RegulationMode;
import nxtcontroller.enums.nxtbuiltin.RunState;
import nxtcontroller.enums.nxtbuiltin.TelegramType;
import nxtcontroller.program.utils.Converter;

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
 * Byte 8-12: TachoLmit (ULONG;0:run forever) in ms (how long may be turned on motors)
 * @see DirectCommand
 * @author Lukas Dilik
 * */

public class SetOutputState extends DirectCommand{

	private static final byte COMMAND_LENGTH = 12;
	
	/**
	 * call this when you want to get ReturnPackage from this command
	 */
	public void setRequireReponseFlag(){
		this.command[0] = TelegramType.DIRECT_COMMAND_RRQ;
		super.refreshCommand();
	}
	
	/**
	 * @param port onNXT:[A:0,B:1,C:2]
	 */
	public void setOutputPort(byte port){
		this.command[2] = port;
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
	 * how long may be turned on motors in miliSeconds, 0 means forever
	 * @param miliSeconds
	 */
	public void setTachoLimit(int miliSeconds){
		byte[] temp  = new byte[4];
		temp = Converter.toULONG(miliSeconds);
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

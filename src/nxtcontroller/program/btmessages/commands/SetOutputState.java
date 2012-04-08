package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.Mode;
import nxtcontroller.enums.nxtbuiltin.RegulationMode;
import nxtcontroller.enums.nxtbuiltin.RunState;
import nxtcontroller.enums.nxtbuiltin.TelegramType;
import nxtcontroller.program.utils.Converter;

	/**
 	 * generate a byte array command for NXT
	 * Command name:  SETOUTPUTSTATE (in data[2..13])
	 * Byte 0: 0x80 means direct command telegram, no response required
	 * Byte 1: 0x04
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
	
	public void setRequireReponseFlag(){
		this.command[0] = TelegramType.DIRECT_COMMAND_RRQ;
		super.refreshCommand();
	}
	
	public void setOutputPort(byte port){
		this.command[2] = port;
		super.refreshCommand();
	}
	
	public void setPower(byte power){
		this.command[3] = power;
		super.refreshCommand();
	}
	
	public void setMode(byte modeBitMask){
		this.command[4] = modeBitMask;
		super.refreshCommand();
	}
	
	public void setRegulationMode(byte regulationMode){
		this.command[5] = regulationMode;
		super.refreshCommand();
	}
	
	public void setTurnRatio(byte turnRatio){
		this.command[6] = turnRatio;
		super.refreshCommand();
	}
	
	public void setRunState(byte runState){
		this.command[7] = runState;
		super.refreshCommand();
	}
	
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

}

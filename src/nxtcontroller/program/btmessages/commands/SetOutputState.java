package nxtcontroller.program.btmessages.commands;

import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.Mode;
import nxtcontroller.enums.nxtbuiltin.RegulationMode;
import nxtcontroller.enums.nxtbuiltin.RunState;
import nxtcontroller.enums.nxtbuiltin.TelegramType;
import nxtcontroller.program.btmessages.BluetoothMessage;
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
	 * @see BluetoothMessage
	 * @author Lukas Dilik
	 * */
public class SetOutputState extends BluetoothMessage{

	private static final byte COMMAND_LENGTH = 12;
	private byte[] command;
	
	public void setRequireReponseFlag(){
		this.command[0] = TelegramType.DIRECT_COMMAND_RRQ;
		super.appendCommand(command);
	}
	
	public void setOutputPort(byte port){
		this.command[2] = port;
		super.appendCommand(command);
	}
	
	public void setPower(byte power){
		this.command[3] = power;
		super.appendCommand(command);
	}
	
	public void setModeByte(byte modeBitMask){
		this.command[4] = modeBitMask;
		super.appendCommand(command);
	}
	
	public void setRegulationMode(byte regulationMode){
		this.command[5] = regulationMode;
		super.appendCommand(command);
	}
	
	public void setTurnRatio(byte turnRatio){
		this.command[6] = turnRatio;
		super.appendCommand(command);
	}
	
	public void setRunState(byte runState){
		this.command[7] = runState;
		super.appendCommand(command);
	}
	
	public void setTachoLimit(int miliSeconds){
		byte[] temp  = new byte[4];
		temp = Converter.toBytes(miliSeconds);
		System.arraycopy(temp, 0, command, 8, temp.length);
		super.appendCommand(command);
	}
	
	public SetOutputState() {
		super(COMMAND_LENGTH);
		this.command = new byte[COMMAND_LENGTH];
		this.command[0] = TelegramType.DIRECT_COMMAND_NORRQ;
		this.command[1] = CommandType.SET_OUTPUT_STATE;
		this.command[2] = 0x00;
		this.command[3] = 0x00;
		this.command[4] = Mode.MOTOR_ON+Mode.BRAKE+Mode.REGULATED;
		this.command[5] = RegulationMode.REGULATION_MODE_IDLE;
		this.command[6] = 0x00;
		this.command[7] = RunState.MOTOR_RUN_STATE_RUNNING;
		this.command[8] = 0x00;
		this.command[9] = 0x00;
		this.command[10] = 0x00;
		this.command[11] = 0x00;
		super.appendCommand(command);
	}

}

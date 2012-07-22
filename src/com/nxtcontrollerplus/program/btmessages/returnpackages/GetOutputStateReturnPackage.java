package com.nxtcontrollerplus.program.btmessages.returnpackages;

import com.nxtcontrollerplus.program.utils.Converter;

/**
 * Structure:
 * Byte[0-2] @see ReturnPackage
 * Byte[3] - Output port
 * Byte[4] - power set point
 * Byte[5] - Mode(bit-field) @see Mode
 * Byte[6] - regulation mode @see RegulationMode
 * Byte[7] - turn ration
 * Byte[8] - runState @see RunState
 * Byte[9-12] - tachoLimit - current limit on a movement in progress if any
 * Byte[13-16] - tachoCount - internal count number of counts since last of the motor
 * Byte[17-20] - blockTachoCount - current position relative to last programmed movement
 * BYte[21-24] - rotationCount - current position relative to last reset of the rotation sensor for this motor
 * @author Lukas Dilik
 * @see ReturnPackage
 */
public class GetOutputStateReturnPackage extends ReturnPackage{

	private byte outputPort;
	private byte powerSetPoint;
	private byte mode;
	private byte regulationMode;
	private byte turnRatio;
	private byte runState;
	private int  tachoLimit;
	private int  tachoCount;
	private int  blockTachoCount;
	private int  rotationCount;
	
	public  byte getOutputPort() {
		return outputPort;
	}

	public  byte getPowerSetPoint() {
		return powerSetPoint;
	}

	public  byte getMode() {
		return mode;
	}

	public  byte getRegulationMode() {
		return regulationMode;
	}

	public  byte getTurnRatio() {
		return turnRatio;
	}

	public  byte getRunState() {
		return runState;
	}

	public  int getTachoLimit() {
		return tachoLimit;
	}

	public  int getTachoCount() {
		return tachoCount;
	}

	public  int getBlockTachoCount() {
		return blockTachoCount;
	}


	public  int getRotationCount() {
		return rotationCount;
	}

	/**
	 * decodes byte array into seperated class properites which is easy to use its values
	 * @see ReturnPackage
	 * @param bytes - bytes array from NXT device
	 */
	public GetOutputStateReturnPackage(byte[] bytes) {
		super(bytes);
		this.outputPort = super.returnBytes[3];
		this.powerSetPoint = super.returnBytes[4];
		this.mode = super.returnBytes[5];
		this.regulationMode = super.returnBytes[6];
		this.turnRatio = super.returnBytes[7];
		this.runState = super.returnBytes[8];
		//calculate tachoLimit
		byte[] temp = new byte[4];
		System.arraycopy(super.returnBytes, 9, temp, 0, temp.length);
		this.tachoLimit = Converter.fromBytes(temp);
		//calculate tachoCount
		System.arraycopy(super.returnBytes, 13, temp, 0, temp.length);
		this.tachoCount = Converter.fromBytes(temp);
		//calculate blockTachoCount
		System.arraycopy(super.returnBytes, 17, temp, 0, temp.length);
		this.blockTachoCount = Converter.fromBytes(temp);
		//calculate rotationCount
		System.arraycopy(super.returnBytes, 21, temp, 0, temp.length);
		this.rotationCount = Converter.fromBytes(temp);
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "outPut port: "+ getOutputPort()+"\n";
		temp += "power set point: "+ getPowerSetPoint()+"\n";
		temp += "mode(bit-field): "+ getMode()+"\n";
		temp += "regulation mode: "+ getRegulationMode()+"\n";
		temp += "turn ratio: "+ getTurnRatio()+"\n";
		temp += "run state: "+ getRunState()+"\n";
		temp += "tacho limit: "+ getTachoLimit()+"\n";
		temp += "tacho count: "+ getTachoCount()+"\n";
		temp += "block tacho count: "+ getBlockTachoCount()+"\n";
		temp += "rotation count: "+ getRotationCount()+"\n";
		temp += this.getClass().toString()+"_END";
		return temp;
	}

}

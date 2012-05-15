package nxtcontroller.program.btmessages.returnpackages;

import nxtcontroller.program.utils.Converter;

/**
 * Structure:
 * Byte[0-2] @see ReturnPackage 
 * Byte[3] - input port
 * Byte[4] - valid? (boolean TRUE if new data value should be seen as valid data)
 * Byte[5] - calibrated? (boolean TRUE if calibration file found and used for calibrated value below)
 * Byte[6] - sensorType @see SensorType
 * Byte[7] - sensorMode @see SensorMode
 * Byte[8-9] - raw A/D value 
 * Byte[10-11] - normalized A/D value (0-1023)
 * Byte[12-13] - scaled value
 * Byte[14-15] - calibrated value
 * @author Lukas Dilik
 * @see ReturnPackage
 */
public class GetInputValuesReturnPackage extends ReturnPackage{

	private byte inputPort;
	private boolean isValid;
	private boolean isCalibrated;
	private byte sensorType;
	private byte sensorMode;
	private int rawValue;
	private int normalizedValue;
	private int scaledValue;
	private int calibratedValue;
	
	public  byte getInputPort() {
		return inputPort;
	}

	public  boolean isValid() {
		return isValid;
	}

	public  boolean isCalibrated() {
		return isCalibrated;
	}

	public  byte getSensorType() {
		return sensorType;
	}

	public  byte getSensorMode() {
		return sensorMode;
	}

	public  int getRawValue() {
		return rawValue;
	}

	public  int getNormalizedValue() {
		return normalizedValue;
	}

	public  int getScaledValue() {
		return scaledValue;
	}

	public  int getCalibratedValue() {
		return calibratedValue;
	}

	/**
	 * decodes byte array into seperated class properites which is easy to use its values
	 * @see ReturnPackage
	 * @param bytes - bytes array from NXT device
	 */
	public GetInputValuesReturnPackage(byte[] bytes) {
		super(bytes);
		this.inputPort = super.returnBytes[3];
		this.isValid = (super.returnBytes[4]==1) ? true : false;
		this.isCalibrated = (super.returnBytes[5]==1) ? true : false;
		this.sensorType = super.returnBytes[6];
		this.sensorMode = super.returnBytes[7];
		
		byte[] temp = new byte[2];
		//calculate rawValue
		System.arraycopy(super.returnBytes, 8, temp, 0, temp.length);
		this.rawValue = Converter.fromBytes(temp);
		//calculate normalized value
		System.arraycopy(super.returnBytes, 10, temp, 0, temp.length);
		this.normalizedValue = Converter.fromBytes(temp);
		//calculate scaled value
		System.arraycopy(super.returnBytes, 12, temp, 0, temp.length);
		this.scaledValue = Converter.fromBytes(temp);
		//calculate calibrated value
		System.arraycopy(super.returnBytes, 14, temp, 0, temp.length);
		this.calibratedValue = Converter.fromBytes(temp);
	}
	
	public String toString(){
		String temp = super.toString();
		temp += "input port: "+ getInputPort()+"\n";
		temp += "valid?: "+ Boolean.toString(isValid()) +"\n";
		temp += "calibrated?: "+ Boolean.toString(isCalibrated()) +"\n";
		temp += "sensor type: "+ Integer.toHexString(getSensorType())+"\n";
		temp += "sensor mode: "+ Integer.toHexString(getSensorMode())+"\n";
		temp += "raw value: "+ getRawValue()+"\n";
		temp += "normalized value: "+ getNormalizedValue()+"\n";
		temp += "scaled value: "+ getScaledValue()+"\n";
		temp += "calibrated value: "+ getCalibratedValue()+"\n";
		temp += this.getClass().toString()+"_END";
		return temp;
	}

}

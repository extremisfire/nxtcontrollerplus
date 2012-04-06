package nxtcontroller.program.btmessages.returnpackages;

import nxtcontroller.program.utils.Converter;

public class GetBatteryLevelReturnPackage extends ReturnPackage{

	public static final int maxVoltage = 8200; // full battery voltage in mV
	
	private float currentMiliVolts;
	
	public float getBatteryLevel() {
		float temp = ((float)currentMiliVolts/(float)maxVoltage)*100;
		temp = (temp >= 100) ? 100 : temp;
		return temp;
	}

	public GetBatteryLevelReturnPackage(byte[] bytes) {
		super(bytes);
		byte[] b =new byte[2];
		b[0] = super.bytes[5];
		b[1] = super.bytes[6];
		this.currentMiliVolts = Converter.fromBytes(b);
	}

	public String toString(){
		String temp = super.toString();
		temp += "miliVolts: "+ this.currentMiliVolts +"\n";
		temp += "RETURN_PACKAGE_END";
		return temp;
	}
}

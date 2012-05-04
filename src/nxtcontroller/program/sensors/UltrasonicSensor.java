package nxtcontroller.program.sensors;

import android.util.Log;
import nxtcontroller.activity.MainActivity;
import nxtcontroller.program.btmessages.commands.LSWrite;

public class UltrasonicSensor extends I2CSensor{

	public UltrasonicSensor( byte port) {
		super(port);
	}
	
	@Override
	public void initialize(){
		super.initialize();
		Log.d(MainActivity.TAG, "initializing UltraSonic as LS_9V");
	}

	@Override
	public String toString() {
		String ret="ULTRASONIC SENSOR: ";
	    ret += Integer.toString(getMeasuredData())+" cm";
		return ret;
	}

	@Override
	public int getMeasuredData() {
		return (int)(0x000000FF & (int)super.lsdata.getRxData()[0]);
	}

	@Override
	public LSWrite getLSWriteCommand() {
		byte result = 0x42; //adress of register where 1st result is stored
		byte[] tx = {I2CSensor.DEFAULT_REGISTER_ADDRESS, result}; //transmitted data from NXT written in NXT register
		byte recievedDataLength = 0x01; // constant measurement mode is default this means only one value is expected to return
		LSWrite lw = new LSWrite(super.getPort(), tx, recievedDataLength);
		
		return lw;
	}
}

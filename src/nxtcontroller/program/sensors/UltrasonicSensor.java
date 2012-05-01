package nxtcontroller.program.sensors;

import android.util.Log;
import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.nxtbuiltin.InputPort;
import nxtcontroller.program.NXTCommunicator;
import nxtcontroller.program.btmessages.commands.LSWrite;

public class UltrasonicSensor extends I2CSensor{

	public UltrasonicSensor(NXTCommunicator nxt, byte port) {
		super(nxt, port);
	}
	
	@Override
	public void initialize(){
		super.initialize();
		byte[] tx = {0x02, 0x42}; 
		LSWrite lw = new LSWrite(InputPort.PORT3, tx, (byte)1);
		lw.setRequireResponseToOn();
		Log.d(MainActivity.TAG, "initializing ultra sonic LS:\n"+lw.toString());
		super.nxt.write(lw.getBytes());
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
}

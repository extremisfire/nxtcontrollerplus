package nxtcontroller.program.sensors;

import android.util.Log;
import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.nxtbuiltin.InputPort;
import nxtcontroller.program.NXTCommunicator;
import nxtcontroller.program.btmessages.commands.LSWrite;

public class CompassSensor extends I2CSensor{

	public CompassSensor(NXTCommunicator nxt, byte port) {
		super(nxt, port);
	}
	
	@Override
	public void initialize(){
		super.initialize();
		byte[] tx = {0x02, 0x42}; 
		LSWrite lw = new LSWrite(InputPort.PORT3, tx, (byte)1);
		lw.setRequireResponseToOn();
		Log.d(MainActivity.TAG, "initializing Compass:\n"+lw.toString());
		super.nxt.write(lw.getBytes());
	}

	@Override
	public String toString() {
		String ret="COMPASS SENSOR: ";
	    ret += Integer.toString(getMeasuredData()*2)+" deg";
		return ret;
	}

	@Override
	public int getMeasuredData() {
		return (int)(0x000000FF & (int)super.lsdata.getRxData()[0]);
	}

}

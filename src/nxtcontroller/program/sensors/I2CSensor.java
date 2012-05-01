package nxtcontroller.program.sensors;

import nxtcontroller.enums.nxtbuiltin.SensorMode;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.NXTCommunicator;
import nxtcontroller.program.btmessages.returns.packages.LSReadReturnPackages;

public abstract class I2CSensor extends Sensor{
	
	protected LSReadReturnPackages lsdata;
	
	protected I2CSensor(NXTCommunicator nxt,byte port){
		super(nxt,port);
		super.mode = SensorMode.RAW_MODE;
		super.type = SensorType.LOW_SPEED_9V;
	}
	
	
	public void refreshSensorData(LSReadReturnPackages lsdata){
		this.lsdata = lsdata;
	}
}

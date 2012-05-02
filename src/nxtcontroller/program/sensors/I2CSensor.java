package nxtcontroller.program.sensors;

import nxtcontroller.enums.nxtbuiltin.SensorMode;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.NXTCommunicator;
import nxtcontroller.program.btmessages.commands.LSWrite;
import nxtcontroller.program.btmessages.returns.packages.LSReadReturnPackages;

public abstract class I2CSensor extends Sensor{
	
	protected static final byte DEFAULT_REGISTER_ADDRESS = 0x02;
	
	protected LSReadReturnPackages lsdata;
	
	protected I2CSensor(NXTCommunicator nxt,byte port){
		super(nxt,port);
		super.mode = SensorMode.RAW_MODE;
		super.type = SensorType.LOW_SPEED_9V;
	}
	
	
	/**
	 * MUST call refreshData before
	 * @return actual register values from NXT 
	 */
	public void refreshSensorData(LSReadReturnPackages lsdata){
		this.lsdata = lsdata;
	}
	
	public abstract LSWrite getLSWriteCommand();
}

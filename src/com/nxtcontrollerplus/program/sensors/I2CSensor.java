package com.nxtcontrollerplus.program.sensors;

import com.nxtcontrollerplus.enums.nxtbuiltin.SensorMode;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorType;
import com.nxtcontrollerplus.program.btmessages.commands.LSWrite;
import com.nxtcontrollerplus.program.btmessages.returnpackages.LSReadReturnPackage;

public abstract class I2CSensor extends Sensor{
	
	protected static final byte DEFAULT_REGISTER_ADDRESS = 0x02;
	
	protected LSReadReturnPackage lsdata;
	
	protected I2CSensor(byte port){
		super(port);
		super.mode = SensorMode.RAW_MODE;
		super.type = SensorType.LOW_SPEED_9V;
	}
	
	/**
	 * MUST call refreshData before
	 * @return actual register values from NXT 
	 */
	public void refreshSensorData(LSReadReturnPackage lsdata){
		this.lsdata = lsdata;
	}
	
	public abstract LSWrite getLSWriteCommand();
}

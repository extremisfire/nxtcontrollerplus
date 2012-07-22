package com.nxtcontrollerplus.program;

import java.util.ArrayList;

import com.nxtcontrollerplus.enums.ActiveScreen;
import com.nxtcontrollerplus.program.btmessages.commands.GetBatteryLevel;
import com.nxtcontrollerplus.program.btmessages.commands.GetInputValues;
import com.nxtcontrollerplus.program.btmessages.commands.GetOutputState;
import com.nxtcontrollerplus.program.btmessages.commands.LSRead;
import com.nxtcontrollerplus.program.sensors.I2CSensor;
import com.nxtcontrollerplus.program.sensors.Sensor;
import com.nxtcontrollerplus.program.sensors.UltrasonicSensor;
import com.nxtcontrollerplus.program.utils.Converter;

public class SensorManager{
	
	private ArrayList<Byte[]> firstScreenCommands = null;
	private ArrayList<Byte[]> secondScreenCommands = null;
	private ArrayList<Sensor> sensorList = null;
	private SensorRefresher refresher = null;
	private ActiveScreen activeScreen;
	
	private NXTCommunicator nxtCommunicator = null;
	
	public void setActiveScreen(ActiveScreen activeScreen){
		this.activeScreen = activeScreen;
		stopReadingSensorData();
		startReadingSensorData();
	}

	/**
	 * generate commands for refreshing digital sensors and battery level
	 * this sensor are shown only on the first screen
	 */
	private void setUpFirstScreenCommands(){
		this.firstScreenCommands.clear();
		firstScreenCommands.add(Converter.bytesArrayConverter(new GetBatteryLevel().getBytes()));
		
		for(Sensor s:sensorList){
			if(!(s instanceof I2CSensor)){
				GetInputValues giv = new GetInputValues(s.getPort());
				firstScreenCommands.add(Converter.bytesArrayConverter(giv.getBytes()));
			}
		}
	}
	
	/**
	 * generate commands for refreshing I2C sensors like compass and ultrasonic
	 * this sensor are shown only on the second screen
	 */
	private void setUpSecondScreenCommands(){
		this.secondScreenCommands.clear();
		for(Sensor s:sensorList){
			if(s instanceof I2CSensor){
				
				I2CSensor temp = (I2CSensor) s;	
				byte[] one = temp.getLSWriteCommand().getBytes();
				LSRead lr = new LSRead(temp.getPort());
				byte[] two = lr.getBytes();
				byte[] merged = Converter.mergeByteArrays(one, two);
				secondScreenCommands.add(Converter.bytesArrayConverter(merged));
				
				//if ultrasonic is connected then add we have rotation sensor data from third motor for radar
				if(s instanceof UltrasonicSensor){
					GetOutputState get = new GetOutputState(nxtCommunicator.getThirdMotor());
					secondScreenCommands.add(Converter.bytesArrayConverter(get.getBytes()));
				}
			}
		}
	}
	
	private void setUpAutoRefreshedCommands(){
		setUpFirstScreenCommands();
		setUpSecondScreenCommands();
	}
	
	public void addSensor(Sensor sensor){
		if(sensor.getPort()<0 || sensor.getPort() > 3)
			throw new UnsupportedOperationException("Port: "+Byte.toString(sensor.getPort() )+", only <0-3> ports are legal.");
		int found = -1;
		for(int i=0; i<sensorList.size();i++){
			if(sensorList.get(i).getPort()==sensor.getPort()){
				found = i;
				break;
			}
		}
		
		if(found == -1){
			sensorList.add(sensor);
		}else{
			sensorList.set(found,sensor);
		}
	}
	
	public Sensor getDigitalSensor(byte inputPort){
		for(Sensor s:sensorList){
			if(s.getPort()==inputPort && !(s instanceof I2CSensor))
				return s;
		}
		return null; 
	}
	
	public synchronized ArrayList<I2CSensor> getI2CSensors(){
		ArrayList<I2CSensor> temp = new ArrayList<I2CSensor>();
		for(Sensor s:sensorList){
			if(s instanceof I2CSensor)
				temp.add((I2CSensor)s);
		}
		return temp; 
	}
	
	public void startReadingSensorData(){
		setUpAutoRefreshedCommands();
		if(activeScreen.equals(ActiveScreen.First)){
			refresher = new SensorRefresher(firstScreenCommands, sensorList);
		}else if(activeScreen.equals(ActiveScreen.Second)){
			refresher = new SensorRefresher(secondScreenCommands, sensorList);
		}
		refresher.start();
	}
	
	public void stopReadingSensorData(){
		if(refresher != null){
			refresher.unregisterSensors();
			SensorRefresher.setRunning(false);
			refresher = null;
		}
	}
	
	public SensorManager(NXTCommunicator nxtCommunicator){
		this.nxtCommunicator = nxtCommunicator;
		this.firstScreenCommands =  new ArrayList<Byte[]>();
		this.secondScreenCommands =  new ArrayList<Byte[]>();
		this.sensorList = new ArrayList<Sensor>();	
		this.activeScreen = ActiveScreen.First;
	}
	
}

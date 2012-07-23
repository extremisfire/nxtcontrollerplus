package com.nxtcontrollerplus.program;

import java.util.ArrayList;

import android.os.Handler;
import com.nxtcontrollerplus.enums.ActiveScreen;
import com.nxtcontrollerplus.enums.ConnectionStatus;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorType;
import com.nxtcontrollerplus.program.btmessages.commands.GetBatteryLevel;
import com.nxtcontrollerplus.program.btmessages.commands.GetInputValues;
import com.nxtcontrollerplus.program.btmessages.commands.GetOutputState;
import com.nxtcontrollerplus.program.btmessages.commands.LSRead;
import com.nxtcontrollerplus.program.btmessages.commands.SetInputMode;
import com.nxtcontrollerplus.program.sensors.I2CSensor;
import com.nxtcontrollerplus.program.sensors.Sensor;
import com.nxtcontrollerplus.program.sensors.UltrasonicSensor;
import com.nxtcontrollerplus.program.utils.Converter;


public class SensorManager{
	
	public static final int REFRESH_INTERVAL = 35; //ms 
	
	private ArrayList<Command> firstScreenCommands = null;
	private ArrayList<Command> secondScreenCommands = null;
	private ArrayList<Sensor> sensorList = null;
	private ActiveScreen activeScreen = null;
	private Handler sensorRefresherHandler = null;
	
	public Handler getSensorRefresherHandler() {
		return sensorRefresherHandler;
	}

	public void setSensorRefresherHandler(Handler sensorRefresherHandler) {
		this.sensorRefresherHandler = sensorRefresherHandler;
	}

	public void setActiveScreen(ActiveScreen activeScreen){
		this.activeScreen = activeScreen;
	}

	/**
	 * generate commands for refreshing digital sensors and battery level
	 * this sensor are shown only on the first screen
	 */
	private void setUpFirstScreenCommands(){
		this.firstScreenCommands.clear();
		Command command = null;
		command = new Command(Converter.bytesArrayConverter(new GetBatteryLevel().getBytes()), sensorRefresherHandler);
		firstScreenCommands.add(command);
		
		for(Sensor s:sensorList){
			if(!(s instanceof I2CSensor)){
				GetInputValues giv = new GetInputValues(s.getPort());
				command = new Command(Converter.bytesArrayConverter(giv.getBytes()), sensorRefresherHandler);
				firstScreenCommands.add(command);
			}
		}
	}
	
	/**
	 * generate commands for refreshing I2C sensors like compass and ultrasonic
	 * this sensor are shown only on the second screen
	 */
	private void setUpSecondScreenCommands(){
		this.secondScreenCommands.clear();
		Command command = null;
		for(Sensor s:sensorList){
			if(s instanceof I2CSensor){
				
				I2CSensor temp = (I2CSensor) s;	
				byte[] one = temp.getLSWriteCommand().getBytes();
				LSRead lr = new LSRead(temp.getPort());
				byte[] two = lr.getBytes();
				byte[] merged = Converter.mergeByteArrays(one, two);
				command  = new Command(Converter.bytesArrayConverter(merged), sensorRefresherHandler);
				secondScreenCommands.add(command);
				
				//if ultrasonic is connected then add we have rotation sensor data from third motor for radar
				if(s instanceof UltrasonicSensor){
					GetOutputState get = new GetOutputState(NXTCommunicator.getInstance().getThirdMotor());
					command = new Command(Converter.bytesArrayConverter(get.getBytes()), sensorRefresherHandler);
					secondScreenCommands.add(command);
				}
			}
		}
	}
	
	private void setUpAutoRefreshedCommands(){
		setUpFirstScreenCommands();
		setUpSecondScreenCommands();
		setSensorRefresherHandler(NXTCommunicator.getInstance().getMessageHandler());
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
		setUpAutoRefreshedCommands();
	}
	
	public Sensor getDigitalSensor(byte inputPort){
		for(Sensor s:sensorList){
			if(s.getPort()==inputPort && !(s instanceof I2CSensor))
				return s;
		}
		return null; 
	}
	
	private void registerSensors(){
		while(NXTCommunicator.getInstance().getState() != ConnectionStatus.CONNECTED);
		for(Sensor s:sensorList){
			s.initialize();
		}
	}
	
	public void unregisterSensors(){
		byte[] temp = null;
		SetInputMode sim = new SetInputMode((byte)0, SensorType.NO_SENSOR);
		temp =sim.getBytes();
		for(byte i = 1; i<4;i++){
			sim = new SetInputMode(i, SensorType.NO_SENSOR);
			temp = Converter.mergeByteArrays(temp, sim.getBytes());
		}
		NXTCommunicator.getInstance().write(temp);
	}
	
	public ArrayList<I2CSensor> getI2CSensors(){
		ArrayList<I2CSensor> temp = new ArrayList<I2CSensor>();
		for(Sensor s:sensorList){
			if(s instanceof I2CSensor)
				temp.add((I2CSensor)s);
		}
		return temp; 
	}
	
	public void startReadingSensorData(){
		registerSensors();

		if(activeScreen == ActiveScreen.First){
			for(Command c:firstScreenCommands){
				sensorRefresherHandler.postDelayed(c, REFRESH_INTERVAL);
			}
		}else if(activeScreen == ActiveScreen.Second){
			for(Command c:secondScreenCommands){
				sensorRefresherHandler.postDelayed(c, REFRESH_INTERVAL);
			}
		}	
	}
	
	public void stopReadingSensorData(){
		unregisterSensors();
		
		if(activeScreen == ActiveScreen.First){
			for(Command c:firstScreenCommands){
				sensorRefresherHandler.removeCallbacks(c);
			}
		}else if(activeScreen == ActiveScreen.Second){
			for(Command c:secondScreenCommands){
				sensorRefresherHandler.removeCallbacks(c);
			}
		}	
	}
	

	
	public SensorManager(){
		this.firstScreenCommands =  new ArrayList<Command>();
		this.secondScreenCommands =  new ArrayList<Command>();
		this.sensorList = new ArrayList<Sensor>();	
		this.activeScreen = ActiveScreen.First;
	}
	
}

package nxtcontroller.program;

import java.util.ArrayList;
import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.btmessages.commands.DirectCommand;
import nxtcontroller.program.btmessages.commands.GetBatteryLevel;
import nxtcontroller.program.btmessages.commands.GetInputValues;
import nxtcontroller.program.btmessages.commands.LSRead;
import nxtcontroller.program.btmessages.commands.SetInputMode;
import nxtcontroller.program.sensors.I2CSensor;
import nxtcontroller.program.sensors.Sensor;
import android.util.Log;

public class SensorManager extends Thread{
	
	private static final int REFRESH_INTERVAL = 50; //ms 
	
	private NXTCommunicator nxtCommunicator;
	private boolean isRunning;
	private int counter;
	private ArrayList<DirectCommand> autoRefreshedCommands;
	private ArrayList<Sensor> sensorList;
	
	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
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
	
	public Sensor getSensor(byte inputPort){
		for(Sensor s:sensorList){
			if(s.getPort()==inputPort && !(s instanceof I2CSensor))
				return s;
		}
		return null; 
	}
	
	public ArrayList<I2CSensor> getI2CSensors(){
		ArrayList<I2CSensor> temp = new ArrayList<I2CSensor>();
		for(Sensor s:sensorList){
			if(s instanceof I2CSensor)
				temp.add((I2CSensor)s);
		}
		return temp; 
	}
	
	public void unregisterSensors(){
		for(Sensor s:sensorList){
			SetInputMode sim = new SetInputMode(s.getPort(), SensorType.NO_SENSOR);
			nxtCommunicator.write(sim.getBytes());
		}
	}
	
	private void setUpAutoRefreshedCommands(){
		autoRefreshedCommands.clear();
		autoRefreshedCommands.add(new GetBatteryLevel());
		for(Sensor s:sensorList){
			if(s instanceof I2CSensor){
				I2CSensor temp = (I2CSensor) s;
				autoRefreshedCommands.add(temp.getLSWriteCommand());
				autoRefreshedCommands.add(new LSRead(temp.getPort()));
			}else{
				autoRefreshedCommands.add(new GetInputValues(s.getPort()));
			}
		}
	}
	
	private void sendCommandToNXT(int commandNumber){
		try {
			Log.d(MainActivity.TAG,"sending: "+commandNumber);
			nxtCommunicator.write(autoRefreshedCommands.get(commandNumber).getBytes());
		} catch (Exception e) {
			Log.e(MainActivity.TAG, "send command",e);
		}
	}
	
	public SensorManager(NXTCommunicator nxtCommunicator){
		this.nxtCommunicator = nxtCommunicator;
		isRunning = false;
		autoRefreshedCommands =  new ArrayList<DirectCommand>();
		sensorList = new ArrayList<Sensor>();		
	}
	
	private void initializeSensors(){
		for(Sensor s:sensorList){
			s.initialize();
		}
	}

	@Override
	public void start(){
		isRunning = true;
		counter = 0;
		super.start();
		setUpAutoRefreshedCommands();
		try {
			sleep(500);
		} catch (InterruptedException e) {}
		initializeSensors();
	}
	
	@Override
	public void run(){
		while(isRunning){
			try{
				sendCommandToNXT(counter % autoRefreshedCommands.size());
				counter++;
				sleep(REFRESH_INTERVAL);
			}catch(Exception e){
				//do nothing
			}
		}
	}


}

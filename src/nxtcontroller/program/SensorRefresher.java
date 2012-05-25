package nxtcontroller.program;

import java.util.ArrayList;

import android.util.Log;

import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.btmessages.commands.SetInputMode;
import nxtcontroller.program.sensors.Sensor;
import nxtcontroller.program.utils.Converter;

public class SensorRefresher extends Thread{
	
	private static final int REFRESH_INTERVAL = 35; //ms 
	
	private ArrayList<Byte[]> autoRefreshedCommands;
	private ArrayList<Sensor> sensorList;	
	
	private NXTCommunicator nxtCommunicator = NXTCommunicator.getInstance();
	private volatile boolean isRunning;
	private int counter;

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public SensorRefresher(ArrayList<Byte[]> autoRefreshedCommands, ArrayList<Sensor> sensorList){
		this.autoRefreshedCommands = autoRefreshedCommands;
		this.sensorList = sensorList;
	}
	
	private void initializeSensors(){
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
		nxtCommunicator.write(temp);
	}
	
	private void sendCommandToNXT(int commandNumber){
		try {
			Log.d(MainActivity.TAG,"sending: "+commandNumber);
			Byte[] temp = autoRefreshedCommands.get(commandNumber);		
			nxtCommunicator.write(Converter.bytesArrayConverter(temp));
		} catch (Exception e) {
			Log.e(MainActivity.TAG, "send command",e);
		}
	}
	
	@Override
	public void start(){
		isRunning = true;
		counter = 0;
		super.start();
		try {
			sleep(REFRESH_INTERVAL);
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

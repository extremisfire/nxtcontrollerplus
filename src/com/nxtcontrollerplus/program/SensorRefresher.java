package com.nxtcontrollerplus.program;

import java.util.ArrayList;

import android.util.Log;

import com.nxtcontrollerplus.activity.MainActivity;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorType;
import com.nxtcontrollerplus.program.btmessages.commands.SetInputMode;
import com.nxtcontrollerplus.program.sensors.Sensor;
import com.nxtcontrollerplus.program.utils.Converter;

public class SensorRefresher extends Thread{
	
	private static final int REFRESH_INTERVAL = 35; //ms 
	
	private ArrayList<Byte[]> autoRefreshedCommands;
	private ArrayList<Sensor> sensorList;	
	
	private NXTCommunicator nxtCommunicator = NXTCommunicator.getInstance();
	private int counter;
	
	private static boolean isRunning = false;
	
	public static void setRunning(boolean isRunning) {
		SensorRefresher.isRunning = isRunning;
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
		SensorRefresher.isRunning = true;
		counter = 0;
		super.start();
		try {
			sleep(REFRESH_INTERVAL);
		} catch (InterruptedException e) {}
		initializeSensors();
	}
	
	@Override
	public void run(){
		while(SensorRefresher.isRunning){
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

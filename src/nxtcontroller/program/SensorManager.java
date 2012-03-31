package nxtcontroller.program;

import nxtcontroller.activity.MainActivity;
import android.os.Handler;
import android.util.Log;

public class SensorManager extends Thread{
	
	public static final int maxVoltage = 8200; // full battery volatage in mV
	private NXTCommunicator nxtCommunicator;
	private Handler messageHandler;
	private boolean isRunning;
	
	
	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	private void getBatteryLevel(){
		byte[] command = { 0x02, 0x00, 0x00, 0x0B };
		nxtCommunicator.write(command);
		Log.d(MainActivity.TAG,"battery level get: "+NXTCommunicator.bytesToString(command));
	}
	
	public SensorManager(Handler messageHandler, NXTCommunicator nxtCommunicator){
		this.messageHandler = messageHandler;
		this.nxtCommunicator = nxtCommunicator;
		isRunning = false;
	}
	
	@Override
	public void start(){
		isRunning = true;
		super.start();
	}
	
	@Override
	public void run(){
		while(isRunning){
			try{
				getBatteryLevel();
				sleep(5000);
			}catch(Exception e){
				//
			}
		}
	}
	

}

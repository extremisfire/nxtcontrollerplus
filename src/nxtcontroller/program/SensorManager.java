package nxtcontroller.program;

import nxtcontroller.activity.MainActivity;
import nxtcontroller.program.btmessages.commands.GetBatteryLevel;
import android.util.Log;

public class SensorManager extends Thread{
	
	private NXTCommunicator nxtCommunicator;
	private boolean isRunning;
	
	
	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	private void getBatteryLevel(){
		GetBatteryLevel gb = new GetBatteryLevel();
		
		nxtCommunicator.write(gb.getBytes());
		Log.d(MainActivity.TAG, gb.toString());
	}
	
	public SensorManager(NXTCommunicator nxtCommunicator){
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

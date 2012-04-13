package nxtcontroller.program;

import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.nxtbuiltin.SensorMode;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.btmessages.commands.GetBatteryLevel;
import nxtcontroller.program.btmessages.commands.GetInputValues;
import nxtcontroller.program.btmessages.commands.SetInputMode;
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
		//TODO
		GetBatteryLevel gb = new GetBatteryLevel();
		
		nxtCommunicator.write(gb.getBytes());
		Log.d(MainActivity.TAG, gb.toString());
	}
	
	private void refreshValues(){
		SetInputMode sim = new SetInputMode((byte)0, SensorType.SWITCH,SensorMode.BOOLEAN_MODE);
		nxtCommunicator.write(sim.getBytes());
		Log.d(MainActivity.TAG,sim.toString());
		//TODO
		GetInputValues giv = new GetInputValues((byte)0);
		
		nxtCommunicator.write(giv.getBytes());
		Log.d(MainActivity.TAG, giv.toString());
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
				//getBatteryLevel();
				refreshValues();
				sleep(70);
			}catch(Exception e){
				//
			}
		}
	}
	

}

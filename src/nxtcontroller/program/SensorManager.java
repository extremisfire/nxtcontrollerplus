package nxtcontroller.program;

import nxtcontroller.activity.MainActivity;
import nxtcontroller.program.btmessages.commands.GetBatteryLevel;
import nxtcontroller.program.btmessages.commands.PlayTone;
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
		byte[] temp = new byte[SlovakAnthem.NUM_OF_TONES*8];
		/*for(int i=0; i < SlovakAnthem.NUM_OF_TONES;i++){
			PlayTone pt = new PlayTone((short) (SlovakAnthem.frequency[i]+1000), SlovakAnthem.duration[i]);
			System.arraycopy(pt.getBytes(), 0, temp,i*(pt.getBytes().length),pt.getBytes().length);
			Log.d(MainActivity.TAG,pt.toString());
		}
		*/
		PlayTone pt = new PlayTone((short)440, (short) 1000);
		nxtCommunicator.write(pt.getBytes());
		Log.d(MainActivity.TAG,pt.toString());
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

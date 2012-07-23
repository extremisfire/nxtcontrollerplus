package com.nxtcontrollerplus.program;

import com.nxtcontrollerplus.activity.MainActivity;
import com.nxtcontrollerplus.program.utils.Converter;

import android.os.Handler;
import android.util.Log;

public class Command implements Runnable{
	
	private Byte[] command = null;
	private Handler commandHandler = null;
	private NXTCommunicator nxtCommunicator = NXTCommunicator.getInstance();
	
	public Command(Byte[] command, Handler commandHandler){
		this.command = command;
		this.commandHandler = commandHandler;
	}

	public void run() {
		try{
			Log.d(MainActivity.TAG,"sending: " + Converter.bytesToString(this.command));
			nxtCommunicator.write(Converter.bytesArrayConverter(this.command));
			commandHandler.postDelayed(this, SensorManager.REFRESH_INTERVAL);
		}catch(Exception e){
			Log.e(MainActivity.TAG,"sending command ERROR",e);
		}
	}

}

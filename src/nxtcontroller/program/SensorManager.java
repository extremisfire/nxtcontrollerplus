package nxtcontroller.program;

import java.util.ArrayList;
import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.nxtbuiltin.InputPort;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.btmessages.commands.DirectCommand;
import nxtcontroller.program.btmessages.commands.GetInputValues;
import nxtcontroller.program.btmessages.commands.LSGetStatus;
import nxtcontroller.program.btmessages.commands.LSRead;
import nxtcontroller.program.btmessages.commands.LSWrite;
import nxtcontroller.program.btmessages.commands.SetInputMode;
import nxtcontroller.program.sensors.Sensor;
import nxtcontroller.program.sensors.UltrasonicSensor;
import android.util.Log;

public class SensorManager extends Thread{
	
	private static final int REFRESH_INTERVAL = 50; //ms 
	private static final int NUMBER_OF_SENSOR_PORTS = 4;
	
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
		sensor.initialize();
	}
	
	public Sensor getSensor(byte inputPort){
		for(Sensor s:sensorList){
			if(s.getPort()==inputPort)
				return s;
		}
		return null; 
	}
	
	public void unregisterSensors(){
		for(int i=0; i < NUMBER_OF_SENSOR_PORTS;i++){
			SetInputMode sim = new SetInputMode((byte)i, SensorType.NO_SENSOR);
			nxtCommunicator.write(sim.getBytes());
		}
	}
	
	private void setUpAutoRefreshedCommands(){
		autoRefreshedCommands.clear();
		//autoRefreshedCommands.add(new GetBatteryLevel());

		//autoRefreshedCommands.add(new LSGetStatus(InputPort.PORT3));
		
		byte[] tx = {0x02, 0x42}; 
		LSWrite lw = new LSWrite(InputPort.PORT3, tx, (byte)1);
		lw.setRequireResponseToOn();
		autoRefreshedCommands.add(lw);
		autoRefreshedCommands.add(new LSRead(InputPort.PORT3));
		for(int i=0; i < NUMBER_OF_SENSOR_PORTS;i++){
			GetInputValues giv = new GetInputValues((byte)i);
			//autoRefreshedCommands.add(giv);
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
		setUpAutoRefreshedCommands();
	}
	
	private void initializeSensors(){
		for(Sensor s:sensorList){
			if(s instanceof UltrasonicSensor){
				try {
					s.initialize();
					sleep(500);
				} catch (Exception e) {}
			}else{
				s.initialize();
			}
		}
	}

	@Override
	public void start(){
		isRunning = true;
		counter = 0;
		super.start();
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

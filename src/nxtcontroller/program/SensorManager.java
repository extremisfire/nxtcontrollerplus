package nxtcontroller.program;

import java.util.ArrayList;
import nxtcontroller.enums.nxtbuiltin.SensorType;
import nxtcontroller.program.btmessages.commands.GetBatteryLevel;
import nxtcontroller.program.btmessages.commands.GetInputValues;
import nxtcontroller.program.btmessages.commands.LSRead;
import nxtcontroller.program.btmessages.commands.SetInputMode;
import nxtcontroller.program.sensors.I2CSensor;
import nxtcontroller.program.sensors.Sensor;
import nxtcontroller.program.utils.Converter;

public class SensorManager{
	
	private NXTCommunicator nxtCommunicator;
	private ArrayList<Byte[]> autoRefreshedCommands;
	private ArrayList<Sensor> sensorList;
	private SensorRefresher refresher;

	private byte[] mergeByteArrays(byte[] arrayOne, byte[] arrayTwo){
		byte[] temp = new byte[arrayOne.length+arrayTwo.length];
		System.arraycopy(arrayOne, 0, temp, 0, arrayOne.length);
		System.arraycopy(arrayTwo, 0, temp, arrayOne.length, arrayTwo.length);
		return temp;
	}
	
	private void setUpAutoRefreshedCommands(){
		autoRefreshedCommands.clear();
		autoRefreshedCommands.add(Converter.bytesArrayConverter(new GetBatteryLevel().getBytes()));
		
		for(Sensor s:sensorList){
			if(s instanceof I2CSensor){
				I2CSensor temp = (I2CSensor) s;
				
				byte[] one = temp.getLSWriteCommand().getBytes();
				LSRead lr = new LSRead(temp.getPort());
				byte[] two = lr.getBytes();
				byte[] merged = mergeByteArrays(one, two);
				
				autoRefreshedCommands.add(Converter.bytesArrayConverter(merged));
			}else{
				GetInputValues giv = new GetInputValues(s.getPort());
				autoRefreshedCommands.add(Converter.bytesArrayConverter(giv.getBytes()));
			}
		}
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
	
	public synchronized ArrayList<I2CSensor> getI2CSensors(){
		ArrayList<I2CSensor> temp = new ArrayList<I2CSensor>();
		for(Sensor s:sensorList){
			if(s instanceof I2CSensor)
				temp.add((I2CSensor)s);
		}
		return temp; 
	}
	
	public synchronized void unregisterSensors(){
		for(Sensor s:sensorList){
			SetInputMode sim = new SetInputMode(s.getPort(), SensorType.NO_SENSOR);
			nxtCommunicator.write(sim.getBytes());
		}
	}
	
	public void startReadingSensorData(){
		setUpAutoRefreshedCommands();
		refresher = new SensorRefresher(autoRefreshedCommands, sensorList, nxtCommunicator);
		refresher.setRunning(true);
		refresher.start();
	}
	
	public void stopReadingSensorData(){
		if(refresher != null){
			refresher.setRunning(false);
			refresher = null;
		}
	}
	
	public SensorManager(NXTCommunicator nxtCommunicator){
		this.nxtCommunicator = nxtCommunicator;
		autoRefreshedCommands =  new ArrayList<Byte[]>();
		sensorList = new ArrayList<Sensor>();		
	}
	
}

package nxtcontroller.program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import nxtcontroller.activity.MainActivity;
import nxtcontroller.activity.SettingsActivity;
import nxtcontroller.enums.ConnectionStatus;
import nxtcontroller.enums.Keys;
import nxtcontroller.enums.TypeOfMessage;
import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.InputPort;
import nxtcontroller.enums.nxtbuiltin.Motor;
import nxtcontroller.enums.nxtbuiltin.SensorID;
import nxtcontroller.program.btmessages.commands.SetOutputState;
import nxtcontroller.program.btmessages.returns.packages.GetBatteryLevelReturnPackage;
import nxtcontroller.program.btmessages.returns.packages.GetInputValuesReturnPackage;
import nxtcontroller.program.btmessages.returns.packages.GetOutputStateReturnPackage;
import nxtcontroller.program.btmessages.returns.packages.LSGetStatusReturnPackage;
import nxtcontroller.program.btmessages.returns.packages.LSReadReturnPackages;
import nxtcontroller.program.btmessages.returns.packages.ReturnPackage;
import nxtcontroller.program.sensors.CompassSensor;
import nxtcontroller.program.sensors.I2CSensor;
import nxtcontroller.program.sensors.LightSensor;
import nxtcontroller.program.sensors.Sensor;
import nxtcontroller.program.sensors.SoundSensor;
import nxtcontroller.program.sensors.TouchSensor;
import nxtcontroller.program.sensors.UltrasonicSensor;
import nxtcontroller.program.utils.Converter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Singleton pattern class
 * This class provides communication with NXT brick,
 * create socket for communication with NXT
 * send to commands to NXT 
 * receive messages from NXT
 * change connection statutes in UI activity
*/
public final class NXTCommunicator {
	
	/* Singleton instance variable */
	private static volatile NXTCommunicator instance = null;
	
	/* declaration constant values */
	public static final String MyUUID = "00001101-0000-1000-8000-00805F9B34FB";
	private static final int MAX_LENGHT_OF_BT_MESSAGE = 64+2;
	
	/* private class properties declaration */
	private ConnectThread mConnectThread = null;
    private ConnectedThread mConnectedThread = null;
    private Handler messageHandler = null;
    private MainActivity mainActivity = null;
    private BluetoothDevice NXTdevice = null;
    private HashMap<Byte,Integer> connectedSensors = null;
    private byte leftMotor,rightMotor,thirdMotor;
    private SensorManager sensorManager = null;
    /* public class properties declaration */
    
	/* Getters and Setter declaration */
    
	public synchronized void setState(int state) {
		messageHandler.obtainMessage(TypeOfMessage.CONNECTION_STATUS, state).sendToTarget();
	}

	public SensorManager getSensorManager() {
		return sensorManager;
	}

	public void setMessageHandler(Handler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public synchronized int getState(){
		return  mainActivity.getConnectionStatus();
	}
	
	public byte getLeftMotor() {
		return leftMotor;
	}

	public void setLeftMotor(byte leftMotor) {
		if(leftMotor > 2 || leftMotor < 0)
			return;
		this.leftMotor = leftMotor;
		setThirdMotor();
	}

	public byte getRightMotor() {
		return rightMotor;
	}

	public void setRightMotor(byte rightMotor) {
		if(rightMotor > 2 || rightMotor < 0)
			return;
		this.rightMotor = rightMotor;
		setThirdMotor();
	}

	public byte getThirdMotor() {
		return thirdMotor;
	}

	public void setThirdMotor() {
		if(getLeftMotor() == 0 && getRightMotor() == 1)
			this.thirdMotor = Motor.C;
		if(getLeftMotor() == 0 && getRightMotor() == 2)
			this.thirdMotor = Motor.B;
		if(getLeftMotor() == 1 && getRightMotor() == 0)
			this.thirdMotor = Motor.C;
		if(getLeftMotor() == 1 && getRightMotor() == 2)
			this.thirdMotor = Motor.A;
		if(getLeftMotor() == 2 && getRightMotor() == 0)
			this.thirdMotor = Motor.B;
		if(getLeftMotor() == 2 && getRightMotor() == 1)
			this.thirdMotor = Motor.A;
	}

	public HashMap<Byte, Integer> getConnectedSensors() {
		return connectedSensors;
	}
	
	/* Methods and Constructors declaration */
	
	@SuppressWarnings("unused")
	private void extractCommandsFromBuffer(byte[] buffer){
		//TODO
		//LinkedList<Byte[]> commands = new LinkedList<Byte[]>();
		int pos = 0;
		final byte offset = 2;//length in BT msg is counted without two bytes on the front
		byte readedLength = (byte) (buffer[pos]);
		
		while(readedLength != 0){
			byte[] temp = new byte[readedLength+offset];
			System.arraycopy(buffer, pos, temp, 0, temp.length);
			//commands.offer(temp);
			//Log.d(MainActivity.TAG, "Extract Commands:\n"+Converter.bytesToString(temp)+"\n");
			parseReturnPackage(temp);
			pos += temp.length;
			readedLength = (byte) (buffer[pos]);
		}
		//for(Byte[] b:commands){
			
		//}
	}
	
	private void parseReturnPackage(byte[] bytes){
		try{
			ReturnPackage pack = new ReturnPackage(bytes);
			switch (pack.getType()) {
			case CommandType.GET_BATTERY_LEVEL:
				GetBatteryLevelReturnPackage batteryLevel = new GetBatteryLevelReturnPackage(bytes);
				Log.d(MainActivity.TAG,batteryLevel.toString());
				messageHandler.obtainMessage(TypeOfMessage.BATTERY_LEVEL,(int)batteryLevel.getBatteryLevel()).sendToTarget();
				break;
			case CommandType.GET_INPUT_VALUES:
				GetInputValuesReturnPackage inputValues = new GetInputValuesReturnPackage(bytes);
				Log.d(MainActivity.TAG,inputValues.toString());
				getValuesFromDigitalSensor(inputValues);
				break;
			case CommandType.GET_OUTPUT_STATE:
				GetOutputStateReturnPackage outputState = new GetOutputStateReturnPackage(bytes);
				Log.d(MainActivity.TAG,outputState.toString());
				break;
			case CommandType.LS_READ:
				LSReadReturnPackages lsRead = new LSReadReturnPackages(bytes);
				Log.d(MainActivity.TAG,lsRead.toString());
				getValuesFromI2CSensor(lsRead);
				break;
			case CommandType.LS_GET_STATUS:
				LSGetStatusReturnPackage lsGetStatus = new LSGetStatusReturnPackage(bytes);
				Log.d(MainActivity.TAG,lsGetStatus.toString());
				break;
			default:
				Log.d(MainActivity.TAG,pack.toString());
				break;
			}
		
		}catch(Exception e){
			Log.e(MainActivity.TAG,"parse return msgs",e);
		}
	}
	
	private void getValuesFromI2CSensor(LSReadReturnPackages values){
		//String msg = "";
		synchronized (this) {
			ArrayList<I2CSensor> temp = sensorManager.getI2CSensors();
			
			for(I2CSensor s:temp){
				s.refreshSensorData(values);
				//msg = s.toString();
				//TODO
			}
		}
	}
	
	private void getValuesFromDigitalSensor(GetInputValuesReturnPackage values){
		Sensor sensor = null;
		int port = values.getInputPort();
		synchronized (this) {
			sensor = sensorManager.getDigitalSensor((byte)port);	
		}
		sensor.refreshSensorData(values);
		Message msg = messageHandler.obtainMessage(TypeOfMessage.SENSOR_DATA);
	    msg.arg1 = sensor.getPort();
	    msg.arg2 = sensor.getMeasuredData();
		msg.sendToTarget();	
	}
	
	public static synchronized NXTCommunicator getInstance() {
		if (instance == null) {
			instance = new NXTCommunicator();
		}
		return instance;
	}
	
	private NXTCommunicator(){
		this.sensorManager = new SensorManager();
		this.connectedSensors = new HashMap<Byte, Integer>();
	}
	
	/**
	 * add sensor to map of connectedSensors
	 * @param portNumber 0-3
	 * @param sensor - one of supported sensors 
	 * @return if sensorPort had already attached sensor return this sensor and replace old with new else returns null
	 */
	private Integer connectSensorToPort(byte portNumber,final Integer keyForSensorType){
		try {
			switch (keyForSensorType) {
			case SensorID.TOUCH_SENSOR:
				sensorManager.addSensor(new TouchSensor(portNumber));	
				mainActivity.setUpSensorView(portNumber,keyForSensorType);
			break;
			case SensorID.SOUND_SENSOR:
				sensorManager.addSensor(new SoundSensor(portNumber));
				mainActivity.setUpSensorView(portNumber,keyForSensorType);
			break;
			case SensorID.LIGHT_SENSOR:
				sensorManager.addSensor(new LightSensor(portNumber));
				mainActivity.setUpSensorView(portNumber,keyForSensorType);
			break;
			case SensorID.ULTRASONIC_SENSOR:
				sensorManager.addSensor(new UltrasonicSensor(portNumber));
				//TODO
			break;
			case SensorID.COMPASS_SENSOR:
				sensorManager.addSensor(new CompassSensor(portNumber));
				//TODO
			break;
			case SensorID.NO_SENSOR:
				mainActivity.setUpSensorView(portNumber,keyForSensorType);
			break;	
			}
		} catch (Exception e) {
			Log.e(MainActivity.TAG, "connecting sensors", e);
			return null;
		}
		
		return connectedSensors.put(portNumber, keyForSensorType);
	}
	
	public void loadFromPreferences(){
		SharedPreferences currentSettings = this.mainActivity.getSharedPreferences(SettingsActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
		
		setLeftMotor((byte)currentSettings.getInt(Keys.MOTOR_LEFT, Motor.A));
		setRightMotor((byte)currentSettings.getInt(Keys.MOTOR_RIGHT, Motor.B));
		
		if(currentSettings.contains(Keys.SENSOR_1)){
			connectSensorToPort(InputPort.PORT1, currentSettings.getInt(Keys.SENSOR_1, SensorID.NO_SENSOR));
		}
		if(currentSettings.contains(Keys.SENSOR_2)){
			connectSensorToPort(InputPort.PORT2, currentSettings.getInt(Keys.SENSOR_2, SensorID.NO_SENSOR));
		}
		if(currentSettings.contains(Keys.SENSOR_3)){
			connectSensorToPort(InputPort.PORT3, currentSettings.getInt(Keys.SENSOR_3, SensorID.NO_SENSOR));
		}
		if(currentSettings.contains(Keys.SENSOR_4)){
			connectSensorToPort(InputPort.PORT4, currentSettings.getInt(Keys.SENSOR_4, SensorID.NO_SENSOR));
		}
	}
	
	/**
	 * creates new Thread which try to connect to NXT if fails reconnection
	 * will be started automatically
	 * @param remoteDevice - BT device NXT which you want to connect 
	 */
	public synchronized void connectToNXT(BluetoothDevice remoteDevice){
		loadFromPreferences();
		if (getState() == ConnectionStatus.CONNECTING) {
            if (mConnectThread != null){
            	mConnectThread.cancel(); 
            	mConnectThread = null;
            	}
        }
        if (mConnectedThread != null){
        		mConnectedThread.cancel(); 
        		mConnectedThread = null;
        }
        try {
    		this.NXTdevice = remoteDevice;
    		mConnectThread = new ConnectThread(this.NXTdevice);
    		mConnectThread.start();
		} catch (Exception e) {
			Log.e(MainActivity.TAG,"connecting",e);
		}

	}
	
	public synchronized void disconnectFromNXT(){
        try{
        	sensorManager.stopReadingSensorData();
        }catch(Exception e){
        	Log.e(MainActivity.TAG,"stop sensor manager",e);
        }
        
        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }
        if (mConnectedThread != null) {
        	mConnectedThread.cancel();
        	mConnectedThread = null;
        }

        setState(ConnectionStatus.DISCONNECTED);
	}
	
	/**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connectToSocket(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null){
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }
        if (mConnectedThread != null){
    		mConnectedThread.cancel(); 
    		mConnectedThread = null;
        }

        Log.d(MainActivity.TAG,"starting connecting to device");
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        
		setState(ConnectionStatus.CONNECTED);
		try{
			sensorManager.startReadingSensorData();
		}catch(Exception e){
			Log.e(MainActivity.TAG,"sensor man thread",e);
		}
		
    }

    
    /**
     * send a command array of bytes via BT to NXT to move 2 motors
     * @param leftMotorSpeed - speed of first motor range:[-100-100]
     * @param rightMotorSpeed - speed of second motor range:[-100-100]
     */
    public void  move2Motors(byte leftMotorSpeed, byte rightMotorSpeed) {
    	byte[] data1 = generateMoveMotorCommand((byte)getLeftMotor(), (byte)leftMotorSpeed); //command for 1.motor
    	byte[] data2 = generateMoveMotorCommand((byte)getRightMotor(), (byte)rightMotorSpeed); //command for 2.motor
    	
    	//need send this command at once must merge this arrays
    	byte[] command = new byte[data1.length+data2.length];
    	System.arraycopy(data1, 0, command, 0, data1.length);
    	System.arraycopy(data2, 0, command, data1.length, data1.length);
    	
        if(mConnectedThread != null)
        	write(command);
        else
        	Log.d(MainActivity.TAG,"mConnectedThread is NULL");
    }
    
    /**
     * send a command array of bytes via BT to NXT to move 3. motor
     * @param thirdMotorSpeed - speed of motor range:[-100-100]
     */
    public void move3Motor(byte thirdMotorSpeed) {
    	byte[] command = generateMoveMotorCommand((byte)getThirdMotor(), (byte)thirdMotorSpeed); //command for 3.motor
    	
        if(mConnectedThread != null)
        	write(command);
        else
        	Log.d(MainActivity.TAG,"mConnectedThread is NULL");
    }

    public byte[] generateMoveMotorCommand(byte indexOfMotor, byte motorSpeed) {
    	SetOutputState command = new SetOutputState();
    	command.setOutputPort(indexOfMotor);
    	command.setPower(motorSpeed);
    	
        return command.getBytes();
    }
    
    public void stopMove() {
    	this.move2Motors((byte)0, (byte)0);
    	this.move3Motor((byte)0);
    }
    
    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     * @param out
     * @return true if command was send 
     */
    public boolean  write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (getState() != ConnectionStatus.CONNECTED) return false;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        return r.write(out);
    }
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    @SuppressWarnings("unused")
	private synchronized void connectionLost() {
        // Start the service over to restart listening mode
        if(getState() == ConnectionStatus.DISCONNECTED) return;
    	setState(ConnectionStatus.CONNECTION_LOST);
    	
		if(NXTdevice != null)
    		this.connectToNXT(NXTdevice);
    }
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private synchronized void connectionFailed() {
        setState(ConnectionStatus.CONNECTION_FAILED);
    }
	
	
	/**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MyUUID));
            } catch (IOException e) {
            	Log.e(MainActivity.TAG,"socket creation failed" ,e);
            }
            mmSocket = tmp;
    		
        }
        
        @Override
        public void run() {
        	setName("ConnectThread");
            Log.d(MainActivity.TAG,"running connected thread");
            
            
            try {
                mmSocket.connect();
                Log.d(MainActivity.TAG, "connection success");

  
            } catch (IOException e) {
                connectionFailed();
            	Log.d(MainActivity.TAG,"connection failed" ,e);
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    Log.e(MainActivity.TAG,"connection failed" ,e);
                }
                return;
            }
            
            synchronized (NXTCommunicator.this) {
                mConnectThread = null;
            }
           
            connectToSocket(mmSocket, mmDevice);

        }
        
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            	Log.e(MainActivity.TAG,"socket creation canceled" ,e);
            }
        }
    }
    
    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(MainActivity.TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            Log.d(MainActivity.TAG, "run listening");
            byte[] buffer = new byte[MAX_LENGHT_OF_BT_MESSAGE];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    Log.d(MainActivity.TAG,"read from NXT: " + bytes + " bytes");
                    Log.d(MainActivity.TAG,"read from NXT: " + Converter.bytesToString(buffer) + " bytes");
                   // extractCommandsFromBuffer(buffer); TODO
                    parseReturnPackage(buffer);
                    
                } catch (Exception e) {
                    Log.e(MainActivity.TAG, "listenning error",e);
                    // TODO connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public boolean write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                Log.d(MainActivity.TAG, "sending to device: "+ Converter.bytesToString(buffer));
                return true;
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "Exception during write", e);
                return false;
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "close of connect socket failed", e);
            }
        }
    }
	
}



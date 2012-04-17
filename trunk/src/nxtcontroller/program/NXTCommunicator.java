package nxtcontroller.program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.ConnectionStatus;
import nxtcontroller.enums.TypeOfMessage;
import nxtcontroller.enums.nxtbuiltin.CommandType;
import nxtcontroller.enums.nxtbuiltin.InputPort;
import nxtcontroller.program.btmessages.commands.SetOutputState;
import nxtcontroller.program.btmessages.returnpackages.GetBatteryLevelReturnPackage;
import nxtcontroller.program.btmessages.returnpackages.GetInputValuesReturnPackage;
import nxtcontroller.program.btmessages.returnpackages.GetOutputStateReturnPackage;
import nxtcontroller.program.btmessages.returnpackages.ReturnPackage;
import nxtcontroller.program.sensors.LightSensor;
import nxtcontroller.program.sensors.Sensor;
import nxtcontroller.program.sensors.SoundSensor;
import nxtcontroller.program.sensors.TouchSensor;
import nxtcontroller.program.utils.Converter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * This class provides communication with NXT brick,
 * create socket for communication with NXT
 * send to commands to NXT 
 * receive messages from NXT
 * change connection statutes in UI activity
*/
public class NXTCommunicator {
	
	/* declaration constant values */
	public static final String MyUUID = "00001101-0000-1000-8000-00805F9B34FB";
	private static final int MAX_LENGHT_OF_BT_MESSAGE = 64+2;
	
	/* private class properties declaration */
	private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Handler messageHandler;
    private MainActivity mainActivity;
    private BluetoothDevice NXTdevice = null;
    //index of motors HINT:A:0,B:1,C:2
    private byte leftMotor = 0;
    private byte rightMotor = 1;
    private byte thirdMotor = 2;
    private SensorManager sensorManager;
    
    /* public class properties declaration */
    
	/* Getters and Setter declaration */
    
	public synchronized void setState(int state) {
		messageHandler.obtainMessage(TypeOfMessage.CONNECTION_STATUS, state).sendToTarget();
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
	}

	public byte getRightMotor() {
		return rightMotor;
	}

	public void setRightMotor(byte rightMotor) {
		if(rightMotor > 2 || rightMotor < 0)
			return;
		this.rightMotor = rightMotor;
	}

	public byte getThirdMotor() {
		return thirdMotor;
	}

	public void setThirdMotor(byte thirdMotor) {
		if(thirdMotor > 2 || thirdMotor < 0)
			return;
		this.thirdMotor = thirdMotor;
	}

	
	/* Methods and Constructors declaration */
	
	private void handleReturnPackages(byte[] bytes){
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
				getValuesFromSensor(inputValues);
				break;
			case CommandType.GET_OUTPUT_STATE:
				GetOutputStateReturnPackage ouputState = new GetOutputStateReturnPackage(bytes);
				Log.d(MainActivity.TAG,ouputState.toString());
				break;
			default:
				Log.d(MainActivity.TAG,pack.toString());
				break;
			}
		
		}catch(Exception e){
			Log.e(MainActivity.TAG,"handle return msgs",e);
		}
	}
	
	private void getValuesFromSensor(GetInputValuesReturnPackage values){
		//TODO sensor from settings
		String msg = "";
		Sensor sensor = null;
		int port = values.getInputPort();
		
		sensor = sensorManager.getSensor((byte)port);
		if(sensor == null){
		  	msg = "No connected"; 
		}else{
		   	sensor.refreshSensorData(values);
		    msg = sensor.toString();
		}
			
		messageHandler.obtainMessage((port+1)*100,msg).sendToTarget();	
	}
	
	public NXTCommunicator(Handler handler, MainActivity mainActivity){
		this.messageHandler = handler;
		this.mainActivity = mainActivity;
		this.sensorManager = new SensorManager(this);
	}
	
	/**
	 * creates new Thread which try to connect to NXT if fails reconnection
	 * will be started automatically
	 * @param remoteDevice - BT device NXT which you want to connect 
	 */
	public synchronized void connectToNXT(BluetoothDevice remoteDevice){
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
        	sensorManager.unregisterSensors();
        	sensorManager.setRunning(false);
        }catch(Exception e){
        	Log.e(MainActivity.TAG,"stop sensor man",e);
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
	
	public void setUpSensors(){
		TouchSensor ts = new TouchSensor(this, InputPort.PORT1);	
		SoundSensor ss = new SoundSensor(this, InputPort.PORT2);
		ss.setDBMode();
		LightSensor ls = new LightSensor(this, InputPort.PORT4);
		ls.setLightReflectionMode();
		sensorManager.addSensor(ts);
		sensorManager.addSensor(ss);
		sensorManager.addSensor(ls);
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
		setUpSensors();
		try{
			if(!sensorManager.isRunning())
				sensorManager.start();
			sensorManager.setRunning(true);
            
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
    public void move3Motor( byte thirdMotorSpeed) {
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
                    //extractCommandFromBuffer(buffer);
                    handleReturnPackages(buffer);
                    
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



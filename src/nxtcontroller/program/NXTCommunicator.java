package nxtcontroller.program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.ConnectionStatus;
import nxtcontroller.enums.TypeOfMessage;
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
 * BlueTooth Messages for NXT protocol: 
 * 0.byte - command length LSB, !the length of the packages is counted without two length bytes
 * 1.byte - command length MSB
 * 2.byte - command type, 
 * 3.byte-(command.length-1) - command 
  * @author Lukas Dilik
 *
 */
public class NXTCommunicator {
	
	/* declaration constant values */
	public static final String MyUUID = "00001101-0000-1000-8000-00805F9B34FB";
	
	/* private class properties declaration */
	private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
	private int state;
    private Handler messageHandler;
    private BluetoothDevice NXTdevice = null;
    //index of motors HINT:A:0,B:1,C:2
    private byte leftMotor = 0;
    private byte rightMotor = 1;
    private byte thirdMotor = 2;
    
    /* public class properties declaration */
    
	/* Getters and Setter declaration */
    
	public synchronized void setState(int state) {
		this.state = state;
		messageHandler.obtainMessage(TypeOfMessage.CONNECTION_STATUS, state).sendToTarget();
	}
	
	public synchronized byte getLeftMotor() {
		return leftMotor;
	}

	public void setLeftMotor(byte leftMotor) {
		if(leftMotor > 2 || leftMotor < 0)
			return;
		this.leftMotor = leftMotor;
	}

	public synchronized byte getRightMotor() {
		return rightMotor;
	}

	public void setRightMotor(byte rightMotor) {
		if(rightMotor > 2 || rightMotor < 0)
			return;
		this.rightMotor = rightMotor;
	}

	public synchronized byte getThirdMotor() {
		return thirdMotor;
	}

	public void setThirdMotor(byte thirdMotor) {
		if(thirdMotor > 2 || thirdMotor < 0)
			return;
		this.thirdMotor = thirdMotor;
	}

	public synchronized int getState(){
		return  this.state;
	}
	
	/* Methods and Constructors declaration */
	
	public String bytesToString(byte[] bytes){
		String temp="[";
		for(Byte b:bytes){
			temp+=Byte.toString(b);
			temp+=", ";
		}
		return temp+"]";
	}
	
	public NXTCommunicator(Handler handler){
		this.messageHandler = handler;
		setState(ConnectionStatus.DISCONNECTED);
		disconnectFromNXT();
	}
	
	/**
	 * creates new Thread which try to connect to NXT if fails reconnection
	 * will be started automatically
	 * @param remoteDevice - BT device NXT which you want to connect 
	 */
	public synchronized void connectToNXT(BluetoothDevice remoteDevice){
		if(this.getState() != ConnectionStatus.DISCONNECTED)
			return;
		this.NXTdevice = remoteDevice;
		mConnectThread = new ConnectThread(this.NXTdevice);
		mConnectThread.start();
		setState(ConnectionStatus.CONNECTING);
	}
	
	public synchronized void disconnectFromNXT(){
		stopMove();
		setState(ConnectionStatus.DISCONNECTED);
        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }
        if (mConnectedThread != null) {
        	mConnectedThread.cancel();
        	mConnectedThread = null;
        }
       
	}
	
	/**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connectToSocket(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }
        if (mConnectedThread != null) {
        	mConnectedThread.cancel();
        	mConnectedThread = null;
        }

        Log.d(MainActivity.TAG,"starting connecting to device");
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        
		setState(ConnectionStatus.CONNECTED);
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
     * send a command array of bytes via BT to NXT to move 3 motors
     * @param leftMotorSpeed - speed of  motor range:[-100-100]
     * @param rightMotorSpeed - speed of motor range:[-100-100]
     * @param thirdMotorSpeed - speed of motor range:[-100-100]
     */
    public void move3Motor( byte thirdMotorSpeed) {
    	byte[] command = generateMoveMotorCommand((byte)getThirdMotor(), (byte)thirdMotorSpeed); //command for 3.motor
    	
        if(mConnectedThread != null)
        	write(command);
        else
        	Log.d(MainActivity.TAG,"mConnectedThread is NULL");
    }
    
    /**
     * generate a byte array command for NXT
     * @param indexOfMotor - index of motor1 HINT:A:0,B:1,C:2
     * @param motorSpeed - speed of second motor range:[-100-100]
     * @return generated array of bytes, see protocol
     */
    public byte[] generateMoveMotorCommand(byte indexOfMotor, byte motorSpeed) {
        /*
		 * first see Communication protocol above
    	 * Command name:  SETOUTPUTSTATE (in data[2..13])
    	 * Byte 0: 0x80 means direct command telegram, no response required
    	 * Byte 1: 0x04
    	 * Byte 2: Output port (range 0-2;0xFF is special value meaning 'all' for simple control purposes)
    	 * Byte 3: Power set point alias SPEED (range:-100 to 100) negative sing means counter-clockwise
    	 * Byte 4: Mode byte (bit-field)
    	 * Byte 5: Regulation mode (UBYTE;enumerated)
    	 * Byte 6: Turn ratio (SBYTE;-100 to 100)
    	 * Byte 7: RunState (UBYTE;enumerated)
    	 * Byte 8-12: TachoLmit (ULONG;0:run forever) in ms (how long may be turned on motors)
    	*/
    	byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x00, 0x00, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };
    	
        data[4] = indexOfMotor; // motors: A:0,B:1,C:2
        data[5] = motorSpeed; //speed [-100-100]
   
        return data;
    }
    
    public void stopMove() {
    	this.move2Motors((byte)0, (byte)0);
    }
    
    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (getState() != ConnectionStatus.CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private synchronized void connectionLost() {
        // Start the service over to restart listening mode
    	if(this.NXTdevice != null){
    		if(getState() != ConnectionStatus.DISCONNECTED)
    			NXTCommunicator.this.connectToNXT(this.NXTdevice);
    	}
    }
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
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
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    Log.d(MainActivity.TAG, "listening: "+"bytes: "+bytes+" buffer: "+bytesToString(buffer));
                } catch (Exception e) {
                    Log.d(MainActivity.TAG, "disconnected");
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                Log.d(MainActivity.TAG, "sending to device: "+ bytesToString(buffer));
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "Exception during write", e);
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



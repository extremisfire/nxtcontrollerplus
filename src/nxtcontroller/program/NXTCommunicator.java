package nxtcontroller.program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import nxtcontroller.activity.MainActivity;
import nxtcontroller.enums.ConnectionStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class NXTCommunicator {
	
	/* declaration constant values */
	public static final String MyUUID = "00001101-0000-1000-8000-00805F9B34FB";
	
	/* private class properties declaration */
	private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private ConnectionStatus state;
    private Handler messageHandler;
    
    /* public class properties declaration */
	
    
	/* Getters and Setter declaration */
    public ConnectionStatus getState() {
		return state;
	}


	public void setState(ConnectionStatus state) {
		this.state = state;
	}
	
	
	/* Methods and Constructors declaration */
	public NXTCommunicator(Handler handler){
		messageHandler = handler;
	}
	
	



	/**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

    	
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}


        Log.d(MainActivity.TAG,"starting connecting to device");
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

    }

    
    public void move() {
        byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };
        
      
        int motor = 0; 
        byte speed = 0x20;
        if (motor == 0) {
            data[4] = 0x02;
        } else {
            data[4] = 0x01;
        }
        data[5] = speed;
        if(mConnectedThread != null)
        	mConnectedThread.write(data);
        else
        	Log.d(MainActivity.TAG,"mConnectedThread is NULL");
    }
    
    public void stopMove() {
        byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };
        
      
        int motor = 0; 
        byte speed = 0x00;
        if (motor == 0) {
            data[4] = 0x02;
        } else {
            data[4] = 0x01;
        }
        data[5] = speed;
        if(mConnectedThread != null)
        	mConnectedThread.write(data);
        else
        	Log.d(MainActivity.TAG,"mConnectedThread is NULL");
    }
    
    
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Start the service over to restart listening mode
        //NXTCommunicator.this.connectToDevice();
    }
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        connectionLost();
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
            //mBluetoothAdapter.cancelDiscovery();
            
            try {
                mmSocket.connect();
                Log.d(MainActivity.TAG, "connection success");
  
            } catch (IOException e) {
                //connectionFailed();
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
            
            //connected(mmSocket, mmDevice);
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
                    Log.d(MainActivity.TAG, "listening.."+bytes);
                } catch (Exception e) {
                    Log.d(MainActivity.TAG, "disconnected");
                    //connectionLost();
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
                Log.d(MainActivity.TAG, "sending to device:"+buffer.toString());
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



package nxtcontroller.program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import nxtcontroller.program.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MenuActivity extends Activity {
    
	BluetoothAdapter mBluetoothAdapter;
	ArrayAdapter<String> bondedDevices;
	ArrayAdapter<String> foundedDevices; 
    public static final String TAG = "test";

    private static final int REQUEST_ENABLE_BT = 2;
    public static final String MyUUID = "00001101-0000-1000-8000-00805F9B34FB";
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    TextView text; 
    Button turnOnButton, showPairedDevicesButton, scanForDevicesButton,connectButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        text = (TextView) findViewById(R.id.textView1);
        turnOnButton = (Button) findViewById(R.id.button1);
        showPairedDevicesButton = (Button) findViewById(R.id.button2);
        scanForDevicesButton = (Button) findViewById(R.id.button3);
        connectButton = (Button) findViewById(R.id.button4);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
        	text.setText("BlueTooth adapter found");
        }
        
        turnOnButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
            	turnOnBluetooth();
            }
        });
        
        showPairedDevicesButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
            	showPairedDevices();
            }
        });
        
        scanForDevicesButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
            	scanForDevices();
            }
        });
        
        connectButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
            	connectToDevice();
            }
        });
        
        bondedDevices =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        foundedDevices =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
    }
    
    public void turnOnBluetooth(){
    	if (!mBluetoothAdapter.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    }
    
    public void showPairedDevices(){
    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    	// If there are paired devices
    	if (pairedDevices.size() > 0) {
    	    // Loop through paired devices
    	    for (BluetoothDevice device : pairedDevices) {
    	        // Add the name and address to an array adapter to show in a ListView
    	    	bondedDevices.add(device.getName() + "\n" + device.getAddress());
    	    	Log.d("debug",device.getName() + "\n" + device.getAddress() );
    	    }
    	}
    	Spinner s1 = (Spinner) findViewById(R.id.spinner1);
    	bondedDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	s1.setAdapter(bondedDevices);
    	s1.showContextMenu();
    }
    
 // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                foundedDevices.add(device.getName() + "\n" + device.getAddress());
                refreshNewDevices();
            }
            
        }
    };

    public void refreshNewDevices(){
    	Spinner s2 = (Spinner) findViewById(R.id.spinner2);
    	foundedDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	s2.setAdapter(foundedDevices);
    	s2.showContextMenu();
    }
    
    public void scanForDevices(){
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        mBluetoothAdapter.startDiscovery();
    }
    
    public synchronized void connectToDevice(){
    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    	BluetoothDevice stvorka = null;
    	if (pairedDevices.size() > 0) {
    	    // Loop through paired devices
    	    for (BluetoothDevice device : pairedDevices) {
    	    	if(device.getName().equals("stvorka"))
    	    		stvorka = mBluetoothAdapter.getRemoteDevice(device.getAddress());
    	    }
    	}
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(stvorka);
        mConnectThread.start();
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == REQUEST_ENABLE_BT){
    		if(resultCode == Activity.RESULT_OK){
    			text.setText("Bluetooth enabled");
    		}else if (resultCode == Activity.RESULT_CANCELED){
    			text.setText("Bluetooth not enabled");
    		}
    	}
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


        Log.d(TAG,"starting connecting to device");
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

    }

    
    public void move() {
        byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };
        
      
        int motor = 0; 
        if (motor == 0) {
            data[4] = 0x02;
        } else {
            data[4] = 0x01;
        }
        data[5] = 0x20;
        if(mConnectedThread != null)
        	mConnectedThread.write(data);
        else
        	Log.d(TAG,"mConnectedThread is NULL");
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
            	Log.d(TAG,"socket creation failed" ,e);
            }
            mmSocket = tmp;
        }
        
        @Override
        public void run() {
            setName("ConnectThread");
            Log.d(TAG,"running connected thread");
            mBluetoothAdapter.cancelDiscovery();
            
            try {
                mmSocket.connect();
                Log.d(TAG, "connection success");
            } catch (IOException e) {
                connectionFailed();
            	Log.d(TAG,"connection failed" ,e);
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    Log.e(TAG,"connection failed" ,e);
                }
                return;
            }
            
            synchronized (MenuActivity.this) {
                mConnectThread = null;
            }
            
            connected(mmSocket, mmDevice);
        }
        
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
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
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            Log.d(TAG, "run listening");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    Log.d(TAG, "listening.."+bytes);
                } catch (Exception e) {
                    Log.d(TAG, "disconnected");
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
                Log.d(TAG, "sending to device"+buffer.toString());
            } catch (IOException e) {
                Log.d(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "close() of connect socket failed", e);
            }
        }
    }
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Start the service over to restart listening mode
        MenuActivity.this.connectToDevice();
    }
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        connectionLost();
    }
    
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if(mReceiver != null)
			unregisterReceiver(mReceiver); // Don't forget to unregister during onDestroy
		super.onDestroy();
	}
}
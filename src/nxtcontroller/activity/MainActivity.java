package nxtcontroller.activity;

import nxtcontroller.enums.ConnectionStatus;
import nxtcontroller.enums.ErrorCodes;
import nxtcontroller.enums.InfoCodes;
import nxtcontroller.enums.TypeOfMessage;
import nxtcontroller.program.NXTCommunicator;
import nxtcontroller.program.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	/* declaration constant values */
	public static final String TAG = "nxtcontroller"; //debug tag for LogCat
    public static final int REQUEST_CONNECT_DEVICE = 2;
    public static final int REQUEST_ENABLE_BT = 1;
	
	/* private class properties declaration */
    private BluetoothAdapter bluetoothAdapter;
    private TextView statusLabel,deviceNameLabel; 
    private Button connectButton,disconnectButton;
    private int connectionStatus;
    private NXTCommunicator nxtCommunicator;
    private String nxtDeviceName,nxtDeviceAddress = null;
    private String[] errors,infos,connectionStatuses; //message arrays
    
    
    // The Handler that gets information back from NXTCommunicator
    private final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	try{
        		switch (msg.what) {
            	case(TypeOfMessage.TOAST_ERROR):
            		Toast.makeText(getApplicationContext(), (String) msg.obj,2).show();
            	break;
            	case(TypeOfMessage.TOAST_INFO):
            		Toast.makeText(getApplicationContext(), (String) msg.obj,Toast.LENGTH_LONG).show();
            	break;
            	case(TypeOfMessage.STATUS_LABEL):
            		statusLabel.setText((String)msg.obj);
            	break;
            	case(TypeOfMessage.CONNECTION_STATUS):
            		setConnectionStatus((Integer) msg.obj);
            	break;
            }
        	}catch (Exception e){
        		Log.e(TAG,"meesage handling error",e);
        	}

        }
    };
    
    /* Getters and Setter declaration */
    public void setConnectionStatus(int connectionStatus) {
    	switch(connectionStatus){
    		case ConnectionStatus.CONNECTED:
    			statusLabel.setTextColor(Color.GREEN); 
    			nxtCommunicator.move2Motors((byte)0, (byte)30, (byte)1,(byte)-30);
    		break;
    		case ConnectionStatus.DISCONNECTED:
    			statusLabel.setTextColor(Color.BLACK);
    			connectButton.setText(getResources().getString(R.string.connectNXT));
    		break;
    		case ConnectionStatus.CONNECTING:
    			statusLabel.setTextColor(Color.MAGENTA);
    		break;
    		case ConnectionStatus.CONNECTION_FAILED:
    			statusLabel.setTextColor(Color.RED);
    			disconnectNXT();
    			Toast.makeText(getApplicationContext(), infos[InfoCodes.CONNECTION_FAILED_HINT],Toast.LENGTH_LONG).show();
    		break;
    	}
    	statusLabel.setText(connectionStatuses[connectionStatus]);
		this.connectionStatus = connectionStatus;
	}
    
    /* Methods and Constructors declaration */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        
    	//getting needed resources
    	Resources res = getResources();
    	errors = res.getStringArray(R.array.errorsMsg);
    	infos = res.getStringArray(R.array.infoMsg);
    	connectionStatuses = res.getStringArray(R.array.connectionStatues);
    	
    	bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(bluetoothAdapter == null){
    		String msg = errors[ErrorCodes.BLUETOOTH_NOT_FOUND];
    		Toast.makeText(getApplicationContext(),msg,2).show();
    		finish();
    		return;
    	}
    	if(!bluetoothAdapter.isEnabled())
    		turnOnBluetooth();
    	setupComponents();
    }
    

	public void setupComponents(){
		nxtCommunicator = new NXTCommunicator(messageHandler);
        statusLabel = (TextView) findViewById(R.id.statusLabel);
        deviceNameLabel = (TextView) findViewById(R.id.deviceName);
        connectButton = (Button) findViewById(R.id.connectButton);
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setVisibility(View.GONE); 
        setUpListeners();
    }
    
    public void setUpListeners(){
    	try{
            connectButton.setOnClickListener(new View.OnClickListener()  {
                public void onClick(View v) {
	                if(connectionStatus == ConnectionStatus.READY_TO_CONNECT){
	                	connectNXT();
	                }else{
	                	startChooseDeviceActivity();
	                }	
                }
            });
            disconnectButton.setOnClickListener(new View.OnClickListener()  {
                public void onClick(View v) {
                	disconnectNXT();
                }
            });
    	}catch(Exception e){
    		Log.e(MainActivity.TAG," setUpListeners error",e);
    	}
    }
    
    public void connectNXT(){
    	if(nxtDeviceAddress == null|| nxtDeviceName == null)
    	   return;
    	synchronized (nxtCommunicator) {
            if(connectionStatus == ConnectionStatus.READY_TO_CONNECT){
            	BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(nxtDeviceAddress);
            	nxtCommunicator.connectToNXT(remoteDevice);
            }
		}
    	connectButton.setVisibility(View.GONE);
    	disconnectButton.setVisibility(View.VISIBLE);
    }
    
    public void disconnectNXT(){
    	synchronized (nxtCommunicator) {
    		if(nxtCommunicator != null){
    			nxtCommunicator.disconnectFromNXT();	
    			
    		}
		}
    	connectButton.setVisibility(View.VISIBLE);
    	disconnectButton.setVisibility(View.GONE);
     }
    
    public void startChooseDeviceActivity(){
    	if(!bluetoothAdapter.isEnabled()){
    		turnOnBluetooth();
    		setConnectionStatus(ConnectionStatus.DISCONNECTED);
    		return;
    	}
        Intent intent = new Intent(this, BluetoothDeviceManagerActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }
    
	public void turnOnBluetooth(){
    	if (! bluetoothAdapter.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
    	case REQUEST_CONNECT_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
            	String deviceName = data.getExtras().getString(BluetoothDeviceManagerActivity.NAME_OF_DEVICE);
                String deviceAddress = data.getExtras().getString(BluetoothDeviceManagerActivity.ADDRESS_OF_DEVICE);
                deviceNameLabel.setText(deviceName+"@"+deviceAddress);
            	this.nxtDeviceAddress = deviceAddress;
            	this.nxtDeviceName = deviceName;
            	setConnectionStatus(ConnectionStatus.READY_TO_CONNECT);
            	connectNXT();
            }
            break;
    	case REQUEST_ENABLE_BT:
    		if(resultCode == Activity.RESULT_OK){
    			String msg = infos[InfoCodes.BLUETOOTH_ACTIVATED];
    			Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    		}else if (resultCode == Activity.RESULT_CANCELED ){
    			String msg = errors[ErrorCodes.BLUETOOTH_NOT_ACTIVATED];
    			Toast.makeText(getApplicationContext(),msg,2).show();
    		}
        break;
        }
    }
    
	@Override
	protected void onStart() {
		setConnectionStatus(ConnectionStatus.DISCONNECTED);
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
		super.onDestroy();
	}
}
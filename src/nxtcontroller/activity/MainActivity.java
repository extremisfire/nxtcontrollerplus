package nxtcontroller.activity;

import nxtcontroller.enums.ConnectionStatus;
import nxtcontroller.enums.ControlModes;
import nxtcontroller.enums.ErrorCodes;
import nxtcontroller.enums.InfoCodes;
import nxtcontroller.enums.Keys;
import nxtcontroller.enums.TypeOfMessage;
import nxtcontroller.program.ControlPad;
import nxtcontroller.program.NXTCommunicator;
import nxtcontroller.program.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{

	/* declaration constant values */
	public static final String TAG = "nxtcontroller"; //debug tag for LogCat
	public static final String PREFERENCES_FNAME = "Preferences"; //name of shared preferences file
    public static final int REQUEST_CONNECT_DEVICE = 2;
    public static final int REQUEST_ENABLE_BT = 1;
	
	/* private class properties declaration */
    private BluetoothAdapter bluetoothAdapter;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    
    private TextView statusLabel,deviceNameLabel,controlModeLabel; 
    private Button connectButton,disconnectButton;
    private String[] errors,infos,connectionStatuses,controlModes; //message arrays
    
    private int connectionStatus;
    private int controlMode;
    private NXTCommunicator nxtCommunicator;
    private ControlPad controlPad;
    private String nxtDeviceName,nxtDeviceAddress;
  
    
    /* The Handler that gets information back from NXTCommunicator */
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
    			wakeLock.acquire();
    		break;
    		case ConnectionStatus.DISCONNECTED:
    			statusLabel.setTextColor(Color.BLACK);
       			if(wakeLock.isHeld())
    				wakeLock.release();
    		break;
    		case ConnectionStatus.CONNECTING:
    			statusLabel.setTextColor(Color.MAGENTA);
    		break;
    		case ConnectionStatus.CONNECTION_FAILED:
    			statusLabel.setTextColor(Color.RED);
    			disconnectNXT();
    			Toast.makeText(getApplicationContext(), infos[InfoCodes.CONNECTION_FAILED_HINT],Toast.LENGTH_LONG).show();
    		break;
    		case ConnectionStatus.READY_TO_CONNECT:
    			statusLabel.setTextColor(Color.YELLOW);
    		break;
    	}
    	statusLabel.setText(connectionStatuses[connectionStatus]);
		this.connectionStatus = connectionStatus;
	}
    
	/* Methods and Constructors declaration */
    
    private void loadDefaults(){
    	setConnectionStatus(ConnectionStatus.DISCONNECTED);
    	controlMode = ControlModes.TOUCHPAD_MODE;
    	nxtDeviceAddress = null;
    	nxtDeviceName = null;
    	deviceNameLabel.setText(getResources().getString(R.string.selectDevice));
       	connectButton.setVisibility(View.VISIBLE);
    	disconnectButton.setVisibility(View.GONE);
    	refreshUI();
    }

    private void refreshUI(){
    	if(nxtDeviceName==null || nxtDeviceAddress == null){
    		deviceNameLabel.setText(getResources().getString(R.string.selectDevice));
    	}else{
    		deviceNameLabel.setText(nxtDeviceName+"@"+nxtDeviceAddress);
    	}
    	controlModeLabel.setText(controlModes[controlMode]);
    }
    
    private void gettingResources(){
    	Resources res = getResources();
    	errors = res.getStringArray(R.array.errorsMsg);
    	infos = res.getStringArray(R.array.infoMsg);
    	connectionStatuses = res.getStringArray(R.array.connectionStatues);
    	controlModes = res.getStringArray(R.array.controlModes);
    }

	private void setUpComponents(){
		try{
			nxtCommunicator = new NXTCommunicator(messageHandler);
	        statusLabel = (TextView) findViewById(R.id.statusLabel);
	        deviceNameLabel = (TextView) findViewById(R.id.deviceName);
	        controlModeLabel = (TextView) findViewById(R.id.controlModeLabel);
	        connectButton = (Button) findViewById(R.id.connectButton);
	        disconnectButton = (Button) findViewById(R.id.disconnectButton);
	        disconnectButton.setVisibility(View.GONE); 
	        controlPad = (ControlPad) findViewById(R.id.controlPadView);
        }catch(Exception e){
        	Log.e(TAG,"seeting up components",e);
        }
        setUpListeners();
    }
    
    private void setUpListeners(){
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
    
    private void connectNXT(){
    	try{
	    	synchronized (nxtCommunicator) {
		        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(nxtDeviceAddress);
		        nxtCommunicator.connectToNXT(remoteDevice);
		    	registerController();
		    	connectButton.setVisibility(View.GONE);
		    	disconnectButton.setVisibility(View.VISIBLE);
	    	}
    	}catch(Exception e){
    		setConnectionStatus(ConnectionStatus.CONNECTION_FAILED);
    		disconnectNXT();
    		Log.e(TAG,"connectNXT error",e);
    	}
    }
    
    private void disconnectNXT(){
    	try{
	    	unregisterController();
	    	synchronized (nxtCommunicator) {
	    		if(nxtCommunicator != null){
	    			nxtCommunicator.disconnectFromNXT();	
	    			
	    		}
			}
	    	connectButton.setVisibility(View.VISIBLE);
	    	disconnectButton.setVisibility(View.GONE);
    	}catch(Exception e){
    		Log.e(TAG,"disconnectNXT error",e);
    	}
     }
    
    private void startChooseDeviceActivity(){
    	if(!bluetoothAdapter.isEnabled()){
    		turnOnBluetooth();
    		setConnectionStatus(ConnectionStatus.DISCONNECTED);
    		return;
    	}
        Intent intent = new Intent(this, BluetoothDeviceManagerActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }
    
	private void turnOnBluetooth(){
    	if (! bluetoothAdapter.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    }
    
    private void registerController(){
    	try{
    		//controlPad.turnOnTouchControl();
    		controlPad.setNxtCommnunicator(nxtCommunicator);  
    		controlPad.turnOnTiltControl();
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	}catch(Exception e){
    		Log.e(TAG,"control pad setting up",e);
    	}
    }
    
    private void unregisterController(){
    	try{
    		controlPad.turnOffTouchControl(); 		
    	}catch(Exception e){
    		Log.e(TAG,"control pad unregistering",e);
    	}
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
	    	case REQUEST_CONNECT_DEVICE:
	            if (resultCode == Activity.RESULT_OK) {
	            	String deviceName = data.getExtras().getString(Keys.DEVICE_NAME);
	                String deviceAddress = data.getExtras().getString(Keys.DEVICE_ADDRESS);
	            	this.nxtDeviceAddress = deviceAddress;
	            	this.nxtDeviceName = deviceName;
	            	refreshUI();
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
    
    private void loadPreviousState(Bundle savedInstanceState){
    	this.connectionStatus = savedInstanceState.getInt(Keys.CONNECTION_STATUS);
    	this.controlMode = savedInstanceState.getInt(Keys.CONTROL_MODE);
    	this.nxtDeviceName = savedInstanceState.getString(Keys.DEVICE_NAME);
    	this.nxtDeviceAddress = savedInstanceState.getString(Keys.DEVICE_ADDRESS);
    	refreshUI();
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
		if(wakeLock.isHeld())
			wakeLock.release();
	}

	@Override
	protected void onDestroy() {
		disconnectNXT();
		super.onDestroy();
	}
	
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(Keys.CONNECTION_STATUS, this.connectionStatus);
        savedInstanceState.putInt(Keys.CONTROL_MODE, this.controlMode);
        savedInstanceState.putString(Keys.DEVICE_NAME, this.nxtDeviceName);
        savedInstanceState.putString(Keys.DEVICE_ADDRESS, this.nxtDeviceAddress);
        Log.d(TAG,"saving state: "+savedInstanceState);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(bluetoothAdapter == null){
    		String msg = errors[ErrorCodes.BLUETOOTH_NOT_FOUND];
    		Toast.makeText(getApplicationContext(),msg,2).show();
    		finish();
    		return;
    	}
    	
    	powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
    	
        setContentView(R.layout.main_screen);
     
        gettingResources();
        
    	if(!bluetoothAdapter.isEnabled())
    		turnOnBluetooth();
    	
    	setUpComponents();
    	
    	Log.d(TAG,"loading prev: "+savedInstanceState);
    	
    	if(savedInstanceState == null){
    		loadDefaults();
    	}else{
    		loadPreviousState(savedInstanceState);
    	}
    	
    	Log.d(TAG,"cstatus: "+this.connectionStatus);
    	if(this.connectionStatus == ConnectionStatus.CONNECTED){
    		connectNXT();
    	}else{
    		setConnectionStatus(connectionStatus);
    	}
    	
    	
    }
}
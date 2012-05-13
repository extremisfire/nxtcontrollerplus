package nxtcontroller.activity;

import nxtcontroller.enums.ConnectionStatus;
import nxtcontroller.enums.ControlModes;
import nxtcontroller.enums.ErrorCodes;
import nxtcontroller.enums.InfoCodes;
import nxtcontroller.enums.Keys;
import nxtcontroller.enums.TypeOfMessage;
import nxtcontroller.enums.nxtbuiltin.InputPort;
import nxtcontroller.enums.nxtbuiltin.SensorID;
import nxtcontroller.program.NXTCommunicator;
import nxtcontroller.program.NXTDevice;
import nxtcontroller.program.R;
import nxtcontroller.program.sensors.LightSensor;
import nxtcontroller.program.sensors.SoundSensor;
import nxtcontroller.program.views.CompassSensorView;
import nxtcontroller.program.views.ControlPad;
import nxtcontroller.program.views.LightSensorView;
import nxtcontroller.program.views.SensorView;
import nxtcontroller.program.views.SoundSensorView;
import nxtcontroller.program.views.TouchSensorView;
import nxtcontroller.program.views.UltrasonicSensorView;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnClickListener, SeekBar.OnSeekBarChangeListener,android.view.View.OnClickListener{

	/* declaration constant values */
	public static final String TAG = "nxtcontroller"; //debug tag for LogCat
	public static final String PREFERENCES_FNAME = "Preferences"; //name of shared preferences file
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int REQUEST_CONNECT_DEVICE = 2;
    public static final int REQUEST_SETTINGS = 3;
    
    public static final int NUMBER_OF_PORTS = 4;
	
	/* private class properties declaration */
    private BluetoothAdapter bluetoothAdapter;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    
    private TextView statusLabel,deviceNameLabel,controlModeLabel,batteryLevelLabel; 
    
    private TextView[] sensorNames;
    private LinearLayout[] sensorViewWrappers; // position is the number of Port
    private SensorView[] sensorViews;
    
    private Button connectButton,disconnectButton;
    private SeekBar thirdMotorController;
    private String[] errors,infos,connectionStatuses,controlModes,sensorTextName; //message arrays
    
    private int connectionStatus;
    private int controlMode;
    private NXTCommunicator nxtCommunicator = NXTCommunicator.getInstance();
    private ControlPad controlPad;
    private NXTDevice nxtDevice = null;
    private CompassSensorView compass = null;
    private TextView azimuthLabel = null;
    
    private UltrasonicSensorView radar = null;
    private TextView distanceLabel = null;
    
    /* layout flipper resources */
    public ViewFlipper flipper = null;
    private LinearLayout firstLayout = null, secondLayout = null; 
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener; 
    private static final int SWIPE_MIN_DISTANCE = 40;
    private static final int SWIPE_MAX_OFF_PATH = 300;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    
    /* Gesture Detect used for swtich layout onFling event */
    class MyGestureDetector extends SimpleOnGestureListener {
      
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                	return false;
                
                final float distance = e1.getY() - e2.getY();
                final boolean enoughSpeed = Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY;
                if(distance > SWIPE_MIN_DISTANCE && enoughSpeed) {
                    // down to up
                	MainActivity.this.flipper.showNext();
                    return true;
                }  else if (distance < -SWIPE_MIN_DISTANCE && enoughSpeed) {
                	MainActivity.this.flipper.showPrevious();
                    return true;
                } 
                
            } catch (Exception e) {}
            return false;
        }
    };
    
    /* The Handler that gets information back from NXTCommunicator */
    private final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	try{
        		String temp= "";
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
            	case(TypeOfMessage.BATTERY_LEVEL):
            		temp = getResources().getString(R.string.batteryLevel);
            		temp += " " + msg.obj.toString() + " %";
            		batteryLevelLabel.setText(temp);
            	break;
            	case(TypeOfMessage.SENSOR_DATA):
            		refreshSensorData(msg);
            	break;
            	case(TypeOfMessage.COMPASS_SENSOR_DATA):
            		compass.setSensorValue(msg.arg1);
            		compass.invalidate();
            	break;
               	case(TypeOfMessage.ULTRASONIC_SENSOR_DATA):
            		radar.setSensorValue(msg.arg1);
               		radar.invalidate();
            	break;
            }
        	}catch (Exception e){
        		Log.e(TAG,"meesage handling error",e);
        	}
        }
    };
    
    /**
     * @param msg.arg1 = sensor ID
     * msg.arg2 - port number
     * msg.obj - sensor data
     */
    private void refreshSensorData(Message msg){
    	int portNumber = msg.arg1;
    	int sensorData = msg.arg2;
    	SensorView sensorView = sensorViews[portNumber];
    	if(sensorView != null){
    		sensorView.setSensorValue(sensorData);
    		sensorView.invalidate();
    	}   	
    }
    
    public void setUpSensorView(byte portNumber, int sensorID){
    	LinearLayout viewWrapper = sensorViewWrappers[portNumber];
    	viewWrapper.removeAllViews();
    	switch (sensorID) {
		case SensorID.TOUCH_SENSOR:
			TouchSensorView touchView = new TouchSensorView(this);
			sensorViews[portNumber] =  touchView;
			viewWrapper.addView(touchView);
			break;
		case SensorID.SOUND_SENSOR:
			SoundSensor ss = (SoundSensor)nxtCommunicator.getSensorManager().getDigitalSensor(portNumber);
			SoundSensorView soundView = new SoundSensorView(this,ss);
			sensorViews[portNumber] =  soundView;
			viewWrapper.addView(soundView);
			break;
		case SensorID.LIGHT_SENSOR:
			LightSensor ls = (LightSensor)nxtCommunicator.getSensorManager().getDigitalSensor(portNumber);
			LightSensorView lightView = new LightSensorView(this,ls);
			sensorViews[portNumber] =  lightView;
			viewWrapper.addView(lightView);
			break;
    	}
    	
    	TextView name = sensorNames[portNumber];
    	String temp = "Port "+Byte.toString((byte) (portNumber+1))+":\n";
    	temp += sensorTextName[sensorID];
    	name.setText(temp);
    }
    
    /* Getters and Setter declaration */
    
    public  void setConnectionStatus(int connectionStatus) {
    	switch(connectionStatus){
    		case ConnectionStatus.CONNECTED:
    			statusLabel.setTextColor(Color.GREEN);
		    	registerController();
    			wakeLock.acquire();
    		break;
    		case ConnectionStatus.DISCONNECTED:
    			statusLabel.setTextColor(Color.BLACK);
    			batteryLevelLabel.setText(getResources().getString(R.string.batteryLevel));
    			if(wakeLock.isHeld())
    				wakeLock.release();
    		break;
    		case ConnectionStatus.CONNECTING:
    			statusLabel.setTextColor(Color.YELLOW);
    		break;
    		case ConnectionStatus.CONNECTION_FAILED:
    			statusLabel.setTextColor(Color.RED);
    			disconnectNXT();
    			Toast.makeText(getApplicationContext(), infos[InfoCodes.CONNECTION_FAILED_HINT],Toast.LENGTH_LONG).show();
    		break;
    		case ConnectionStatus.CONNECTION_LOST:
    			statusLabel.setTextColor(Color.RED);
    			Toast.makeText(getApplicationContext(), infos[InfoCodes.CONNECTION_LOST_HINT],Toast.LENGTH_LONG).show();
    		break;
    	}
    	statusLabel.setText(connectionStatuses[connectionStatus]);
		this.connectionStatus = connectionStatus;
	}
    
    public synchronized int getConnectionStatus() {
		return connectionStatus;
	}

	public void setControlMode(int controlMode) {
    	try{
	    	switch (controlMode) {
			case ControlModes.TOUCHPAD_MODE:
				if(getConnectionStatus() == ConnectionStatus.CONNECTED){
					controlPad.turnOffTiltControl();
					controlPad.turnOnTouchControl();
				}
				break;
			case ControlModes.TILT_MODE:
				if(getConnectionStatus() == ConnectionStatus.CONNECTED){
					controlPad.turnOffTouchControl();
					controlPad.turnOnTiltControl();
				}
				break;
			}
	    	this.controlMode = controlMode;
	    	controlModeLabel.setText(controlModes[this.controlMode]);
    	}catch(Exception e){
    		Log.e(TAG,"set control error",e);
    	}
	}
    
	public NXTDevice getNxtDevice() {
		return nxtDevice;
	}

	public void setNxtDevice(NXTDevice nxtDevice) {
		this.nxtDevice = nxtDevice;
		deviceNameLabel.setText(nxtDevice.toString());
	}
    
	/* Methods and Constructors declaration */
	private void loadDefaults(){
    	setConnectionStatus(ConnectionStatus.DISCONNECTED);
    	setControlMode(ControlModes.TOUCHPAD_MODE);
    	nxtDevice = null;
    	deviceNameLabel.setText(getResources().getString(R.string.selectDevice));
       	connectButton.setVisibility(View.VISIBLE);
    	disconnectButton.setVisibility(View.GONE);
    }
    
    private void gettingResources(){
    	try{
    		Resources res = getResources();
    		errors = res.getStringArray(R.array.errorsMsg);
    		infos = res.getStringArray(R.array.infoMsg);
    		connectionStatuses = res.getStringArray(R.array.connectionStatues);
    		controlModes = res.getStringArray(R.array.controlModes);
    		sensorTextName = res.getStringArray(R.array.sensorNames);
    	}catch(Exception e){
    		Log.e(TAG,"resources error: ",e);
    	}
    }

	private void setUpComponents(){
		try{
			nxtCommunicator.setMainActivity(this);
			nxtCommunicator.setMessageHandler(this.messageHandler);
			
			batteryLevelLabel = (TextView) findViewById(R.id.batteryLevelLable);
			
			sensorNames = new TextView[NUMBER_OF_PORTS];
			sensorViewWrappers = new LinearLayout[NUMBER_OF_PORTS];
			sensorViews = new SensorView[NUMBER_OF_PORTS];
			
			sensorNames[InputPort.PORT1]  = (TextView)findViewById(R.id.row1name);
			sensorNames[InputPort.PORT2]  = (TextView)findViewById(R.id.row2name);
			sensorNames[InputPort.PORT3]  = (TextView)findViewById(R.id.row3name);
			sensorNames[InputPort.PORT4]  = (TextView)findViewById(R.id.row4name);

			sensorViewWrappers[InputPort.PORT1] = (LinearLayout)findViewById(R.id.view1);
			sensorViewWrappers[InputPort.PORT2] = (LinearLayout)findViewById(R.id.view2);
			sensorViewWrappers[InputPort.PORT3] = (LinearLayout)findViewById(R.id.view3);
			sensorViewWrappers[InputPort.PORT4] = (LinearLayout)findViewById(R.id.view4);
			
			statusLabel = (TextView) findViewById(R.id.statusLabel);
	        deviceNameLabel = (TextView) findViewById(R.id.deviceName);
	        controlModeLabel = (TextView) findViewById(R.id.controlModeLabel);
	        
	        connectButton = (Button) findViewById(R.id.connectButton);
	        disconnectButton = (Button) findViewById(R.id.disconnectButton);
	        disconnectButton.setVisibility(View.GONE); 
	        
	        thirdMotorController = (SeekBar)findViewById(R.id.thirdMotorSeekBar);
	        thirdMotorController.setMax(200);
	        thirdMotorController.setProgress(100);
	        
	        flipper = (ViewFlipper)findViewById(R.id.flipper);
	        firstLayout = (LinearLayout)findViewById(R.id.firstScreen);
	        secondLayout = (LinearLayout)findViewById(R.id.secondScreen);
	        
	        /* setUP Compass and Radar */
	        compass = (CompassSensorView)findViewById(R.id.compassSensorView);
	        azimuthLabel = (TextView)findViewById(R.id.azimutLabel);
	        compass.setAzimuthLabel(azimuthLabel);
	        
	        distanceLabel = (TextView)findViewById(R.id.distanceLabel);
	        radar = (UltrasonicSensorView)findViewById(R.id.ultrasonicSensorView);
	        radar.setDistanceLabel(distanceLabel);
	        
	        controlPad = (ControlPad) findViewById(R.id.controlPadView);
        }catch(Exception e){
        	Log.e(TAG,"seeting up components",e);
        }
        setUpListeners();
    }
    
    private void setUpListeners(){
    	try{
    		thirdMotorController.setOnSeekBarChangeListener(this);
            connectButton.setOnClickListener(new View.OnClickListener()  {
                public void onClick(View v) {
	                startChooseDeviceActivity();
                }
            });
            disconnectButton.setOnClickListener(new View.OnClickListener()  {
                public void onClick(View v) {
                	disconnectNXT();
                }
            });
            
            firstLayout.setOnTouchListener(gestureListener);
            firstLayout.setOnClickListener(MainActivity.this); 
            secondLayout.setOnTouchListener(gestureListener);
            secondLayout.setOnClickListener(MainActivity.this); 
            
    	}catch(Exception e){
    		Log.e(MainActivity.TAG," setUpListeners error",e);
    	}
    }
    
    private void connectNXT(){
    	try{
		    BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(nxtDevice.getAddress());
		    nxtCommunicator.connectToNXT(remoteDevice);
		    setConnectionStatus(ConnectionStatus.CONNECTING);
		    connectButton.setVisibility(View.GONE);
		    disconnectButton.setVisibility(View.VISIBLE);
    	}catch(Exception e){
    		setConnectionStatus(ConnectionStatus.CONNECTION_FAILED);
    		Log.e(TAG,"connectNXT error",e);
    	}
    }
    
    private void disconnectNXT(){
    	try{
	    	if(nxtCommunicator != null){
	    		nxtCommunicator.stopMove();
	    		nxtCommunicator.disconnectFromNXT();
			}
    		Log.d(TAG,"disconnecting:"+getNxtDevice());
    		unregisterController();
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
    		controlPad.turnOnTouchControl();
    	}catch(Exception e){
    		Log.e(TAG,"control pad setting up",e);
    	}
    }
    
    private void unregisterController(){
    	try{
    		controlPad.turnOffTouchControl(); 	
    		controlPad.turnOffTiltControl();
    	}catch(Exception e){
    		Log.e(TAG,"control pad unregistering",e);
    	}
    	
    }
    
    private void showSettingsActivity(){
    	if(connectionStatus == ConnectionStatus.CONNECTED)
    		disconnectNXT();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_SETTINGS);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, controlModes[0]);
		menu.add(0, 1, 1, controlModes[1]);
		menu.add(0, 2, 2, getResources().getString(R.string.settingsText));
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			setControlMode(ControlModes.TOUCHPAD_MODE);
			break;
		case 1:
			setControlMode(ControlModes.TILT_MODE);
			break;
		case 2:
			showSettingsActivity();
			break;
		}
		
		return true;
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
	    	case REQUEST_CONNECT_DEVICE:
	            if (resultCode == Activity.RESULT_OK) {
	            	String deviceName = data.getExtras().getString(Keys.DEVICE_NAME);
	                String deviceAddress = data.getExtras().getString(Keys.DEVICE_ADDRESS);
	                setNxtDevice(new NXTDevice(deviceName,deviceAddress));
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
	    	case REQUEST_SETTINGS:
	    		if(resultCode == Activity.RESULT_OK){
	    			Toast.makeText(getApplicationContext(), infos[InfoCodes.SETTING_SAVED],Toast.LENGTH_LONG).show();
	    			nxtCommunicator.loadFromPreferences();
	    		}else{
	    			Toast.makeText(getApplicationContext(), infos[InfoCodes.SETTING_NOT_SAVED],Toast.LENGTH_LONG).show();
	    		}
	        break;
        }
    }
    
    private void loadPreviousState(Bundle savedInstanceState){
    	try{
    		this.connectionStatus = savedInstanceState.getInt(Keys.CONNECTION_STATUS);
    		setControlMode(savedInstanceState.getInt(Keys.CONTROL_MODE));
    		NXTDevice temp = new NXTDevice();
    		temp.setName(savedInstanceState.getString(Keys.DEVICE_NAME));
    		temp.setAddress(savedInstanceState.getString(Keys.DEVICE_ADDRESS));
    		setNxtDevice(temp);
    	}catch(Exception e){
    		Log.e(TAG, "load previous state", e);
    	}
    	
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
		if(wakeLock.isHeld())
			wakeLock.release();
		disconnectNXT();
		super.onStop();
	}

	
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(Keys.CONNECTION_STATUS, this.connectionStatus);
        savedInstanceState.putInt(Keys.CONTROL_MODE, this.controlMode);
        if(nxtDevice != null){
        	savedInstanceState.putString(Keys.DEVICE_NAME, nxtDevice.getName());
        	savedInstanceState.putString(Keys.DEVICE_ADDRESS, nxtDevice.getAddress());
        }
        Log.d(TAG,"saving state: "+savedInstanceState);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };       
        
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

    	if(savedInstanceState == null){
    		loadDefaults();
    	}else{
    		loadPreviousState(savedInstanceState);
        	Log.d(TAG,"loading previous state: "+savedInstanceState);
    	}
    	
    	if(this.connectionStatus == ConnectionStatus.CONNECTED){
    		connectNXT();
    	}else{
    		setConnectionStatus(connectionStatus);
    	}
    	
    }

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		final int zero = 100;
		if(this.connectionStatus == ConnectionStatus.CONNECTED){
			byte speed = (byte) (progress - zero);
			speed *= -1;
			nxtCommunicator.move3Motor(speed);
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		final int zero = 100;
		if(this.connectionStatus == ConnectionStatus.CONNECTED){
			byte speed = 0;
			nxtCommunicator.move3Motor(speed);
		}
		seekBar.setProgress(zero);
	}

	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
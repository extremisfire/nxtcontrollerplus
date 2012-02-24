package nxtcontroller.program;

import java.util.Set;
import nxtcontroller.enums.ErrorCodes;
import nxtcontroller.enums.InfoCodes;
import nxtcontroller.enums.TypeOfMessage;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;


/**
 * This class is used for enabling BlueTooh, selecting device NXT
 * which you want to connect, you can choose from 
 * paired devices or scan for new.
 * This class obtain MAC address from device you want to connect
 * a send it to NXTCommunicator.
 * @author Lukas Dilik
 * @see NXTCommunicator
 */

public class BluetoothDeviceManager extends Activity{
	
	/* declaration constant values */
	private static final int REQUEST_ENABLE_BT = 1;
	private String[] errors,infos; //message arrays
	
	/* private class properties declaration */
	private BluetoothAdapter mBluetoothAdapter;
	private Set<BluetoothDevice> bondedDevices;
	private Set<BluetoothDevice> foundedDevices; 
	private Handler messageHandler;
	private boolean bluetoothOn;
	
	/* 
	 * this receiver is used for scanning new BT devices
	 */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                addFoundedDevice(device);
            }
            
        }
    };
	
    /* Getters and Setter declaration */
    public Set<BluetoothDevice> getBondedDevices() {
		return bondedDevices;
	}

	public void setBondedDevices(Set<BluetoothDevice> bondedDevices) {
		this.bondedDevices = bondedDevices;
	}

	public Set<BluetoothDevice> getFoundedDevices() {
		return foundedDevices;
	}

	public void setFoundedDevices(Set<BluetoothDevice> foundedDevices) {
		this.foundedDevices = foundedDevices;
	}
	
	/* Methods and Constructors declaration */
    public BluetoothDeviceManager(Context context, Handler handler) {
    	//getting needed resources
    	Resources res = context.getResources();
    	errors = res.getStringArray(R.array.errorsMsg);
    	infos = res.getStringArray(R.array.infoMsg);
    	
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	bluetoothOn = mBluetoothAdapter.isEnabled();
        messageHandler = handler;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if(mBluetoothAdapter == null){
    		messageHandler.obtainMessage(TypeOfMessage.TOAST_ERROR,errors[ErrorCodes.BLUETOOTH_NOT_FOUND]).sendToTarget();
    		finish();
    	}
    	bondedDevices = mBluetoothAdapter.getBondedDevices();
        super.onCreate(savedInstanceState);
    }
	
    public void connectToNXT(){
    	
    }
    
	public void turnOnBluetooth(){
    	if (!bluetoothOn) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    }
    
    public void showPairedDevices(){
    	//TODO
    }
    
 

    
    private void addFoundedDevice(BluetoothDevice device){
        foundedDevices.add(device);
        //TODO
    }


    
    public void startScanningForDevices(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); 
        mBluetoothAdapter.startDiscovery();
    }
    
    public synchronized void connectToDevice(){
    	//TODO
    	 //mBluetoothAdapter.getRemoteDevice("00:16:53:13:BB:E2");
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == REQUEST_ENABLE_BT){
    		if(resultCode == Activity.RESULT_OK){
    			messageHandler.obtainMessage(TypeOfMessage.TOAST_INFO, infos[InfoCodes.BLUETOOTH_ACTIVATED]).sendToTarget();
    		}else if (resultCode == Activity.RESULT_CANCELED){
    			messageHandler.obtainMessage(TypeOfMessage.TOAST_ERROR, infos[ErrorCodes.BLUETOOTH_NOT_ACTIVATED]).sendToTarget();
    		}
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
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if(mReceiver != null)
			unregisterReceiver(mReceiver);
		super.onDestroy();
	}

}

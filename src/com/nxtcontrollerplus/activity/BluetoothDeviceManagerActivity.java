package com.nxtcontrollerplus.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nxtcontrollerplus.R;
import com.nxtcontrollerplus.enums.Keys;
import com.nxtcontrollerplus.program.NXTCommunicator;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.bluetooth.*;
import android.content.*;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * This class is used for enabling BlueTooh, selecting device NXT
 * which you want to connect, you can choose from 
 * paired devices or scan for new.
 * This class obtain MAC address from device you want to connect
 * a send it to NXTCommunicator.
 * @author Lukas Dilik
 * @see NXTCommunicator
 */

public class BluetoothDeviceManagerActivity extends ExpandableListActivity{
	
	/* declaration constant values */
    
	/* private class properties declaration */
	private BluetoothAdapter bluetoothAdapter;
	private List<Map<String, String>> groupData; 
	private List<List<Map<String, String>>> childData; 
	private List<Map<String, String>> foundedDevices; 
    private ExpandableListAdapter listAdapter;
	
	/** 
	 * this receiver class is used for scanning new BT devices 
	 */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //filtering only NXT like devices and not paired devices
                if ((device.getBondState() != BluetoothDevice.BOND_BONDED) && (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.TOY_ROBOT)) {
                	addFoundedDevice(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            		scanningFinished();
            }
            
        }
    };
    
    
    /**
     * this listener is used to handle onClick to child element action 
     * in expandable list where devices are shown
     */
    private OnChildClickListener myOnChildClickListener = new OnChildClickListener() {
		
		public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
           
			if(bluetoothAdapter.isDiscovering())
            	bluetoothAdapter.cancelDiscovery();
			
			String deviceName = childData.get(groupPosition).get(childPosition).get(Keys.DEVICE_NAME);
			String deviceAddress = childData.get(groupPosition).get(childPosition).get(Keys.DEVICE_ADDRESS);
			
			if( deviceName.equals( getResources().getString(R.string.noFoundedDevicesLabel) )){
					return false;
			}
			
            sendFoundedDeviceBackToActivity(deviceName, deviceAddress);
			return false;
		}
    };
    
    
    /**
     * This listener handle action groupClick
     * when click on group "scan for new devices" scanning begin 
     */
    private OnGroupClickListener myOnGroupClickListener = new OnGroupClickListener(){
		
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			try{
				Map<String,String> temp = groupData.get(groupPosition);
				getResources().getString(R.string.scanLabel);
				String gName = temp.get(Keys.GROUP_NAME);
				if(gName.equals(getResources().getString(R.string.scanLabel))){
					startScanningForDevices();
				}
			}catch (Exception e){ 
				Log.e(MainActivity.TAG,"ongroup click",e);
			}
			return false;
		}
    	
    };
	
    /* Getters and Setter declaration */

    
	/* Methods and Constructors declaration */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//getting needed resources
    	Resources res = getResources();
    	res.getStringArray(R.array.infoMsg);
    	res.getStringArray(R.array.errorsMsg);
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	setTitle(R.string.selectDevice);
        setResult(Activity.RESULT_CANCELED);    
        
    	setUpComponents();
    	loadBondedDevicesToExpList();
    	loadFoundedDevicesToExpList();
    	refreshExpListViewAdapter();
    	
    	getExpandableListView().expandGroup(0);
    	getExpandableListView().setOnChildClickListener(myOnChildClickListener);
    	getExpandableListView().setOnGroupClickListener(myOnGroupClickListener);
        super.onCreate(savedInstanceState);
    }
    
    public void setUpComponents(){
    	foundedDevices = new ArrayList<Map<String, String>>();
        groupData = new ArrayList<Map<String, String>>();
        childData = new ArrayList<List<Map<String, String>>>();
        Map<String, String> pairedDevs = new HashMap<String, String>();
        pairedDevs.put(Keys.GROUP_NAME, getResources().getString(R.string.pairedDevicesLabel));
        groupData.add(pairedDevs);
        Map<String, String> newDevs = new HashMap<String, String>();
        newDevs.put(Keys.GROUP_NAME, getResources().getString(R.string.scanLabel));
        groupData.add(newDevs);
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
    }
    
    public void loadBondedDevicesToExpList(){
    	Set<BluetoothDevice> temp = bluetoothAdapter.getBondedDevices();
        List<Map<String, String>> children = new ArrayList<Map<String, String>>();
    	for(BluetoothDevice d:temp){
    		if(d.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.TOY_ROBOT){ // show only NXT like devices
	    		Map<String, String> curChildMap = new HashMap<String, String>();
	            children.add(curChildMap);
	            curChildMap.put(Keys.DEVICE_NAME,d.getName());
	            curChildMap.put(Keys.DEVICE_ADDRESS,d.getAddress());
    		}
    	} 
    	childData.add(children);
    }
    
    public void loadFoundedDevicesToExpList(){
    	if(foundedDevices.size()>0){
    		childData.add(foundedDevices);
    	}else{
    		Map<String, String> temp = new HashMap<String, String>();
    		temp.put(Keys.DEVICE_NAME,getResources().getString(R.string.noFoundedDevicesLabel));
    		temp.put(Keys.DEVICE_ADDRESS,"");
    		List<Map<String, String>> tempChild = new ArrayList<Map<String,String>>();
    		tempChild.add(temp);
            childData.add(tempChild);
    	}
    }
    
    public void refreshExpListViewAdapter(){
    	childData.clear();
    	loadBondedDevicesToExpList();
    	loadFoundedDevicesToExpList();
        listAdapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {Keys.GROUP_NAME },
                new int[] { android.R.id.text1},
                childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { Keys.DEVICE_NAME, Keys.DEVICE_ADDRESS, },
                new int[] { android.R.id.text1, android.R.id.text2 }
                );
        setListAdapter(listAdapter);   
        getExpandableListView().expandGroup(1);
    }

    /**
     * starts scanning for BlueTooth devices like NXT
     * change the label and show progress on activity
     */
    public void startScanningForDevices(){
    	BluetoothDeviceManagerActivity.this.setProgressBarIndeterminateVisibility(true);
       
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); 
        if(bluetoothAdapter.isDiscovering())
        	return;
        
        String title = getResources().getString(R.string.scanningLabel);
        title += ", "+Integer.toString(foundedDevices.size());
        title += " "+getResources().getString(R.string.newFoundedDevicesLabel);
        setTitle(title);
        
        bluetoothAdapter.startDiscovery();
    }
    
    private void sendFoundedDeviceBackToActivity(String deviceName, String deviceAddress){
		Intent intent = new Intent();
        intent.putExtra(Keys.DEVICE_NAME, deviceName);
        intent.putExtra(Keys.DEVICE_ADDRESS,deviceAddress);
        setResult(Activity.RESULT_OK, intent);
        finish();
        return;
    }
    
    private void addFoundedDevice(BluetoothDevice device){
		Map<String,String> tempDev = new HashMap<String, String>();
		tempDev.put(Keys.DEVICE_NAME,device.getName());
		tempDev.put(Keys.DEVICE_ADDRESS,device.getAddress());
		foundedDevices.add(tempDev);
		String title = getResources().getString(R.string.scanningLabel);
        title += ", "+Integer.toString(foundedDevices.size());
        title += " "+getResources().getString(R.string.newFoundedDevicesLabel);
        BluetoothDeviceManagerActivity.this.setTitle(title);
		refreshExpListViewAdapter();
    }
    
    private void scanningFinished(){
    	BluetoothDeviceManagerActivity.this.setProgressBarIndeterminateVisibility(false);
        String title = getResources().getString(R.string.scanCompleteLabel);
        title += ", "+Integer.toString(foundedDevices.size());
        title += " "+getResources().getString(R.string.newFoundedDevicesLabel);
        BluetoothDeviceManagerActivity.this.setTitle(title);
    }
    

	@Override
	protected void onDestroy() {
		try{
			unregisterReceiver(mReceiver);
		}catch(Exception e){
			Log.e(MainActivity.TAG,"ondestroy error",e);
		};
		super.onDestroy();
	}

}

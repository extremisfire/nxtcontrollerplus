package nxtcontroller.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nxtcontroller.enums.ErrorCodes;
import nxtcontroller.enums.InfoCodes;
import nxtcontroller.program.NXTCommunicator;
import nxtcontroller.program.R;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.bluetooth.*;
import android.content.*;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
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
    public static final String NAME_OF_DEVICE = "name";
    public static final String GROUP_NAME = "group_name";
    public static final String ADDRESS_OF_DEVICE = "address";
	private static final int REQUEST_ENABLE_BT = 1;
	private String[] errors,infos; //message arrays
	
	/* private class properties declaration */
	private BluetoothAdapter bluetoothAdapter;
	private List<Map<String, String>> groupData; 
	private List<List<Map<String, String>>> childData; 
	private List<Map<String, String>> foundedDevices; 
    private ExpandableListAdapter listAdapter;
	
	/* 
	 * this receiver is used for scanning new BT devices
	 */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //if ((device.getBondState() != BluetoothDevice.BOND_BONDED) && (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.TOY_ROBOT)) {
                if (true) {
                	foundedDevices.clear();
					Map<String,String> tempDev = new HashMap<String, String>();
					tempDev.put(NAME_OF_DEVICE,device.getName());
					tempDev.put(ADDRESS_OF_DEVICE,device.getAddress());
					foundedDevices.add(tempDev);
	                String title = "";
	                title += " "+Integer.toString(foundedDevices.size());
	                title += " "+getResources().getString(R.string.newFoundedDevicesLabel);
	                BluetoothDeviceManagerActivity.this.setTitle(title);
					refreshExpListViewAdapter();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	BluetoothDeviceManagerActivity.this.setProgressBarIndeterminateVisibility(false);
                String title = getResources().getString(R.string.scanCompleteLabel);
                title += " "+Integer.toString(foundedDevices.size());
                title += " "+getResources().getString(R.string.newFoundedDevicesLabel);
                BluetoothDeviceManagerActivity.this.setTitle(title);
            }
            
        }
    };
    
    private OnChildClickListener myOnChildClickListener = new OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
           
			if(bluetoothAdapter.isDiscovering())
            	bluetoothAdapter.cancelDiscovery();
			String deviceName = childData.get(groupPosition).get(childPosition).get(NAME_OF_DEVICE);
			String deviceAddress = childData.get(groupPosition).get(childPosition).get(ADDRESS_OF_DEVICE);
			if( deviceName.equals( getResources().getString(R.string.noFoundedDevicesLabel) )){
					return false;
			}
            Intent intent = new Intent();
            intent.putExtra(NAME_OF_DEVICE, deviceName);
            intent.putExtra(ADDRESS_OF_DEVICE, deviceAddress);
            setResult(Activity.RESULT_OK, intent);
            finish();
			return false;
		}
    };
    
    private OnGroupClickListener myOnGroupClickListener = new OnGroupClickListener(){

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			try{
				Map<String,String> temp = groupData.get(groupPosition);
				getResources().getString(R.string.scanLabel);
				String gName = temp.get(GROUP_NAME);
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
    	errors = res.getStringArray(R.array.errorsMsg);
    	infos = res.getStringArray(R.array.infoMsg);
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	setTitle(R.string.selectDevice);
    	bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setResult(Activity.RESULT_CANCELED);
        
        if(!bluetoothAdapter.isEnabled())
        	turnOnBluetooth();
     
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
        pairedDevs.put(GROUP_NAME, getResources().getString(R.string.pairedDevicesLabel));
        groupData.add(pairedDevs);
        Map<String, String> newDevs = new HashMap<String, String>();
        newDevs.put(GROUP_NAME, getResources().getString(R.string.scanLabel));
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
	            curChildMap.put(NAME_OF_DEVICE,d.getName());
	            curChildMap.put(ADDRESS_OF_DEVICE,d.getAddress());
    		}
    	} 
    	childData.add(children);
    }
    
    public void loadFoundedDevicesToExpList(){
    	if(foundedDevices.size()>0){
    		childData.add(foundedDevices);
    	}else{
    		Map<String, String> temp = new HashMap<String, String>();
    		temp.put(NAME_OF_DEVICE,getResources().getString(R.string.noFoundedDevicesLabel));
    		temp.put(ADDRESS_OF_DEVICE,"");
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
                new String[] {GROUP_NAME },
                new int[] { android.R.id.text1},
                childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { NAME_OF_DEVICE, ADDRESS_OF_DEVICE },
                new int[] { android.R.id.text1, android.R.id.text2 }
                );
        setListAdapter(listAdapter);    
        getExpandableListView().expandGroup(0);
        getExpandableListView().expandGroup(1);
    }
    
	public void turnOnBluetooth(){
    	if (! bluetoothAdapter.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    }

 
    public void startScanningForDevices(){
    	BluetoothDeviceManagerActivity.this.setProgressBarIndeterminateVisibility(true);
    	if(!bluetoothAdapter.isEnabled()){
    		turnOnBluetooth();
    	}
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); 
        if(bluetoothAdapter.isDiscovering())
        	bluetoothAdapter.cancelDiscovery();
        
        String title = getResources().getString(R.string.scanningLabel)+"\n";
        title += " "+Integer.toString(foundedDevices.size());
        title += " "+getResources().getString(R.string.newFoundedDevicesLabel);
        setTitle(title);
        bluetoothAdapter.startDiscovery();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == REQUEST_ENABLE_BT){
    		if(resultCode == Activity.RESULT_OK){
    			String msg = infos[InfoCodes.BLUETOOTH_ACTIVATED];
    			Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    		}
    	}
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        String title = ((TextView) info.targetView).getText().toString();
        
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
            Toast.makeText(this, title + ": Child " + childPos + " clicked in group " + groupPos,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            Toast.makeText(this, title + ": Group " + groupPos + " clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        return false;
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
		try{
			unregisterReceiver(mReceiver);
		}catch(Exception e){
			Log.e(MainActivity.TAG,"ondestroy error",e);
		};
		super.onDestroy();
	}

}

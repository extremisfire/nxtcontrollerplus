package nxtcontroller.activity;

import nxtcontroller.enums.InfoCodes;
import nxtcontroller.enums.TypeOfMessage;
import nxtcontroller.program.R;
import android.app.Activity;
import android.content.res.Resources;
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
	
	/* private class properties declaration */
    private TextView statusLabel; 
    private Button connectButton;
	
    
    /* Methods and Constructors declaration */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        statusLabel = (TextView) findViewById(R.id.statusLabel);
       
        connectButton = (Button) findViewById(R.id.connectButton);
        setUpListeners();
    }
    
    public void setUpListeners(){
    	try{
    		Resources res = this.getResources();
    		String [] temp = res.getStringArray(R.array.infoMsg);
    		 statusLabel.setText(temp[InfoCodes.DISCONNECTED]);
            connectButton.setOnClickListener(new View.OnClickListener()  {
                public void onClick(View v) {
                	//TODO
                }
            });
    	}catch(Exception e){
    		Log.e(MainActivity.TAG," setUpListeners error",e);
    	}
    }
    
    
    // The Handler that gets information back from BlueToothDeviceManager and NXTCommunicator
    private final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	try{
        		switch (msg.what) {
            	case(TypeOfMessage.TOAST_ERROR):
            		Toast.makeText(getApplicationContext(), (String) msg.obj,Toast.LENGTH_LONG).show();
            	break;
            	case(TypeOfMessage.TOAST_INFO):
            		Toast.makeText(getApplicationContext(), (String) msg.obj,Toast.LENGTH_SHORT).show();
            	break;
            	case(TypeOfMessage.STATUS_LABEL):
            		statusLabel.setText((String)msg.obj);
            	break;
            }
        	}catch (Exception e){
        		Log.e(TAG,"meesage handling error",e);
        	}

        }
    };
    
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
		super.onDestroy();
	}
}
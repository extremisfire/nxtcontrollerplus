package com.nxtcontrollerplus.activity;

import com.nxtcontrollerplus.R;
import com.nxtcontrollerplus.enums.Keys;
import com.nxtcontrollerplus.enums.nxtbuiltin.Motor;
import com.nxtcontrollerplus.enums.nxtbuiltin.SensorID;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class SettingsActivity extends Activity{
 
	public static final String PREFERENCES_NAME = "MySettings";
	private Button saveButton;
	private Spinner leftMotor,rightMotor,sensor1,sensor2,sensor3,sensor4;
	private SharedPreferences currentSettings;

	@Override
	protected void onCreate(Bundle state){
       super.onCreate(state);
       setTitle(getResources().getString(R.string.settingsText));
       currentSettings = getSharedPreferences(PREFERENCES_NAME, 0);
	   setContentView(R.layout.settings);
	   initializeComponents();
	}
	
	private void initializeComponents(){
		saveButton = (Button)findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
            	saveSettings();
                setResult(RESULT_OK);
                finish();
            }
        });
		leftMotor = (Spinner)findViewById(R.id.motorLeftSpinner);
		rightMotor = (Spinner)findViewById(R.id.motorRightSpinner);
		sensor1 = (Spinner)findViewById(R.id.sensor1Spinner);
		sensor2 = (Spinner)findViewById(R.id.sensor2Spinner);
		sensor3 = (Spinner)findViewById(R.id.sensor3Spinner);
		sensor4 = (Spinner)findViewById(R.id.sensor4Spinner);
		
		leftMotor.setSelection(currentSettings.getInt(Keys.MOTOR_LEFT, Motor.A));
		rightMotor.setSelection(currentSettings.getInt(Keys.MOTOR_RIGHT, Motor.B));
		sensor1.setSelection(currentSettings.getInt(Keys.SENSOR_1, SensorID.NO_SENSOR));
		sensor2.setSelection(currentSettings.getInt(Keys.SENSOR_2, SensorID.NO_SENSOR));
		sensor3.setSelection(currentSettings.getInt(Keys.SENSOR_3, SensorID.NO_SENSOR));
		sensor4.setSelection(currentSettings.getInt(Keys.SENSOR_4, SensorID.NO_SENSOR));
	}
	
	private void saveSettings(){
		SharedPreferences.Editor editor = currentSettings.edit();
		editor.putInt(Keys.MOTOR_LEFT, leftMotor.getSelectedItemPosition());
		editor.putInt(Keys.MOTOR_RIGHT, rightMotor.getSelectedItemPosition());
		editor.putInt(Keys.SENSOR_1, sensor1.getSelectedItemPosition());
		editor.putInt(Keys.SENSOR_2, sensor2.getSelectedItemPosition());
		editor.putInt(Keys.SENSOR_3, sensor3.getSelectedItemPosition());
		editor.putInt(Keys.SENSOR_4, sensor4.getSelectedItemPosition());
		editor.commit();
	}

}

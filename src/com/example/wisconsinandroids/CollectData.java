package com.example.wisconsinandroids;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.location.*;
import android.widget.Button;

public class CollectData extends ActionBarActivity implements SensorEventListener, GpsStatus.Listener, GpsStatus.NmeaListener, View.OnClickListener{
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private LocationManager mGPS;
	private TextView AccelX, AccelY, AccelZ, GPS_String, GPS_Status;
	private Button start, stop;
	private boolean started = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_data);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		

		GPS_String = (TextView) findViewById(R.id.GPS_String);
		GPS_Status = (TextView) findViewById(R.id.GPS_Status);
        start = (Button) findViewById(R.id.StartButton);//Returns NULL...
        stop = (Button) findViewById(R.id.StopButton);// Returns NULL...
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGPS = (LocationManager) getSystemService(LOCATION_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        
        mGPS.addGpsStatusListener(this);
        mGPS.addNmeaListener(this);
        
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.collect_data, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_collect_data,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onNmeaReceived(long timestamp, String nmea) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
		AccelX = (TextView) findViewById(R.id.AccelX);
		AccelY = (TextView) findViewById(R.id.AccelY);
		AccelZ = (TextView) findViewById(R.id.AccelZ);
        AccelX.setText("X:" + event.values[0]);
        AccelY.setText("Y:" + event.values[1]);
        AccelZ.setText("Z:" + event.values[2]);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
    public void onClick(View v) {
    	if(v.getId() == start.getId())
    	{
    		started = true;
    	}
    	else if(v.getId() == stop.getId())
    	{
    		started = false;
    	}
        // Do something in response to button click
    	stop.setClickable(started);
    	start.setClickable(!started);
    }

}

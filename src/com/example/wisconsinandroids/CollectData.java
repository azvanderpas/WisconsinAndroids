package com.example.wisconsinandroids;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.location.*;
import android.widget.Button;

public class CollectData extends ActionBarActivity implements SensorEventListener, GpsStatus.Listener, 
GpsStatus.NmeaListener, View.OnClickListener, LocationListener
{
	
	final int ACCURACY_FINE = 1;
	final int ACCURACY_COARSE = 2;
	final int ACCURACY_HIGH = 3;
	final int ACCURACY_MEDIUM = 2;
	final int ACCURACY_LOW = 1;
	final int NO_REQUIREMENT = 0;
	final int POWER_HIGH = 3;
	final int POWER_MEDIUM = 2;
	final int POWER_LOW = 1;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private LocationManager mGPS;
	private TextView AccelX, AccelY, AccelZ, GPS_String, GPS_Status, Latitude, Longitude;
	private Button startStop;
	private boolean started = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_data);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGPS = (LocationManager) getSystemService(LOCATION_SERVICE);
        mGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, (float) 0.5, this);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        
        mGPS.addGpsStatusListener(this);
        mGPS.addNmeaListener(this);
        

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.collect_data, menu);
		

        startStop = (Button) findViewById(R.id.StartStopButton);
        startStop.setOnClickListener(this);
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
		if(!started)
			return;
		GPS_String = (TextView) findViewById(R.id.GPS_String);
		GPS_String.setText("TimeStamp: " + timestamp + "\nGPS String: " + nmea);
	}

	@Override
	public void onGpsStatusChanged(int event) {
		if(!started)
			return;
		GPS_Status = (TextView) findViewById(R.id.GPS_Status);
		String stat;
		switch(event)
		{
		case 1: stat = "GPS_EVENT_STARTED";
		case 2: stat = "GPS_EVENT_STOPPED";
		case 3: stat = "GPS_EVENT_FIRST_FIX";
		case 4: stat = "GPS_EVENT_SATELLITE_STATUS";
		default: stat = "?";
		}
		GPS_Status.setText("GPS Status: " + stat);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || !started)
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
    	started = !started;
    	startStop.setText((started ? "Stop" : "Start"));
    }


	@Override
	public void onLocationChanged(Location location) {
		if(!started)
			return;
		Longitude = (TextView) findViewById(R.id.Longitude);
		Latitude = (TextView) findViewById(R.id.Latitude);
		
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		boolean east = (lon > 0);
		boolean north = (lat > 0);
		if(!east)
		{
			lon = -lon;
		}
		if(!north)
		{
			lat = -lat;
		}
		int degLat = (int)lat;
		int degLon = (int)lon;
		int minLat = (int)((lat-(double)degLat) * 60.0);
		int minLon = (int)((lon-(double)degLon) * 60.0);
		int secLat = (int)((((lat-degLat) * 60.0) - minLat) * 60.0);
		int secLon = (int)((((lon-degLon) * 60.0) - minLon) * 60.0);
		
		Longitude.setText("Long: " + degLon + "° " + minLon + "' " + secLon + "\"" + (east ? "E" : "W"));
		Latitude.setText("Lat: " +degLat + "° " + minLat + "' " + secLat + "\"" + (north ? "N" : "S"));
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}

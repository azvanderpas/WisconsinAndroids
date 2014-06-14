package com.example.wisconsinandroids;

import java.util.Date;
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

public class CollectData extends ActionBarActivity implements SensorEventListener, /*GpsStatus.Listener,*/ 
GpsStatus.NmeaListener, View.OnClickListener, LocationListener
{
	
	private static final int ACCURACY_FINE = 1;
	private static final int ACCURACY_COARSE = 2;
	private static final int ACCURACY_HIGH = 3;
	private static final int ACCURACY_MEDIUM = 2;
	private static final int ACCURACY_LOW = 1;
	private static final int NO_REQUIREMENT = 0;
	private static final int POWER_HIGH = 3;
	private static final int POWER_MEDIUM = 2;
	private static final int POWER_LOW = 1;
	private static final boolean useLegacyOrientationSensor = true;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer,mMagneticField, mGyro;
	private LocationManager mGPS;
	private TextView AccelX, AccelY, AccelZ, GPS_String, Latitude, Longitude, 
		azimuth, pitch, roll, magX, magY, magZ, GPS_Status;
	private Button startStop;
	private boolean started = false;
	private float accelValues[] = new float[3];
	private float magValues[] = new float[3];

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
        mGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, (float) 0.0, this);
        
        if(useLegacyOrientationSensor)
        {
        	mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        	mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_UI);
        }
        
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_UI);
        
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        
       // mGPS.addGpsStatusListener(this);
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
		
		Date stamp = new Date(timestamp);
		GPS_String = (TextView) findViewById(R.id.GPS_String);
		GPS_String.setText(stamp + "\nGPS String:\n" + nmea);
	}

	/*@Override
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
	}*/

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(!started)
			return;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
        	AccelX = (TextView) findViewById(R.id.AccelX);
			AccelY = (TextView) findViewById(R.id.AccelY);
			AccelZ = (TextView) findViewById(R.id.AccelZ);
			accelValues[0] = event.values[0];
			accelValues[1] = event.values[1];
			accelValues[2] = event.values[2];
	        AccelX.setText("\tX:" + accelValues[0]);
	        AccelY.setText("\tY:" + accelValues[1]);
	        AccelZ.setText("\tZ:" + accelValues[2]); 
		}
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
        	magX = (TextView) findViewById(R.id.magX);
			magY = (TextView) findViewById(R.id.magY);
			magZ = (TextView) findViewById(R.id.magZ);
			magValues[0] = event.values[0];
			magValues[1] = event.values[1];
			magValues[2] = event.values[2];
	        magX.setText("\tX:" + magValues[0] + "uT");
	        magY.setText("\tY:" + magValues[1] + "uT");
	        magZ.setText("\tZ:" + magValues[2] + "uT"); 
	        if(!useLegacyOrientationSensor)
	        {
	        	float orientation[] = new float[9];
	        	float rotationMatrixR[] = new float[9];
	        	float rotationMatrixI[] = new float[9];
	        	if(SensorManager.getRotationMatrix(rotationMatrixR, rotationMatrixI, accelValues, magValues))
	        	{
					SensorManager.getOrientation(rotationMatrixR, orientation);
					for (int i = 0; i < orientation.length; i++)
					{
						orientation[i]*=(180/Math.PI);
					}
					orientation[0] = (orientation[0] < 0) ? orientation[0] + 360 : orientation[0];
					azimuth = (TextView) findViewById(R.id.azimuth);
					pitch = (TextView) findViewById(R.id.pitch);
					roll = (TextView) findViewById(R.id.roll);	
			        azimuth.setText("\tAzimuth:" + orientation[0] + "° ");
			        pitch.setText("\tPitch:" + orientation[1] + "° ");
			        roll.setText("\tRoll:" + orientation[2] + "° "); 
	        	}
	        }
        }
        if(useLegacyOrientationSensor)
        {
        	if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
	        {
	        	azimuth = (TextView) findViewById(R.id.azimuth);
				pitch = (TextView) findViewById(R.id.pitch);
				roll = (TextView) findViewById(R.id.roll);
		        azimuth.setText("\tAzimuth:" + event.values[0] + "° ");
		        pitch.setText("\tPitch:" + event.values[1] + "° ");
		        roll.setText("\tRoll:" + event.values[2] + "° ");      	
	        }
        }
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

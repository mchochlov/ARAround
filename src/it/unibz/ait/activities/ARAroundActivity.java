package it.unibz.ait.activities;

import it.unibz.ait.R;
import it.unibz.ait.services.PlacesSearchService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ARAroundActivity extends Activity {

	private static final String TAG1 = "PlacesLocationListener";
	private static final String TAG2 = "ARAroundActivity";

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0; // in
																		// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 30000; // in
																// Milliseconds

	protected LocationManager locationManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
				new PlacesLocationListener());

	}

	public class PlacesLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			Log.i(TAG1, "Location: , longtitude - " + location.getLongitude()
					+ ", latitude - " + location.getLatitude());

			Log.i(TAG2, "About to execute service...");
			if (isOnline() != null) {
				Log.i(TAG2, "Starting service...");

				Intent intent = new Intent(ARAroundActivity.this,
						PlacesSearchService.class);
				intent.putExtra("longtitude", location.getLongitude());
				intent.putExtra("latitude", location.getLatitude());
				startService(intent);
				
				
			}

		}

		public void onProviderDisabled(String s) {
			Toast.makeText(ARAroundActivity.this,
					"Provider disabled by the user. GPS turned off",
					Toast.LENGTH_LONG).show();

		}

		public void onProviderEnabled(String s) {
			Toast.makeText(ARAroundActivity.this,
					"Provider enabled by the user. GPS turned on",
					Toast.LENGTH_LONG).show();

		}

		public void onStatusChanged(String s, int i, Bundle b) {
			Toast.makeText(ARAroundActivity.this, "Provider status changed",
					Toast.LENGTH_LONG).show();

		}

		public NetworkInfo isOnline() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo();
		}

	}

}
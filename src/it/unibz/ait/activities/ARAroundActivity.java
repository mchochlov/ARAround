package it.unibz.ait.activities;

import it.unibz.ait.R;
import it.unibz.ait.services.PlacesSearchService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class ARAroundActivity extends Activity {
	
	private static final String TAG = "ARAroundActivity";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.i(TAG, "About to execute service...");
		if (isOnline() != null)
		{
			Log.i(TAG, "Starting service...");
			Intent intent = new Intent(this, PlacesSearchService.class);
			startService(intent);	
		}
		
	}

	public NetworkInfo isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}
}
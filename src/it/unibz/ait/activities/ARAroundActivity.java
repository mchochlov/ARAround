package it.unibz.ait.activities;

import it.unibz.ait.R;
import it.unibz.ait.orientation.OrientationListener;
import it.unibz.ait.orientation.OrientationManager;
import it.unibz.ait.model.PlaceData;
import it.unibz.ait.services.PlacesSearchService;
import it.unibz.ait.services.ServiceResultReceiver;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;

public class ARAroundActivity extends Activity implements OrientationListener,
		ServiceResultReceiver.Receiver {

	private static Context CONTEXT;
	private static final String TAG1 = "PlacesLocationListener";
	private static final String TAG2 = "ARAroundActivity";
	private static final String TAGA = "Azimuth";
	private static final String TAGB = "Loc arround azimuth";
	public Location currentLocation;

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0; // in
																		// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in
	// Milliseconds

	protected LocationManager locationManager;
	private Camera mCamera;
	private CameraPreview cameraPreview;
	private PlacesLocationListener plListener;
	public ServiceResultReceiver mReceiver;
	public PoiView poiView;
	public ArrayList<PlaceData> places = new ArrayList<PlaceData>();

	public float cameraHorizontalAngle;
	public float cameraVerticalAngle;

	float screenX ;
	float screenY ;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		CONTEXT = this;// -> will write meaning of azimuth

		// Create an instance of Camera
		mCamera = getCameraInstance();

		cameraHorizontalAngle = mCamera.getParameters()
				.getHorizontalViewAngle();

		cameraVerticalAngle = mCamera.getParameters().getVerticalViewAngle();

		// Create our Preview view and set it as the content of our activity.
		cameraPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(cameraPreview);

		poiView = new PoiView(this);
		preview.addView(poiView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		plListener = new PlacesLocationListener();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES,
				// MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
				// new PlacesLocationListener());
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, plListener);
		mReceiver = new ServiceResultReceiver(new Handler());
		mReceiver.setReceiver(this);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
		locationManager.removeUpdates(plListener);

		mReceiver.setReceiver(null);
		if (OrientationManager.isListening()) {
			OrientationManager.stopListening();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCamera == null) {
			cameraPreview.setCamera(getCameraInstance());
		}
		if (OrientationManager.isSupported()) {
			OrientationManager.startListening(this);
		}
		if (mReceiver.getReceiver() == null) {
			mReceiver.setReceiver(this);
		}
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			Log.d(TAG2, "Error setting camera preview: " + e.getMessage());
		}
		return c; // returns null if camera is unavailable
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
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
				intent.putExtra("receiver", mReceiver);
				startService(intent);
				currentLocation = location;
			}
		}

		public void onProviderDisabled(String s) {
			// Log.i(TAG1, "Provider disabled by the user. GPS turned off");
		}

		public void onProviderEnabled(String s) {
			// Log.i(TAG1, "Provider enabled by the user. GPS turned on");
		}

		public void onStatusChanged(String s, int i, Bundle b) {
			Log.i(TAG1, "Provider status changed");
		}

		public NetworkInfo isOnline() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo();
		}

	}

	protected void onDestroy() {
		super.onDestroy();
		if (OrientationManager.isListening()) {
			OrientationManager.stopListening();
		}

	}

	public static Context getContext() {
		return CONTEXT;
	}

	public void onOrientationChanged(float azimuth, float pitch, float roll) {

		for (PlaceData place : places) {
			// azimuth = 41;
			// boolean visiblePlace = false;
			double lat1 = currentLocation.getLatitude();
			double lng1 = currentLocation.getLongitude();
			double halfCameraAngle = cameraHorizontalAngle * 0.5;
			double phoneRightSide = azimuth + halfCameraAngle;
			if (phoneRightSide > 360)
				phoneRightSide = phoneRightSide - 360;

			double phoneLeftSide = azimuth - halfCameraAngle;
			if (phoneLeftSide < 0)
				phoneLeftSide = phoneLeftSide + 360;

			double lat2 = place.getLatitude();
			double lng2 = place.getLongitude();
			double longitudinalDifference = lng2 - lng1;
			double latitudinalDifference = lat2 - lat1;

		/*	double locAzimuth = Math
					.toDegrees((Math.PI * .5d)
							- Math.atan(latitudinalDifference
									/ longitudinalDifference));
			if (longitudinalDifference > 0)
				locAzimuth = locAzimuth;
			else if (longitudinalDifference < 0)
				locAzimuth = locAzimuth + Math.PI;
			else if (latitudinalDifference < 0)
				locAzimuth = Math.PI;
			else
				locAzimuth = 0;
*/
			
			double y = Math.sin(longitudinalDifference) * Math.cos(lat2);
			double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2)* Math.cos(longitudinalDifference);
			double angle = Math.atan2(y, x); //not finished here yet
			double headingDeg = azimuth;
			double angleDeg = angle * 180/Math.PI;
			double heading = headingDeg*Math.PI/180;
			angle =((angleDeg + 360)% 360) * Math.PI/180; //normalize to 0 to 360 (instead of -180 to 180), then convert back to radians
			double locAzimuth = angle * 180/Math.PI;
			
			
			// double locAzimuth = 167;
			if (phoneRightSide >= phoneLeftSide) {
				if ((locAzimuth > phoneLeftSide)
						&& (locAzimuth < phoneRightSide)) {
					place.setVisible(true);

				} else {
					place.setVisible(false);
				}

			}
			if (phoneRightSide <= phoneLeftSide) {
				if ((locAzimuth < (phoneRightSide + 360.0))
						&& (locAzimuth > phoneLeftSide)) {
					place.setVisible(true);
				} else {
					place.setVisible(false);
				}
			}

			//distance
			
			double R = 6371; // km
			double dLat = Math.toRadians(lat2-lat1);
			double dLon = Math.toRadians(lng2-lng1);
			double lat1R = Math.toRadians(lat1);
			double lat2R = Math.toRadians(lat2);

			double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
			        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1R) * Math.cos(lat2R); 
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
			double distance = R * c;
			
			// convert from 3D to 2D
			double xCoord = Math.sin(angle-heading) * distance;
			double zCoord = Math.cos(angle-heading) * distance;
			 screenX = (float) ((xCoord * 256) / zCoord);
			 //pitch  - our y
			// screenY = (float) ((pitch * 256) / zCoord);
			Log.i(TAGA, String.valueOf(azimuth) + " " + distance + " " + screenX );
			Log.i(TAGB, place.getProvider() + " " + locAzimuth + place.isVisible());

		}
		poiView.postInvalidate();
		/*
		 * ((TextView) findViewById(R.id.pitch)).setText(String.valueOf(pitch));
		 * ((TextView) findViewById(R.id.roll)).setText(String.valueOf(roll));
		 */
	}

	// @Override
	public void onBottomUp() {
		// Toast.makeText(this, "Bottom UP", 1000).show();
	}

	// @Override
	public void onLeftUp() {
		// Toast.makeText(this, "Left UP", 1000).show();
	}

	// @Override
	public void onRightUp() {
		// Toast.makeText(this, "Right UP", 1000).show();
	}

	// @Override
	public void onTopUp() {
		// Toast.makeText(this, "Top UP", 1000).show();
	}

	public class PoiView extends View {

		public PoiView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int pos = 30;
			for (PlaceData place : places) {
				if (place.isVisible()) {
					Paint paint = new Paint();
					paint.setStyle(Paint.Style.FILL_AND_STROKE);
					paint.setColor(Color.WHITE);
					paint.setShadowLayer(3, 0, 0, Color.BLACK);
					paint.setTypeface(Typeface.DEFAULT_BOLD);
					paint.setTextSize(12);

					canvas.drawText(place.getProvider(), screenX, pos, paint);
					

					pos = pos + 30;
				}
			}

			super.onDraw(canvas);
		}
	}

	/** A basic Camera preview class */
	public class CameraPreview extends SurfaceView implements
			SurfaceHolder.Callback {

		private SurfaceHolder mHolder;
		private Camera mCamera;
		private static final String TAG_CAM = "CameraPreview";

		public CameraPreview(Context context, Camera camera) {
			super(context);
			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			this.mCamera = camera;
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void setCamera(Camera camera) {
			this.mCamera = camera;
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			} catch (IOException e) {
				Log.d(TAG_CAM,
						"Error setting camera preview: " + e.getMessage());
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// If your preview can change or rotate, take care of those events
			// here.
			// Make sure to stop the preview before resizing or reformatting it.

			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}

			// stop preview before making changes
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}

			// make any resize, rotate or reformatting changes here

			// start preview with new settings
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
				Log.d(TAG_CAM,
						"Error starting camera preview: " + e.getMessage());
			}
		}
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		places = resultData.getParcelableArrayList("results");
		poiView.postInvalidate();
	}

}

package it.unibz.ait.activities;

import it.unibz.ait.R;
import it.unibz.ait.services.PlacesSearchService;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Toast;

public class ARAroundActivity extends Activity {

	private static final String TAG1 = "PlacesLocationListener";
	private static final String TAG2 = "ARAroundActivity";

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0; // in
																		// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 30000; // in
	// Milliseconds

	protected LocationManager locationManager;
	private Camera mCamera;
	private CameraPreview cameraPreview;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mCamera = getCameraInstance();
		cameraPreview = new CameraPreview(this, mCamera);
		PoiView poiView = new PoiView(this);
		setContentView(cameraPreview);
		addContentView(poiView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
				new PlacesLocationListener());

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera();
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (mCamera == null) {
			cameraPreview.setCamera(getCameraInstance());
		}
		// PoiView poiView = new PoiView(this);
		// setContentView(cameraPreview);
		// addContentView(poiView, new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));

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

	public class PoiView extends View {

		public PoiView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			canvas.drawText("Test Text", 10, 10, paint);
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
			mCamera = camera;

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
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
			// empty. Take care of releasing the Camera preview in your
			// activity.
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

}
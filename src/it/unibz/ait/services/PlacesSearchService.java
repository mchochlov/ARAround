package it.unibz.ait.services;

import java.io.IOException;

import it.unibz.ait.model.Place;
import it.unibz.ait.model.PlaceList;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class PlacesSearchService extends IntentService {

	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";

	private static final HttpTransport transport = new ApacheHttpTransport();

	private static final String API_KEY = "AIzaSyDsuAJz24_oGbgqUzALidG2jD_-Wu-831E";

	private static final String TAG = "PlacesSearchService";

	public PlacesSearchService() {
		super("PlacesSearchService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			double latitude = intent.getDoubleExtra("latitude", 0);
			double longitude = intent.getDoubleExtra("longtitude", 0);
			Log.i(TAG, "Perform Search ....");
			HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
			request.url.put("key", API_KEY);
			request.url.put("location", latitude + "," + longitude);
			request.url.put("radius", 50);
			request.url.put("sensor", "false");

			PlaceList places = request.execute().parseAs(PlaceList.class);
			Log.i(TAG, "STATUS = " + places.status);
			for (Place place : places.results) {
				Log.i(TAG, place.toString());
			}
			


		} catch (HttpResponseException e) {
			try {
				Log.e(TAG, e.getResponse().parseAsString(), e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HttpRequestFactory createRequestFactory(
			final HttpTransport transport) {

		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				headers.setApplicationName("Google-Places-DemoApp");
				request.headers = headers;
				JsonHttpParser parser = new JsonHttpParser();
				parser.jsonFactory = new JacksonFactory();
				request.addParser(parser);
			}
		});
	}

}

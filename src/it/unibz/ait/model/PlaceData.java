package it.unibz.ait.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceData implements Parcelable {

	private String name;
	private float lng;
	private float lat;

	public PlaceData(String name, float lng, float lat) {
		this.name = name;
		this.lng = lng;
		this.lat = lat;
	}

	public String getName() {
		return name;
	}

	public float getLng() {
		return lng;
	}

	public float getLat() {
		return lat;
	}

	public int describeContents() {
		return this.hashCode();
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeFloat(lng);
		dest.writeFloat(lat);
	}

	public static final Parcelable.Creator<PlaceData> CREATOR = new Parcelable.Creator<PlaceData>() {
		public PlaceData createFromParcel(Parcel in) {
			return new PlaceData(in);
		}

		public PlaceData[] newArray(int size) {
			return new PlaceData[size];
		}
	};

	private PlaceData(Parcel in) {
		this.name = in.readString();
		this.lng = in.readFloat();
		this.lat = in.readFloat();
	}

}

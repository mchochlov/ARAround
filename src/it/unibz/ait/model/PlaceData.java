package it.unibz.ait.model;

import android.location.Location;
import android.os.Parcel;

public class PlaceData extends Location {

	private byte visible;
	private float x;
	private float y;

	public PlaceData(String name) {
		super(name);
		this.visible = 0;
		this.x = 0;
		this.y = 0;
	}

	public boolean isVisible() {
		return visible == 1;
	}

	public void setVisible(boolean visible) {
		if (visible)
			this.visible = 1;
		else 
			this.visible = 0 ;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeByte(visible);
		dest.writeFloat(x);
		dest.writeFloat(y);
	}

}

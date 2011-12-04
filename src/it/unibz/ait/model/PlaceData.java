package it.unibz.ait.model;

import android.location.Location;
import android.os.Parcel;

public class PlaceData extends Location {

	private byte visible;

	public PlaceData(String name) {
		super(name);
		this.visible = 0;
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

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeByte(visible);
	}

}

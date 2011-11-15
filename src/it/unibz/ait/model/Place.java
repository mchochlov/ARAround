package it.unibz.ait.model;

import com.google.api.client.util.Key;

public class Place {

	@Key
	public String name;

	@Key
	public Geometry geometry;

	@Override
	public String toString() {
		return "Place [name=" + name + ", longitudes=" + geometry.location.lng
				+ ", latitudes=" + geometry.location.lat + "]";
	}

}

package it.unibz.ait.model;

import com.google.api.client.util.Key;

public class Place {

	@Key
	public String name;

	@Key
	public Geometry geometry;

	@Key
	public String[] types;

	@Override
	public String toString() {

		String listOfPlaceTypes = "";
		for (String typeOfPlace : types) {
			listOfPlaceTypes = listOfPlaceTypes + " " + typeOfPlace;
		}

		return "Place [name=" + name + ", longitudes=" + geometry.location.lng
				+ ", latitudes=" + geometry.location.lat + "]" + " type:"
				+ listOfPlaceTypes;
	}

	public boolean isExcludedType() {
		String listOfPlaceTypes = "";
		for (String typeOfPlace : types) {
			listOfPlaceTypes = listOfPlaceTypes + " " + typeOfPlace;
		}
		if (listOfPlaceTypes.contains("locality")
				|| listOfPlaceTypes.contains("country")
				|| listOfPlaceTypes.contains("street_number")
				|| listOfPlaceTypes.contains("route")
				|| listOfPlaceTypes.contains("political")) {
			return true;
		} else
			return false;
	}
}

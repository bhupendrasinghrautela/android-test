package com.makaan.response.search;

/**
 * A model for typeahead getSearchResult response data
 * 
 * */
public class Search {

	private String id;
	private String label;
	private String type;
	private String displayText;
	private String coreText;
	private String googlePlaceId;
	private String city;
	private String locality;
	private int cityId;
	private int localityId;
	private int projectId;
	private boolean isSuggestion;
	private boolean isGooglePlace;
	private double latitude;
	private double longitude;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {this.city = city;}


	public String getType() {
		return type;
	}
	public void setType(String type) {this.type = type;}

	public String getDisplayText() {
		return displayText;
	}
	public void setDisplayText(String displayText) {this.displayText = displayText;}

	public String getCoreText() {
		return coreText;
	}
	public void setCoreText(String coreText) {this.coreText = coreText;}

	public String getGooglePlaceId() {
		return googlePlaceId;
	}

	public void setGooglePlaceId(String googlePlaceId) {
		this.googlePlaceId = googlePlaceId;
	}

	public boolean isSuggestion() {
		return isSuggestion;
	}

	public void setSuggestion(boolean isSuggestion) {
		this.isSuggestion = isSuggestion;
	}

	public boolean isGooglePlace() {
		return isGooglePlace;
	}

	public void setGooglePlace(boolean isGooglePlace) {
		this.isGooglePlace = isGooglePlace;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getLocalityId() {
		return localityId;
	}

	public void setLocalityId(int localityId) {
		this.localityId = localityId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
}
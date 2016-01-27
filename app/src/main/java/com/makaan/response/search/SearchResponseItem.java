package com.makaan.response.search;

/**
 * A model for typeahead getSearchResult response data
 * 
 * */
public class SearchResponseItem implements Cloneable{

	public String id, entityId, entityName,
			type, displayText,
			builderName, builderId,
			googlePlaceId,  city, redirectUrlFilters;

	public boolean isSuggestion, isGooglePlace;
	private double latitude, longitude;
	public long cityId;

	public SearchResponseItem(SearchResponseItem search) {
		this.id = search.id;
		this.cityId = search.cityId;
		this.entityId = search.entityId;
		this.entityName = search.entityName;
		this.type = search.type;
		this.displayText = search.displayText;
		this.builderName = search.builderName;
		this.builderId = search.builderId;
		this.googlePlaceId = search.googlePlaceId;
		this.city = search.city;
		this.redirectUrlFilters = search.redirectUrlFilters;
		this.isSuggestion = search.isSuggestion;
		this.isGooglePlace = search.isGooglePlace;
		this.latitude = search.latitude;
		this.longitude = search.longitude;
	}

	@Override
	public SearchResponseItem clone() throws CloneNotSupportedException {
		return new SearchResponseItem(this);
	}
}
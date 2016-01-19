package com.makaan.response.search;

/**
 * A model for typeahead getSearchResult response data
 * 
 * */
public class Search {

	public String id, entityId, entityName,
			type, displayText,
			builderName, builderId,
			googlePlaceId,  city, redirectUrlFilters;

	public boolean isSuggestion, isGooglePlace;
	private double latitude, longitude;

}
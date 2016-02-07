package com.makaan.response.search;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A model for typeahead getSearchResult response data
 * 
 * */
public class SearchResponseItem implements Cloneable, Parcelable{

	public String id, entityId, entityName,
			type, displayText,
			builderName, builderId,
			googlePlaceId,  city, redirectUrlFilters;

	public boolean isSuggestion, isGooglePlace;
	private double latitude, longitude;
	public long cityId;

	public SearchResponseItem(){}

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

	protected SearchResponseItem(Parcel in) {
		id = in.readString();
		entityId = in.readString();
		entityName = in.readString();
		type = in.readString();
		displayText = in.readString();
		builderName = in.readString();
		builderId = in.readString();
		googlePlaceId = in.readString();
		city = in.readString();
		redirectUrlFilters = in.readString();
        isSuggestion = in.readByte() != 0;
        isGooglePlace = in.readByte() != 0;
		latitude = in.readDouble();
		longitude = in.readDouble();
		cityId = in.readLong();
	}

	public static final Creator<SearchResponseItem> CREATOR = new Creator<SearchResponseItem>() {
		@Override
		public SearchResponseItem createFromParcel(Parcel in) {
			return new SearchResponseItem(in);
		}

		@Override
		public SearchResponseItem[] newArray(int size) {
			return new SearchResponseItem[size];
		}
	};

	@Override
	public SearchResponseItem clone() throws CloneNotSupportedException {
		return new SearchResponseItem(this);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(entityId);
		dest.writeString(entityName);
		dest.writeString(type);
		dest.writeString(displayText);
		dest.writeString(builderName);
		dest.writeString(builderId);
		dest.writeString(googlePlaceId);
		dest.writeString(city);
		dest.writeString(redirectUrlFilters);
		dest.writeByte((byte) (isSuggestion ? 1 : 0));
        dest.writeByte((byte) (isGooglePlace ? 1 : 0));
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeLong(cityId);
	}
}
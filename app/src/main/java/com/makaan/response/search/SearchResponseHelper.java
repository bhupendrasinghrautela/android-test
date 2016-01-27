package com.makaan.response.search;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.makaan.MakaanBuyerApplication;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.response.master.ApiLabel;
import com.makaan.service.ListingService;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sunil on 19/01/16.
 */
public class SearchResponseHelper {

    //WIP

    public static void resolveSearch(ArrayList<SearchResponseItem> searchResponseArrayList, Context context){

        if (searchResponseArrayList == null || searchResponseArrayList.size() == 0) {
            return;
        }
        // get last item from the arraylist
        SearchResponseItem searchItem = searchResponseArrayList.get(searchResponseArrayList.size() - 1);

        if(TextUtils.isEmpty(searchItem.type)){
            return;
        }

        if(searchItem.type.contains(SearchSuggestionType.SUGGESTION.getValue())){
            ((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_SUGGESTION, searchItem.redirectUrlFilters);
            //TODO
            return;
        } else if(searchItem.type.contains(SearchSuggestionType.PROJECT_SUGGESTION.getValue())){
            //TODO
            return;

        } else if(searchItem.type.contains(SearchSuggestionType.TEMPLATE.getValue())){
            //TODO
            return;

        } else if (SearchSuggestionType.PROJECT.getValue().equalsIgnoreCase(searchItem.type)) {
            //TODO open project page
            return;

        }

        // handle overview cases
        if(SearchSuggestionType.CITY_OVERVIEW.getValue().equalsIgnoreCase(searchItem.type)) {
            Intent cityIntent = new Intent(context, CityActivity.class);

            // check from id if entity id is not present
            if(TextUtils.isEmpty(searchItem.entityId)) {
                cityIntent.putExtra(CityActivity.CITY_ID, Long.valueOf(searchItem.id.replace("TYPEAHEAD-CITY-OVERVIEW-", "")));
            } else {
                cityIntent.putExtra(CityActivity.CITY_ID, Long.valueOf(searchItem.entityId));
            }

            context.startActivity(cityIntent);
            return;
        } else if(SearchSuggestionType.LOCALITY_OVERVIEW.getValue().equalsIgnoreCase(searchItem.type)) {
            Intent cityIntent = new Intent(context, LocalityActivity.class);

            // check from id if entity id is not present
            if(TextUtils.isEmpty(searchItem.entityId)) {
                cityIntent.putExtra(LocalityActivity.LOCALITY_ID, Long.valueOf(searchItem.id.replace("TYPEAHEAD-LOCALITY-OVERVIEW-", "")));
            } else {
                cityIntent.putExtra(LocalityActivity.LOCALITY_ID, Long.valueOf(searchItem.entityId));
            }

            context.startActivity(cityIntent);
            return;
        }

        Map<String,ApiLabel> searchResultType = MasterDataCache.getInstance().getSearchTypeMap();
        String searchField = searchResultType.get(searchItem.type).key;

        MakaanBuyerApplication.serpSelector.removeTerm("builderId");
        MakaanBuyerApplication.serpSelector.removeTerm("localityId");
        MakaanBuyerApplication.serpSelector.removeTerm("cityId");
        MakaanBuyerApplication.serpSelector.removeTerm("suburbId");

        // TODO check if we need to clear all the filters
        //MakaanBuyerApplication.serpSelector.reset();
        if(SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(searchItem.type)
                || SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(searchItem.type)) {
            // as selected item is locality
            // we need to add all the selected localities to get serp
            MakaanBuyerApplication.serpSelector.removeTerm(searchField);
            for(SearchResponseItem item : searchResponseArrayList) {
                MakaanBuyerApplication.serpSelector.term(searchField, String.valueOf(item.entityId));
            }
            MakaanBuyerApplication.serpSelector.term("cityId", String.valueOf(searchItem.cityId));
        } else {
            MakaanBuyerApplication.serpSelector.term(searchField, String.valueOf(searchItem.entityId), true);
        }

        if (SearchSuggestionType.BUILDER.getValue().equalsIgnoreCase(searchItem.type)) {
            // if current activity is SerpActivity
            if(context instanceof SerpRequestCallback) {
                ((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_BUILDER, MakaanBuyerApplication.serpSelector);
            } else {
                // because we are in some other activity, so lets start Serp Activity to handle this request
                Intent intent = new Intent(context, SerpActivity.class);
                intent.putExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_BUILDER);
                context.startActivity(intent);
            }
            return;
        } else if (SearchSuggestionType.BUILDERCITY.getValue().equalsIgnoreCase(searchItem.type)) {
            MakaanBuyerApplication.serpSelector.term("builderId", String.valueOf(searchItem.builderId), true);


        } else if (SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(searchItem.type)) {

            if(context instanceof SerpRequestCallback) {
                ((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_LOCALITY, MakaanBuyerApplication.serpSelector);
            } else {
                // because we are in some other activity, so lets start Serp Activity to handle this request
                Intent intent = new Intent(context, SerpActivity.class);
                intent.putExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_LOCALITY);
                context.startActivity(intent);
            }
            return;

        } else if (SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(searchItem.type)) {

            if(context instanceof SerpRequestCallback) {
                ((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_SUBURB, MakaanBuyerApplication.serpSelector);
            } else {
                // because we are in some other activity, so lets start Serp Activity to handle this request
                Intent intent = new Intent(context, SerpActivity.class);
                intent.putExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_SUBURB);
                context.startActivity(intent);
            }
            return;

        } else if (SearchSuggestionType.CITY.getValue().equalsIgnoreCase(searchItem.type)) {

            if(context instanceof SerpRequestCallback) {
                ((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_CITY, MakaanBuyerApplication.serpSelector);
            } else {
                // because we are in some other activity, so lets start Serp Activity to handle this request
                Intent intent = new Intent(context, SerpActivity.class);
                intent.putExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_CITY);
                context.startActivity(intent);
            }
            return;
            /*Intent cityIntent = new Intent(context, CityActivity.class);
            cityIntent.putExtra(CityActivity.CITY_ID, Long.valueOf(searchResponseItem.entityId));
            context.startActivity(cityIntent);*/

        } else if (SearchSuggestionType.GOOGLE_PLACE.getValue().equalsIgnoreCase(searchItem.type)) {
            //TODO provide google places field in serp selector
            MakaanBuyerApplication.serpSelector.removeTerm(searchField);

            if(context instanceof SerpRequestCallback) {
                ((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_GPID, MakaanBuyerApplication.serpSelector, searchItem.googlePlaceId);
            } else {
                // because we are in some other activity, so lets start Serp Activity to handle this request
                Intent intent = new Intent(context, SerpActivity.class);
                intent.putExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_GPID);
                intent.putExtra(SerpActivity.REQUEST_DATA, searchItem.googlePlaceId);
                context.startActivity(intent);
            }
            return;

        }
        ((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_UNKNOWN, MakaanBuyerApplication.serpSelector);
    }

    public static String getType(SearchResponseItem searchResponseItem){

        if (searchResponseItem == null || TextUtils.isEmpty(searchResponseItem.type)) {
            return "";
        }


        if(searchResponseItem.type.contains(SearchSuggestionType.SUGGESTION.getValue())){

            return "suggestion";
        } else if(searchResponseItem.type.contains(SearchSuggestionType.PROJECT_SUGGESTION.getValue())){
            return "suggestion";

        } else if(searchResponseItem.type.contains(SearchSuggestionType.TEMPLATE.getValue())){
            return "suggestion";

        } else if (SearchSuggestionType.PROJECT.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            return "project";

        }

        Map<String,ApiLabel> searchResultType = MasterDataCache.getInstance().getSearchTypeMap();
        return searchResultType.get(searchResponseItem.type).value;
    }


}

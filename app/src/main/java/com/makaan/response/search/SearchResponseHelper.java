package com.makaan.response.search;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.makaan.MakaanBuyerApplication;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.cache.MasterDataCache;
import com.makaan.response.master.ApiLabel;
import com.makaan.service.ListingService;

import java.util.Map;

/**
 * Created by sunil on 19/01/16.
 */
public class SearchResponseHelper {

    //WIP

    public static void resolveSearch(SearchResponseItem searchResponseItem, Context context){

        if (searchResponseItem == null) {
            return;
        }

        if(TextUtils.isEmpty(searchResponseItem.type)){
            return;
        }

        if(searchResponseItem.type.contains(SearchSuggestionType.SUGGESTION.getValue())){

            //TODO
            return;
        } else if(searchResponseItem.type.contains(SearchSuggestionType.PROJECT_SUGGESTION.getValue())){
            //TODO
            return;

        } else if(searchResponseItem.type.contains(SearchSuggestionType.TEMPLATE.getValue())){
            //TODO
            return;

        } else if (SearchSuggestionType.PROJECT.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            //TODO open project page
            return;

        }

        Map<String,ApiLabel> searchResultType = MasterDataCache.getInstance().getSearchTypeMap();
        String searchField = searchResultType.get(searchResponseItem.type).key;

        // TODO check if we need to clear all the filters
        //MakaanBuyerApplication.serpSelector.reset();
        MakaanBuyerApplication.serpSelector.term(searchField, String.valueOf(searchResponseItem.entityId), true);

        if (SearchSuggestionType.BUILDER.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            // if current activity is SerpActivity
            if(context instanceof SerpRequestCallback) {
                ((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_BUILDER, MakaanBuyerApplication.serpSelector);
            } else {
                // because we are in some other activity, so lets start Serp Activity to handle this request
                Intent intent = new Intent(context, SerpActivity.class);
                intent.putExtra(SerpActivity.TYPE, SerpActivity.TYPE_BUILDER);
                context.startActivity(intent);
            }
            return;
        } else if (SearchSuggestionType.BUILDERCITY.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            MakaanBuyerApplication.serpSelector.term("builderId", String.valueOf(searchResponseItem.builderId), true);


        } else if (SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(searchResponseItem.type)) {


        } else if (SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(searchResponseItem.type)) {


        } else if (SearchSuggestionType.CITY.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            Intent cityIntent = new Intent(context, CityActivity.class);
            cityIntent.putExtra(CityActivity.CITY_ID, Long.valueOf(searchResponseItem.entityId));
            context.startActivity(cityIntent);

        } else if (SearchSuggestionType.GOOGLE_PLACE.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            //TODO provide google places field in serp selector
        }

        new ListingService().handleSerpRequest(MakaanBuyerApplication.serpSelector);
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

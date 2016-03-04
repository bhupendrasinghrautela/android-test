package com.makaan.response.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.makaan.activity.city.CityActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.Session;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.master.ApiLabel;
import com.makaan.util.KeyUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sunil on 19/01/16.
 */
public class SearchResponseHelper {

    //WIP

    public static void resolveSearch(ArrayList<SearchResponseItem> searchResponseArrayList, Context context, boolean supportsListing){

        if (searchResponseArrayList == null || searchResponseArrayList.size() == 0) {
            return;
        }
        // get last item from the arraylist
        SearchResponseItem searchItem = searchResponseArrayList.get(searchResponseArrayList.size() - 1);

        if(TextUtils.isEmpty(searchItem.type)){
            return;
        }

        if(searchItem.type.contains(SearchSuggestionType.SUGGESTION.getValue())){
            //((SerpRequestCallback)context).serpRequest(SerpActivity.TYPE_SUGGESTION, searchItem.redirectUrlFilters);
            //TODO
            return;
        } else if(searchItem.type.contains(SearchSuggestionType.PROJECT_SUGGESTION.getValue())){
            //TODO
            return;

        } else if(searchItem.type.contains(SearchSuggestionType.TEMPLATE.getValue())){
            //TODO
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
            Intent localityIntent = new Intent(context, LocalityActivity.class);

            // check from id if entity id is not present
            if(TextUtils.isEmpty(searchItem.entityId)) {
                localityIntent.putExtra(LocalityActivity.LOCALITY_ID, Long.valueOf(searchItem.id.replace("TYPEAHEAD-LOCALITY-OVERVIEW-", "")));
            } else {
                localityIntent.putExtra(LocalityActivity.LOCALITY_ID, Long.valueOf(searchItem.entityId));
            }

            context.startActivity(localityIntent);
            return;
        } else if(SearchSuggestionType.PROJECT.getValue().equalsIgnoreCase(searchItem.type)) {
            Intent projectIntent = new Intent(context, ProjectActivity.class);
            Bundle bundle = new Bundle();

            // check from id if entity id is not present
            if(TextUtils.isEmpty(searchItem.entityId)) {
                bundle.putLong(ProjectActivity.PROJECT_ID, Long.valueOf(searchItem.id.replace("TYPEAHEAD-PROJECT-", "")));
            } else {
                bundle.putLong(ProjectActivity.PROJECT_ID, Long.valueOf(searchItem.entityId));
            }
            projectIntent.putExtras(bundle);
            context.startActivity(projectIntent);
            return;
        }

        Map<String,ApiLabel> searchResultType = MasterDataCache.getInstance().getSearchTypeMap();


        if(SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(searchItem.type)
                || SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(searchItem.type)) {
            SerpRequest request = new SerpRequest(SerpActivity.TYPE_SEARCH);
            // as selected item is locality/suburb
            // we need to add all the selected localities/suburbs to get serp
            for(SearchResponseItem item : searchResponseArrayList) {
                if(KeyUtil.LOCALITY_ID.equalsIgnoreCase(searchResultType.get(item.type).key)) {
                    request.setLocalityId(Long.valueOf(item.entityId));
                    if(item.cityId > 0) {
                        request.setCityId(item.cityId);
                    }
                } else if(KeyUtil.SUBURB_ID.equalsIgnoreCase(searchResultType.get(item.type).key)) {
                    request.setSuburbId(Long.valueOf(item.entityId));
                    if(item.cityId > 0) {
                        request.setCityId(item.cityId);
                    }
                }
                request.setSearch(item);
            }
            if(searchResponseArrayList.size() == 1) {
                request.setTitle(searchResponseArrayList.get(0).displayText);
            } else {
                request.setTitle(String.format("%s +%d", searchResponseArrayList.get(0).displayText, searchResponseArrayList.size() - 1));
            }
            // TODO cityId is not coming in search results
//            MakaanBuyerApplication.mSerpSelector.term("cityId", String.valueOf(searchItem.cityId));
            request.launchSerp(context);
            return;
        } else if (SearchSuggestionType.BUILDER.getValue().equalsIgnoreCase(searchItem.type)) {
            SerpRequest request = new SerpRequest(SerpActivity.TYPE_BUILDER);
            request.setBuilderId(Long.valueOf(searchItem.entityId));
            request.setTitle(searchItem.displayText);
            request.setSearch(searchItem);
            request.launchSerp(context);
            return;
        } else if (SearchSuggestionType.BUILDERCITY.getValue().equalsIgnoreCase(searchItem.type)) {
            SerpRequest request = new SerpRequest(SerpActivity.TYPE_BUILDER_CITY);
            request.setBuilderId(Long.valueOf(searchItem.builderId));
            request.setCityId(Long.valueOf(searchItem.entityId));
            request.setTitle(searchItem.displayText);
            request.setSearch(searchItem);
            request.launchSerp(context);
            return;
        } else if (SearchSuggestionType.CITY.getValue().equalsIgnoreCase(searchItem.type)) {
            SerpRequest request = new SerpRequest(SerpActivity.TYPE_CITY);
            request.setCityId(Long.valueOf(searchItem.entityId));
            request.setTitle(searchItem.displayText);
            request.setSearch(searchItem);
            request.launchSerp(context);
            return;

        } else if (SearchSuggestionType.GOOGLE_PLACE.getValue().equalsIgnoreCase(searchItem.type)) {
            SerpRequest request = new SerpRequest(SerpActivity.TYPE_GPID);
            request.setGpId(searchItem.googlePlaceId);
            request.setTitle(searchResponseArrayList.get(0).displayText);
            request.setSearch(searchItem);
            request.launchSerp(context);
            return;

        } else if(SearchSuggestionType.NEARBY_PROPERTIES.getValue().equalsIgnoreCase(searchItem.type)) {
            SerpRequest request = new SerpRequest(SerpActivity.TYPE_NEARBY);
            if(Session.phoneLocation != null) {
                request.setLatitude(Session.phoneLocation.getLatitude());
                request.setLongitude(Session.phoneLocation.getLongitude());
            } else if(Session.apiLocation != null) {
                request.setLatitude(Session.apiLocation.centerLatitude);
                request.setLongitude(Session.apiLocation.centerLongitude);
            }
            request.setSort(SerpRequest.Sort.GEO_ASC);
            request.setTitle(searchItem.displayText);
            request.launchSerp(context);
            return;

        }
        SerpRequest request = new SerpRequest(SerpActivity.TYPE_UNKNOWN);
        request.launchSerp(context);
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

        } else if (SearchSuggestionType.NEARBY_PROPERTIES.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            return "";

        } else if (SearchSuggestionType.HEADER_TEXT.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            return "header";

        } else if (SearchSuggestionType.ERROR.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            return "error";

        } else if (SearchSuggestionType.SELLER.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            return "seller";

        }

        Map<String,ApiLabel> searchResultType = MasterDataCache.getInstance().getSearchTypeMap();
        return searchResultType.get(searchResponseItem.type).value;
    }


}

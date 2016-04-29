package com.makaan.activity.deeplink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.makaan.activity.HomeActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.constants.RequestConstants;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.response.seo.SeoUrlResponse;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sunil on 10/03/16.
 */
public class DeepLinkHelper {

    public enum TemplateId{
        MAKAAN_HOME_PAGE, MAKAAN_CITY_OVERVIEW,
        MAKAAN_SUBURB_OVERVIEW, MAKAAN_LOCALITY_OVERVIEW,
        MAKAAN_PROJECT_OVERVIEW,MAKAAN_PROPERTY_BUY,
        MAKAAN_PROPERTY_RENT,MAKAAN_BUILDER,
        MAKAAN_BUILDER_BHK,MAKAAN_CITY_BUILDER,MAKAAN_COMPANY_URL
    }


    /**
     * Returns appropriate intent based on SEO url api response
     * @param context
     * @param seoUrlResponse
     * */
    public static boolean resolveDeepLink(Context context, SeoUrlResponse seoUrlResponse){

        if(seoUrlResponse ==null || seoUrlResponse.data==null ||
                seoUrlResponse.data.urlDetail==null){
            return false;
        }

        SeoUrlResponse.UrlDetail urlDetail = seoUrlResponse.data.urlDetail;

        if(TextUtils.isEmpty(urlDetail.templateId)){
            return false;
        }

        if(TemplateId.MAKAAN_CITY_OVERVIEW.name().equalsIgnoreCase(urlDetail.templateId)){
            Intent intent = new Intent(context, OverviewActivity.class);

            Bundle bundle = new Bundle();
            bundle.putLong(OverviewActivity.ID, urlDetail.cityId);
            bundle.putInt(OverviewActivity.TYPE, OverviewItemType.CITY.ordinal());

            intent.putExtras(bundle);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_LOCALITY_OVERVIEW.name().equalsIgnoreCase(urlDetail.templateId)||
                TemplateId.MAKAAN_SUBURB_OVERVIEW.name().equalsIgnoreCase(urlDetail.templateId)){
            Intent intent = new Intent(context, OverviewActivity.class);

            Bundle bundle = new Bundle();
            intent.putExtra(OverviewActivity.ID, urlDetail.localityId);
            bundle.putInt(OverviewActivity.TYPE, OverviewItemType.LOCALITY.ordinal());

            intent.putExtras(bundle);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_PROJECT_OVERVIEW.name().equalsIgnoreCase(urlDetail.templateId)){
            Intent intent = new Intent(context, OverviewActivity.class);

            Bundle bundle = new Bundle();
            intent.putExtra(OverviewActivity.ID, urlDetail.projectId);
            bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROJECT.ordinal());

            intent.putExtras(bundle);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_PROPERTY_BUY.name().equalsIgnoreCase(urlDetail.templateId)||
                TemplateId.MAKAAN_PROPERTY_RENT.name().equalsIgnoreCase(urlDetail.templateId)){
            Intent intent = new Intent(context, OverviewActivity.class);

            Bundle bundle = new Bundle();
            intent.putExtra(OverviewActivity.ID, urlDetail.listingId);
            bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROPERTY.ordinal());

            intent.putExtras(bundle);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_HOME_PAGE.name().equalsIgnoreCase(urlDetail.templateId)){
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
            return true;

        }else if(isSerpPage(urlDetail)){

            String serpFilterUrl = getFilters(urlDetail.listingFilter);
            if(TextUtils.isEmpty(serpFilterUrl)){
                return false;
            }

            SerpRequest request = new SerpRequest(SerpActivity.TYPE_NOTIFICATION);
            request.launchSerp(context, serpFilterUrl);
            return true;

        }

        return false;
    }

    private static boolean isSerpPage(SeoUrlResponse.UrlDetail urlDetail){

        if(null==urlDetail || TextUtils.isEmpty(urlDetail.templateId) || TextUtils.isEmpty(urlDetail.listingFilter)){
            return false;
        }else {
            return true;
        }

    }

    private static String getFilters(String filter){
        if(TextUtils.isEmpty(filter)){
            return null;
        }

        if(filter.contains(RequestConstants.FILTERS)){
            return filter;
        }

        try {
            JSONObject object = new JSONObject(filter);
            JSONObject filterObject = new JSONObject();
            filterObject.put(RequestConstants.FILTERS, object);
            return filterObject.toString();
        } catch (JSONException e) {
            Crashlytics.logException(e);
            return null;
        }

    }
}

package com.makaan.activity.deeplink;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.makaan.activity.HomeActivity;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.seo.SeoUrlResponse;


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

        if(TextUtils.isEmpty(seoUrlResponse.data.templateId)){
            return false;
        }

        if(TemplateId.MAKAAN_CITY_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.data.templateId)){
            Intent intent = new Intent(context, CityActivity.class);
            intent.putExtra(CityActivity.CITY_ID, seoUrlResponse.data.urlDetail.cityId);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_LOCALITY_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.data.templateId)||
                TemplateId.MAKAAN_SUBURB_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.data.templateId)){
            Intent intent = new Intent(context, CityActivity.class);
            intent.putExtra(LocalityActivity.LOCALITY_ID, seoUrlResponse.data.urlDetail.localityId);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_PROJECT_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.data.templateId)){
            Intent intent = new Intent(context, CityActivity.class);
            intent.putExtra(ProjectActivity.PROJECT_ID, seoUrlResponse.data.urlDetail.projectId);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_PROPERTY_BUY.name().equalsIgnoreCase(seoUrlResponse.data.templateId)||
                TemplateId.MAKAAN_PROPERTY_RENT.name().equalsIgnoreCase(seoUrlResponse.data.templateId)){
            Intent intent = new Intent(context, CityActivity.class);
            intent.putExtra(PropertyActivity.LISTING_ID, seoUrlResponse.data.urlDetail.listingId);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_HOME_PAGE.name().equalsIgnoreCase(seoUrlResponse.data.templateId)){
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
            return true;

        }else if(isSerpPage(seoUrlResponse)){
            String serpFilterUrl = seoUrlResponse.data.urlDetail.listingFilter;
            SerpRequest request = new SerpRequest(SerpActivity.TYPE_NOTIFICATION);
            request.launchSerp(context, serpFilterUrl);
            return true;

        }

        return false;
    }

    private static boolean isSerpPage(SeoUrlResponse seoUrlResponse){

        if(
                ((seoUrlResponse.data.templateId.contains("MAKAAN") &&
                seoUrlResponse.data.templateId.contains("RENT") &&
                seoUrlResponse.data.templateId.contains("BUY"))

                || (TemplateId.MAKAAN_BUILDER.name().equalsIgnoreCase(seoUrlResponse.data.templateId))

                || (TemplateId.MAKAAN_BUILDER_BHK.name().equalsIgnoreCase(seoUrlResponse.data.templateId))

                || (TemplateId.MAKAAN_CITY_BUILDER.name().equalsIgnoreCase(seoUrlResponse.data.templateId))

                || (TemplateId.MAKAAN_COMPANY_URL.name().equalsIgnoreCase(seoUrlResponse.data.templateId)))

              && !TextUtils.isEmpty(seoUrlResponse.data.urlDetail.listingFilter)

        ){
            return true;
        }

        return false;
    }
}

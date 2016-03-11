package com.makaan.activity.deeplink;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.makaan.activity.listing.SerpActivity;
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

        if(seoUrlResponse ==null || seoUrlResponse.getData()==null ||
                seoUrlResponse.getData().getUrlDetail()==null){
            return false;
        }

        if(TextUtils.isEmpty(seoUrlResponse.getData().getTemplateId())){
            return false;
        }

        if(TemplateId.MAKAAN_CITY_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId())){

        }else if(TemplateId.MAKAAN_LOCALITY_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId())||
                TemplateId.MAKAAN_SUBURB_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId())){

        }else if(TemplateId.MAKAAN_PROJECT_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId())){

        }else if(TemplateId.MAKAAN_PROPERTY_BUY.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId())||
                TemplateId.MAKAAN_PROPERTY_RENT.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId())){

        }else if(TemplateId.MAKAAN_HOME_PAGE.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId())){

        }else if(isSerpPage(seoUrlResponse)){


            String serpFilterUrl = seoUrlResponse.getData().getUrlDetail().getListingFilter();

            SerpRequest request = new SerpRequest(SerpActivity.TYPE_NOTIFICATION);
            request.launchSerp(context, serpFilterUrl);

            return true;

        }

        return false;
    }

    private static boolean isSerpPage(SeoUrlResponse seoUrlResponse){

        if(
                ((seoUrlResponse.getData().getTemplateId().contains("MAKAAN") &&
                seoUrlResponse.getData().getTemplateId().contains("RENT") &&
                seoUrlResponse.getData().getTemplateId().contains("BUY"))

                || (TemplateId.MAKAAN_BUILDER.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId()))

                || (TemplateId.MAKAAN_BUILDER_BHK.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId()))

                || (TemplateId.MAKAAN_CITY_BUILDER.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId()))

                || (TemplateId.MAKAAN_COMPANY_URL.name().equalsIgnoreCase(seoUrlResponse.getData().getTemplateId())))

              && !TextUtils.isEmpty(seoUrlResponse.getData().getUrlDetail().getListingFilter())

        ){
            return true;
        }

        return false;
    }
}

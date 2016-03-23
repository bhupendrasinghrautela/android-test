package com.makaan.activity.deeplink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.makaan.activity.HomeActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.overview.OverviewItemType;
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
            Intent intent = new Intent(context, OverviewActivity.class);

            Bundle bundle = new Bundle();
            bundle.putLong(OverviewActivity.ID, seoUrlResponse.data.urlDetail.cityId);
            bundle.putInt(OverviewActivity.TYPE, OverviewItemType.CITY.ordinal());

            intent.putExtras(bundle);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_LOCALITY_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.data.templateId)||
                TemplateId.MAKAAN_SUBURB_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.data.templateId)){
            Intent intent = new Intent(context, OverviewActivity.class);

            Bundle bundle = new Bundle();
            intent.putExtra(OverviewActivity.ID, seoUrlResponse.data.urlDetail.localityId);
            bundle.putInt(OverviewActivity.TYPE, OverviewItemType.LOCALITY.ordinal());

            intent.putExtras(bundle);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_PROJECT_OVERVIEW.name().equalsIgnoreCase(seoUrlResponse.data.templateId)){
            Intent intent = new Intent(context, OverviewActivity.class);

            Bundle bundle = new Bundle();
            intent.putExtra(OverviewActivity.ID, seoUrlResponse.data.urlDetail.projectId);
            bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROJECT.ordinal());

            intent.putExtras(bundle);
            context.startActivity(intent);
            return true;

        }else if(TemplateId.MAKAAN_PROPERTY_BUY.name().equalsIgnoreCase(seoUrlResponse.data.templateId)||
                TemplateId.MAKAAN_PROPERTY_RENT.name().equalsIgnoreCase(seoUrlResponse.data.templateId)){
            Intent intent = new Intent(context, OverviewActivity.class);

            Bundle bundle = new Bundle();
            intent.putExtra(OverviewActivity.ID, seoUrlResponse.data.urlDetail.listingId);
            bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROPERTY.ordinal());

            intent.putExtras(bundle);
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

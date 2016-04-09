package com.makaan.activity.lead;

import android.os.Bundle;
import android.text.TextUtils;

import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.pyr.PyrOtpVerification;
import com.makaan.activity.shortlist.ShortListFavoriteAdapter;
import com.makaan.activity.shortlist.ShortListRecentFragment;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.fragment.pyr.ThankYouScreenFragment;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.pyr.PyrData;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by proptiger on 7/1/16.
 */
public class LeadFormPresenter {
    private static LeadFormPresenter mLeadFormPresenter;
    private LeadFormReplaceFragment mLeadFormReplaceFragment;
    private LeadCallNowFragment mLeadCallNowFragment;
    private MultipleLeadFormFragment mMultipleLeadFormFragment;
    private LeadInstantCallBackFragment mLeadInstantCallBackFragment;
    private LeadLaterCallBackFragment mLeadLaterCallBackFragment;
    private ThankYouScreenFragment mThankYouFragment;
    private PyrOtpVerification mPyrOtpVerification;
    private String temporaryPhoneNo;
    public static String MAKAAN_ASSIST_VALUE = "makaan_assist";
    public static String NO_SELLERS_FRAGMENT = "no_seller";

    String name;
    String id;
    String phone;
    String score;
    String source;
    int cityId;
    boolean assist;
    String area,bhkAndUnitType,locality;
    Long projectOrListingId;
    Long multipleSellerIds[];
    PyrRequest pyrRequest;
    String sellerImageUrl;
    String cityName,salesType;
    String projectName;
    String serpSubCategory;
    Long localityId;
    Long projectId;
    Long propertyId;
    Long userId;

    public String getSerpSubCategory() {
        return serpSubCategory;
    }

    public void setSerpSubCategory(String serpSubCategory) {
        this.serpSubCategory = serpSubCategory;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getSalesType() {
        return salesType;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }
    public Long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Long localityId) {
        this.localityId = localityId;
    }

    public String getSellerImageUrl() {
        return sellerImageUrl;
    }

    public void setSellerImageUrl(String sellerImageUrl) {
        this.sellerImageUrl = sellerImageUrl;
    }

    public PyrRequest getPyrRequest() {
        return pyrRequest;
    }

    public void setPyrRequest(PyrRequest pyrRequest) {
        this.pyrRequest = pyrRequest;
    }

    public Long getProjectOrListingId() {
        return projectOrListingId;
    }

    public void setProjectOrListingId(Long projectOrListingId) {
        this.projectOrListingId = projectOrListingId;
    }

    public boolean isAssist() {
        return assist;
    }

    public void setAssist(boolean assist) {
        this.assist = assist;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBhkAndUnitType() {
        return bhkAndUnitType;
    }

    public void setBhkAndUnitType(String bhkAndUnitType) {
        this.bhkAndUnitType = bhkAndUnitType;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public LeadFormPresenter() {

    }

    public Long[] getMultipleSellerIds() {
        return multipleSellerIds;
    }

    public Long[] setMultipleSellerIds(ArrayList<Integer> multipleSellerIdsArray) {
        Long arr[]=new Long[multipleSellerIdsArray.size()];
        int i=0;
        for(Integer value:multipleSellerIdsArray){
            arr[i]=Long.valueOf(value);
            i++;
        }
        multipleSellerIds=arr;
        return arr;
    }


    public static LeadFormPresenter getLeadFormPresenter() {
        if (mLeadFormPresenter == null) {
            mLeadFormPresenter = new LeadFormPresenter();
        }
        return mLeadFormPresenter;
    }

    public void setReplaceFragment(LeadFormReplaceFragment replaceFragment) {
        mLeadFormReplaceFragment = replaceFragment;
    }


    public void showLeadCallNowFragment() {
        mLeadCallNowFragment = new LeadCallNowFragment();
        mLeadFormReplaceFragment.replaceFragment(mLeadCallNowFragment, true);
    }

    public void showLeadInstantCallBackFragment() {
        mLeadInstantCallBackFragment = new LeadInstantCallBackFragment();
        mLeadFormReplaceFragment.replaceFragment(mLeadInstantCallBackFragment, true);
    }

    public void showLeadLaterCallBAckFragment() {
        mLeadLaterCallBackFragment = new LeadLaterCallBackFragment();
        mLeadFormReplaceFragment.replaceFragment(mLeadLaterCallBackFragment, true);
    }

    public void showMultipleLeadsFragment() {
        mMultipleLeadFormFragment = new MultipleLeadFormFragment()   ;
        mLeadFormReplaceFragment.replaceFragment(mMultipleLeadFormFragment, true);
    }

    public PyrOtpVerification showPyrOtpFragment(PyrData data) {
        mPyrOtpVerification = new PyrOtpVerification();
        mPyrOtpVerification.setData(data);
        mLeadFormReplaceFragment.replaceFragment(mPyrOtpVerification, true);
        return mPyrOtpVerification;
    }

    public void showThankYouScreenFragment(boolean makaanAssist, boolean throughNoSellers, int popcount) {
        mThankYouFragment = new ThankYouScreenFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(MAKAAN_ASSIST_VALUE, makaanAssist);
        bundle.putBoolean(NO_SELLERS_FRAGMENT, throughNoSellers);
        mThankYouFragment.setArguments(bundle);
        mLeadFormReplaceFragment.popFromBackstack(popcount + 1);
        mLeadFormReplaceFragment.replaceFragment(mThankYouFragment, false);
    }

    public void getCallBackSuccess(){
        if(mLeadLaterCallBackFragment!=null){
            mLeadLaterCallBackFragment.successfulResponse();
        }
    }

    public void getCallBackFailure(){
        if(mLeadLaterCallBackFragment!=null){
            mLeadLaterCallBackFragment.errorInResponse();
        }
    }

    public void instantCallSuccess(){
        if(mLeadInstantCallBackFragment!=null){
            mLeadInstantCallBackFragment.successfulInstantResponse();
        }
    }

    public void instantCallFailure(){
        if(mLeadInstantCallBackFragment!=null){
            mLeadInstantCallBackFragment.errorInInstantResponse();
        }
    }

    public String getTemporaryPhoneNo() {
        return temporaryPhoneNo;
    }

    public void setTemporaryPhoneNo(String temporaryPhoneNo) {
        this.temporaryPhoneNo = temporaryPhoneNo;
    }

    public String getSubmitStoredLabel(String source){
        String string="";
        if(!TextUtils.isEmpty(source)) {
            if (source.equalsIgnoreCase(SerpActivity.class.getName())) {
                return String.format("%s_%s_%s", projectOrListingId,cityId,id);
            }
            else if (source.equalsIgnoreCase(ProjectFragment.class.getName())) {
                if(multipleSellerIds!=null && multipleSellerIds.length>0){
                    return String.format("%s_%s_%s", projectId, cityId, Arrays.toString(multipleSellerIds));
                }
                return String.format("%s_%s_%s", projectId, cityId, id);
            }
            else if (source.equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
                if(multipleSellerIds!=null && multipleSellerIds.length>0){
                    return String.format("%s_%s_%s", projectOrListingId, cityId, Arrays.toString(multipleSellerIds));
                }
                return String.format("%s_%s_%s", propertyId, cityId, id);
            }
            else if (source.equalsIgnoreCase(ShortListFavoriteAdapter.class.getName())) {
                return String.format("%s_%s_%s", projectOrListingId, cityId, id);
            }
            else if (source.equalsIgnoreCase(ShortListRecentFragment.class.getName())){
                return String.format("%s_%s_%s", projectOrListingId, cityId, id);
            }
        }
        return string;
    }

}

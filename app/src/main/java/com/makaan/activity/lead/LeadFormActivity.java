package com.makaan.activity.lead;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.shortlist.ShortListFavoriteAdapter;
import com.makaan.activity.shortlist.ShortListRecentFragment;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.fragment.MakaanMessageDialogFragment;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.network.VolleyErrorParser;
import com.makaan.response.leadForm.InstantCallbackResponse;
import com.makaan.response.pyr.PyrPostResponse;
import com.makaan.util.KeyUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.OnClick;

/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadFormActivity extends MakaanFragmentActivity implements LeadFormReplaceFragment {
    public static final int LEAD_DROP_REQUEST = 3001;
    private FragmentTransaction mFragmentTransaction;
    private LeadFormPresenter mLeadFormPresenter;
    private long mListingId = -1;
    public String source;
    private boolean multipleSellers=false;
    private static final int SINGLE_SELLER=1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lead_form;
    }

    /**
     * back button handling for toolbar back click
     */
    @OnClick(R.id.btn_back_toolbar)
    public void onToolbarBack(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name=this.getIntent().getExtras().getString(KeyUtil.NAME_LEAD_FORM);
        String id=this.getIntent().getExtras().getString(KeyUtil.SINGLE_SELLER_ID);
        ArrayList<Integer> multipleSellerids=this.getIntent().getExtras().getIntegerArrayList(KeyUtil.MULTIPLE_SELLER_IDS);
        String score=this.getIntent().getExtras().getString(KeyUtil.SCORE_LEAD_FORM);
        String phone=this.getIntent().getExtras().getString(KeyUtil.PHONE_LEAD_FORM);
        String area=this.getIntent().getExtras().getString(KeyUtil.AREA_LEAD_FORM);
        String bhk=this.getIntent().getExtras().getString(KeyUtil.BHK_UNIT_TYPE);
        String locality=this.getIntent().getExtras().getString(KeyUtil.LOCALITY_LEAD_FORM);
        String project=this.getIntent().getExtras().getString(KeyUtil.PROJECT_LEAD_FORM);
        String builder=this.getIntent().getExtras().getString(KeyUtil.BUILDER_LEAD_FORM);
        boolean assist=this.getIntent().getExtras().getBoolean(KeyUtil.ASSIST_LEAD_FORM);
        source=this.getIntent().getExtras().getString(KeyUtil.SOURCE_LEAD_FORM);
        int cityId= (int) this.getIntent().getExtras().getLong(KeyUtil.CITY_ID_LEAD_FORM);
        Long projectOrListingId=this.getIntent().getExtras().getLong(KeyUtil.LISTING_ID_LEAD_FORM);
        Long localityId=this.getIntent().getExtras().getLong(KeyUtil.LOCALITY_ID_LEAD_FORM);
        Long projectId=this.getIntent().getExtras().getLong(KeyUtil.PROJECT_ID_LEAD_FORM);
        Long propertyId=this.getIntent().getExtras().getLong(KeyUtil.PROPERTY_Id_LEAD_FORM);
        Long userId=this.getIntent().getExtras().getLong(KeyUtil.USER_ID);
        String sellerImgUrl=this.getIntent().getExtras().getString(KeyUtil.SELLER_IMAGE_URL_LEAD_FORM);
        String cityName=this.getIntent().getExtras().getString(KeyUtil.CITY_NAME_LEAD_FORM);
        String salesType=this.getIntent().getExtras().getString(KeyUtil.SALE_TYPE_LEAD_FORM);
        String projectName=this.getIntent().getExtras().getString(KeyUtil.PROJECT_NAME_LEAD_FORM);
        String serpSubCategory=this.getIntent().getExtras().getString(KeyUtil.SERP_SUB_CATEGORY);
        mLeadFormPresenter = LeadFormPresenter.getLeadFormPresenter();
        mLeadFormPresenter.setId(id);
        mLeadFormPresenter.setName(name);
        mLeadFormPresenter.setPhone(phone);
        mLeadFormPresenter.setScore(score);
        mLeadFormPresenter.setReplaceFragment(this);
        mLeadFormPresenter.setLocalityId(localityId);
        mLeadFormPresenter.setProjectId(projectId);
        mLeadFormPresenter.setPropertyId(propertyId);
        mLeadFormPresenter.setUserId(userId);
        mLeadFormPresenter.setSerpSubCategory(serpSubCategory);

        if(null!=projectName){
            mLeadFormPresenter.setProjectName(projectName);
        }

        if(null!=salesType){
            mLeadFormPresenter.setSalesType(salesType);
        }

        if(cityName!=null){
            mLeadFormPresenter.setCityName(cityName);
        }

        if(null!=sellerImgUrl){
            mLeadFormPresenter.setSellerImageUrl(sellerImgUrl);
        }
        else {
            mLeadFormPresenter.setSellerImageUrl(null);
        }

        if(null!=area){
            mLeadFormPresenter.setArea(area);
        }

        if(null!=bhk){
            mLeadFormPresenter.setBhkAndUnitType(bhk);
        }

        if(!TextUtils.isEmpty(project)) {
            if(!TextUtils.isEmpty(builder)) {
                mLeadFormPresenter.setLocality(String.format("%s %s", builder, project).toLowerCase());
            } else {
                mLeadFormPresenter.setLocality(project.toLowerCase());
            }
        } else if(!TextUtils.isEmpty(builder)) {
            mLeadFormPresenter.setLocality(builder.toLowerCase());
        }

        if(assist){
            mLeadFormPresenter.setAssist(assist);
        }

        if(null!=multipleSellerids && multipleSellerids.size()>0){
            mLeadFormPresenter.setMultipleSellerIds(multipleSellerids);
            multipleSellers=true;
        }

        mLeadFormPresenter.setSource(source);

        Bundle bundle=this.getIntent().getExtras();
        if (bundle != null && bundle.getString("source")!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
            if(bundle.get("listingId")!=null) {
                mLeadFormPresenter.setProjectOrListingId((Long) bundle.get("listingId"));
            }
            if(bundle.get("cityId")!=null) {
                Long value=(Long)bundle.get("cityId");
                mLeadFormPresenter.setCityId(value.intValue());
            }
        }else {
            mLeadFormPresenter.setCityId(cityId);
            mLeadFormPresenter.setProjectOrListingId(projectOrListingId);
        }
            try {
            mListingId = this.getIntent().getExtras().getLong(KeyUtil.LISTING_ID);
        }catch (Exception e){}

        if(null!=multipleSellerids && multipleSellerids.size()>0) {
            mLeadFormPresenter.showMultipleLeadsFragment();
        }
        else {
            mLeadFormPresenter.showLeadCallNowFragment();
            multipleSellers=false;
        }
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    @Override
    public String getScreenName() {
        return ScreenNameConstants.SCREEN_NAME_LEAD_FORM;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean shouldAddToBackStack) {
        if(source!=null) {
            Bundle bundle = new Bundle();
            bundle.putString("source", source);
            fragment.setArguments(bundle);
        }
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.leadform_fragment_holder, fragment, fragment.getClass().getName());
        if(shouldAddToBackStack) {
            mFragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        mFragmentTransaction.commit();
    }

    @Override
    public void popFromBackstack(int popCount) {
        if (getSupportFragmentManager().getBackStackEntryCount() >=1 ){
            for(int i=0;i<popCount;i++) {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1 ){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            super.onBackPressed();
        }
    }


    @Subscribe
    public void pyrResponse(PyrPostResponse pyrPostResponse){
        if(isActivityDead()){
            return;
        }

        if(null!=pyrPostResponse.getError()){
            /*Toast.makeText(this, VolleyErrorParser.getMessage(pyrPostResponse.getError()),Toast.LENGTH_SHORT).show();*/
            if(!isFinishing()) {
                mLeadFormPresenter.getCallBackFailure();
                MakaanMessageDialogFragment.showMessage(getFragmentManager(),
                        VolleyErrorParser.getMessage(pyrPostResponse.getError()), "ok");
            }
        }
        else if(pyrPostResponse.getStatusCode().equals("2XX")) {
            mLeadFormPresenter.getCallBackSuccess();
            if(source!=null && source.equalsIgnoreCase(SerpActivity.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, mLeadFormPresenter.getSerpSubCategory());
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                if(multipleSellers){
                    properties.put(MakaanEventPayload.VALUE, mLeadFormPresenter.getMultipleSellerIds().length);
                }else {
                    properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                }
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredGetCallBack);
            }
            else if(source!=null && source.equalsIgnoreCase(ProjectFragment.class.getName())){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.project);
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                if(multipleSellers){
                    properties.put(MakaanEventPayload.VALUE, mLeadFormPresenter.getMultipleSellerIds().length);
                }else {
                    properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                }
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredGetCallBack);
            }
            else if(source!=null && source.equalsIgnoreCase(PropertyDetailFragment.class.getName())){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.PropertyInCaps);
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                if(multipleSellers){
                    properties.put(MakaanEventPayload.VALUE, mLeadFormPresenter.getMultipleSellerIds().length);
                }else {
                    properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                }
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredGetCallBack);
            }
            else if(source!=null && source.equalsIgnoreCase(ShortListFavoriteAdapter.class.getName())){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCaps);
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                if(multipleSellers){
                    properties.put(MakaanEventPayload.VALUE, mLeadFormPresenter.getMultipleSellerIds().length);
                }else {
                    properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                }
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredGetCallBack);
            }
            else if(source!=null && source.equalsIgnoreCase(ShortListRecentFragment.class.getName())){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCaps);
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                if(multipleSellers){
                    properties.put(MakaanEventPayload.VALUE, mLeadFormPresenter.getMultipleSellerIds().length);
                }else {
                    properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                }
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredGetCallBack);
            }

            if(multipleSellers && !pyrPostResponse.getData().isOtpVerified()) {
                mLeadFormPresenter.showPyrOtpFragment(pyrPostResponse.getData());
                //fragment.setData(pyrPostResponse.getData());
            }else {
                mLeadFormPresenter.showThankYouScreenFragment(false, false, 2);
            }
            setResult(RESULT_OK,new Intent().putExtra(KeyUtil.LISTING_ID, mListingId));
        }
    }

    @Subscribe
    public void instantResponse(InstantCallbackResponse instantCallbackResponse) {
        if(isActivityDead()){
            return;
        }

        if(null!=instantCallbackResponse.getError()){
            /*Toast.makeText(this, VolleyErrorParser.getMessage(instantCallbackResponse.getError()),Toast.LENGTH_SHORT).show();*/
            if(!isFinishing()) {
                mLeadFormPresenter.instantCallFailure();
                MakaanMessageDialogFragment.showMessage(getFragmentManager(),
                        VolleyErrorParser.getMessage(instantCallbackResponse.getError()), "ok");
            }
        }
        else if(instantCallbackResponse.getStatusCode().equals("2XX")){
            mLeadFormPresenter.instantCallSuccess();
            if(source!=null && source.equalsIgnoreCase(SerpActivity.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, mLeadFormPresenter.getSerpSubCategory());
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredConnectNow);
            }
            else if(source!=null && source.equalsIgnoreCase(ProjectFragment.class.getName())){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.project);
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredConnectNow);
            }
            else if(source!=null && source.equalsIgnoreCase(PropertyDetailFragment.class.getName())){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.PropertyInCaps);
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredConnectNow);
            }
            else if(source!=null && source.equalsIgnoreCase(ShortListFavoriteAdapter.class.getName())){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCaps);
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredConnectNow);
            }
            else if(source!=null && source.equalsIgnoreCase(ShortListRecentFragment.class.getName())){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCaps);
                properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(source));
                properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.leadStoredConnectNow);
            }


            mLeadFormPresenter.showThankYouScreenFragment(false, false,2);
            setResult(RESULT_OK, new Intent().putExtra(KeyUtil.LISTING_ID, mListingId));
        }
    }

}

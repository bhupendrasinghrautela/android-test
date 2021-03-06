package com.makaan.activity.lead;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.shortlist.ShortListFavoriteAdapter;
import com.makaan.activity.shortlist.ShortListRecentFragment;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.MakaanMessageDialogFragment;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.notification.GcmPreferences;
import com.makaan.request.pyr.PyrEnquiryType;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.country.CountryCodeResponse;
import com.makaan.response.user.UserResponse;
import com.makaan.response.user.UserResponse.UserData;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PyrService;
import com.makaan.util.CommonUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.JsonBuilder;
import com.makaan.util.JsonParser;
import com.makaan.util.PermissionManager;
import com.makaan.util.StringUtil;
import com.makaan.util.ValidationUtil;
import com.segment.analytics.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadLaterCallBackFragment extends MakaanBaseFragment implements View.OnFocusChangeListener{

    @Bind(R.id.select_country_spinner)
    Spinner mCountrySpinner;
    @Bind(R.id.leadform_country_code_textview)
    TextView mCodeTextView;
    @Bind(R.id.leadform_name)
    EditText mName;
    @Bind(R.id.leadform_email)
    EditText mEmail;
    @Bind(R.id.leadform_mobileno_edittext)
    EditText mNumber;
    LeadFormPresenter mLeadFormPresenter;
    @Bind(R.id.tv_seller_name)
    TextView mTextViewSellerName;
    @Bind(R.id.seller_ratingbar)
    RatingBar mRatingBarSeller;
    @Bind(R.id.iv_seller_name)
    TextView mSellerNameProfileText;
    @Bind(R.id.iv_seller_image_lead_later_Call_back )
    de.hdodenhof.circleimageview.CircleImageView mSellerImage;
    @Bind(R.id.btn_call_later)
    Button mCallLaterButton;
    @Bind(R.id.lead_from_user_email)TextInputLayout mUserEmailTextInputLayout;
    @Bind(R.id.lead_from_user_mobile)TextInputLayout mUserMobileNumberTextInputLayout;
    @Bind(R.id.lead_from_username)TextInputLayout mUserNameTextInputLayout;
    @Bind(R.id.lead_later_scroll_view)ScrollView mScrollView;
    private Integer mCountryId;
    private ArrayAdapter<String> mCountryAdapter;
    private List<String> mCountryNames;
    private List<CountryCodeResponse.CountryCodeData> mCountries;
    private static final int MOSTLY_USED_COUNTRIES = 7;
    private boolean mobileFlag=true;
    private boolean nameFlag=true;
    private boolean emailFlag=true;
    private boolean defaultCountry=false;
    private String previousName="";
    private String previousEmail="";
    private String previousNumber="";
    private static final int SINGLE_SELLER=1;

    @Override
    protected int getContentViewId() {
        return R.layout.layout_lead_later_callback;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLeadFormPresenter= LeadFormPresenter.getLeadFormPresenter();
        if(!TextUtils.isEmpty((mLeadFormPresenter.getName()))){
            mTextViewSellerName.setText(mLeadFormPresenter.getName().toLowerCase());
        }
        else {
            mTextViewSellerName.setText("");
        }
        if(!TextUtils.isEmpty(mLeadFormPresenter.getScore())) {
            mRatingBarSeller.setVisibility(View.VISIBLE);
            mRatingBarSeller.setRating(Float.valueOf(mLeadFormPresenter.getScore()));
        } else {
            mRatingBarSeller.setVisibility(View.INVISIBLE);
        }
        mobileFlag=false;
        nameFlag=false;
        emailFlag=false;
        defaultCountry=false;
        initializeCountrySpinner();

        //User data prefill
        try{
            UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
            if(userResponse!=null && userResponse.getData()!=null && userResponse.getData().firstName!=null) {
                mName.setText(userResponse.getData().firstName);
            }
            if(userResponse!=null && userResponse.getData()!=null && userResponse.getData().email!=null) {
                mEmail.setText(userResponse.getData().email);
            }
            if(userResponse!=null && userResponse.getData()!=null && userResponse.getData().contactNumber!=null) {
                mNumber.setText(userResponse.getData().contactNumber);
            }
            mobileFlag=true;
            nameFlag=true;
            emailFlag=true;
        }catch (Exception e){
            //No impact don't do anything
        }
        setSellerImage();
        mName.setOnFocusChangeListener(this);
        mEmail.setOnFocusChangeListener(this);
        mNumber.setOnFocusChangeListener(this);
    }

    @OnClick(R.id.tv_do_connect_now)
    void doNowClicked(){
        Bundle bundle =getArguments();
        if(bundle!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.connectNowLeadNavigation);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickSerpCallConnect);
        }
        else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.connectNowLeadNavigation);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectCallConnect);
        }
        else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.connectNowLeadNavigation);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyCallConnect);
        }
        else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ShortListFavoriteAdapter.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.connectNowLeadNavigation);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickShortListFavCallConnect);
        }
        else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ShortListRecentFragment.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.connectNowLeadNavigation);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickShortListRecentCallConnect);
        }
        //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        LeadFormPresenter.getLeadFormPresenter().showLeadInstantCallBackFragment();
        //User data prefill
        try{
            UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
            if(userResponse == null){
                userResponse = new UserResponse();
                userResponse.setData(new UserData());
            }
            if(mName.getText()!=null) {
                userResponse.getData().firstName = mName.getText().toString();
            }
            if(mEmail.getText()!=null) {
                userResponse.getData().email = mEmail.getText().toString();
            }
            if(mNumber.getText()!=null) {
                userResponse.getData().contactNumber = mNumber.getText().toString().trim();
            }
            CookiePreferences.setLastUserInfo(getActivity(), JsonBuilder.toJson(userResponse).toString());
        } catch (Exception e) {
            //No impact don't do anything
            Crashlytics.logException(e);
        }
        mLeadFormPresenter.setTemporaryPhoneNo(mNumber.getText().toString().trim());
    }

    @OnClick(R.id.btn_call_later)
    void callLaterClicked(){

        if(mName.getText().toString().trim().length()==0){
            Bundle bundle =getArguments();
            if(bundle!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.add_user_name_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickSerpCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.add_user_name_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.add_user_name_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ShortListFavoriteAdapter.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.add_user_name_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickShortListFavCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ShortListRecentFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.add_user_name_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickShortListRecentCallConnect);
            }
            mUserNameTextInputLayout.setError(getActivity().getResources().getString(R.string.add_user_name_toast));
            /*Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.add_user_name_toast),
                    Toast.LENGTH_SHORT).show();*/

            /*MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                    getActivity().getResources().getString(R.string.add_user_name_toast), "ok");*/
        }
        else if(!ValidationUtil.isValidEmail(mEmail.getText().toString().trim())){
            Bundle bundle =getArguments();
            if(bundle!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_email));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickSerpCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_email));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_email));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ShortListFavoriteAdapter.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_email));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickShortListFavCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ShortListRecentFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_email));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickShortListRecentCallConnect);
            }
            mUserEmailTextInputLayout.setError(getActivity().getResources().getString(R.string.invalid_email));
            /*Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalid_email_toast),
                    Toast.LENGTH_SHORT).show();*/

            /*MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                    getActivity().getResources().getString(R.string.invalid_email), "ok");*/
        }
        else if(!ValidationUtil.isValidPhoneNumber(mNumber.getText().toString().trim(),mCountrySpinner.getSelectedItem().toString())){
            Bundle bundle =getArguments();
            if(bundle!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_phone_no_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickSerpCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_phone_no_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_phone_no_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ShortListFavoriteAdapter.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_phone_no_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickShortListFavCallConnect);
            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ShortListRecentFragment.class.getName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, getActivity().getResources().getString(R.string.invalid_phone_no_toast));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickShortListRecentCallConnect);
            }

            mUserMobileNumberTextInputLayout.setError(getActivity().getResources().getString(R.string.invalid_phone_no_toast));
            /*Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalid_phone_no_toast),
                    Toast.LENGTH_SHORT).show();*/
            /*MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                    getActivity().getResources().getString(R.string.invalid_phone_no_toast), "ok");*/
        }
        else {

            //User data prefill
            try{
                UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
                if(userResponse == null){
                    userResponse = new UserResponse();
                    userResponse.setData(new UserData());
                }
                userResponse.getData().firstName = mName.getText().toString();
                userResponse.getData().email = mEmail.getText().toString();
                userResponse.getData().contactNumber = mNumber.getText().toString().trim();
                CookiePreferences.setLastUserInfo(getActivity(), JsonBuilder.toJson(userResponse).toString());
            } catch (Exception e) {
                //No impact don't do anything
                Crashlytics.logException(e);
            }

            sendCallLaterEvent();

            PyrRequest mPyrRequest = new PyrRequest();
            PyrEnquiryType mPyrEnquiryType = new PyrEnquiryType();
            mPyrEnquiryType.setId(2);
            mPyrRequest.setEnquiryType(mPyrEnquiryType);
            mPyrRequest.setName(mName.getText().toString().trim());
            mPyrRequest.setEmail(mEmail.getText().toString().trim());
            mPyrRequest.setPhone(mNumber.getText().toString().trim());
            mPyrRequest.setMultipleCompanyIds(new Long[]{Long.valueOf(mLeadFormPresenter.getId())});
            mPyrRequest.setDomainId(1);
            mPyrRequest.setCountryId(mCountryId);
            mPyrRequest.setApplicationType("MobileAndroidApp");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("gcm_id", GcmPreferences.getGcmRegId(getContext()));
                mPyrRequest.setJsonDump(jsonObject.toString());
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            mPyrRequest.setCityId(mLeadFormPresenter.getCityId());
            mPyrRequest.setCityName(mLeadFormPresenter.getCityName());
            mPyrRequest.setSalesType(mLeadFormPresenter.getSalesType());
            mPyrRequest.setPageType(null);
            mPyrRequest.setSendOtp(false);
            mPyrRequest.setLocalityIds(new int[]{mLeadFormPresenter.getLocalityId().intValue()});
            if(mLeadFormPresenter.getProjectOrListingId()!=null && mLeadFormPresenter.getProjectOrListingId()!=0) {
                mPyrRequest.setListingId(mLeadFormPresenter.getProjectOrListingId());
            }
            if(mLeadFormPresenter.getProjectId()!=null && mLeadFormPresenter.getProjectId()!=0) {
                mPyrRequest.setProjectId(mLeadFormPresenter.getProjectId());
            }
            if(!TextUtils.isEmpty(mLeadFormPresenter.getProjectName())) {
                mPyrRequest.setProjectName(mLeadFormPresenter.getProjectName());
            }

            Bundle bundle =getArguments();
            String str = new Gson().toJson(mPyrRequest);
         //   Log.e("string==>> ", str);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str);
            } catch (JSONException e) {
                CommonUtil.TLog("exception", e);
                Crashlytics.logException(e);
            }
            if (jsonObject != null) {
                mCallLaterButton.setText(getResources().getString(R.string.submitting_info));
                mCallLaterButton.setClickable(false);
                ((PyrService) (MakaanServiceFactory.getInstance().getService(PyrService.class))).makePyrRequest(jsonObject);
            }
        }
    }

    void initializeCountrySpinner() {
        String str = StringUtil.readRawTextFile(getActivity(), R.raw.countries);
        CountryCodeResponse response = (CountryCodeResponse) JsonParser.parseJson(str, CountryCodeResponse.class);
        mCountries = response.getData();
        mCountryNames = new ArrayList<String>();
        int count = 0;
        for (CountryCodeResponse.CountryCodeData countryData : mCountries) {
            count++;
            mCountryNames.add(countryData.getLabel());
            //to differentiate between sorted countries and most used countries
            if (count == MOSTLY_USED_COUNTRIES) {
                mCountryNames.add("-------------------");
            }
        }
        mCountryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, mCountryNames) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.country_text_view);
                textView.setText(mCountryNames.get(position));
                return rowView;
            }

            @Override
            public boolean isEnabled(int position) {
                return position != MOSTLY_USED_COUNTRIES;
                //to make it non selectable
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }
        };
        mCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountrySpinner.setAdapter(mCountryAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mCountrySpinner.setDropDownWidth(400);
        }
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = getArguments();
                if (bundle != null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName()) && defaultCountry) {
                    //defaultCountry=true;
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillSerpGetCallBack);

                }
                else if (bundle != null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName()) && defaultCountry) {
                    //defaultCountry=true;
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillProjectGetCallBack);

                }
                else if (bundle != null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName()) && defaultCountry) {
                    //defaultCountry=true;
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillPropertyGetCallBack);

                }
                else if (bundle != null && bundle.getString("source").equalsIgnoreCase(ShortListFavoriteAdapter.class.getName()) && defaultCountry) {
                    //defaultCountry=true;
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillShortListFavGetCallBack);

                }
                else if (bundle != null && bundle.getString("source").equalsIgnoreCase(ShortListRecentFragment.class.getName()) && defaultCountry) {
                    //defaultCountry=true;
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillShortListRecentGetCallBack);

                }

                defaultCountry=true;
                if (position > MOSTLY_USED_COUNTRIES) {
                    position--;
                }
                mCodeTextView.setText(mCountries.get(position).getCountryCode() + "-");
                mCountryId = mCountries.get(position).getCountryId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @OnTextChanged(R.id.leadform_mobileno_edittext)
    public void onMobileTextChange(CharSequence cs, int arg1, int arg2, int arg3) {
        Bundle bundle = getArguments();
        if (!previousNumber.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName()) && mobileFlag) {
            mobileFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.mobile);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillSerpGetCallBack);
        }
        else if (!previousNumber.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName()) && mobileFlag) {
            mobileFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.mobile);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillProjectGetCallBack);

        }
        else if (!previousNumber.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName()) && mobileFlag) {
            mobileFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.mobile);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillPropertyGetCallBack);

        }
        else if (!previousNumber.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ShortListFavoriteAdapter.class.getName()) && mobileFlag) {
            mobileFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.mobile);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillShortListFavGetCallBack);

        }
        else if (!previousNumber.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ShortListRecentFragment.class.getName()) && mobileFlag) {
            mobileFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.mobile);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillShortListRecentGetCallBack);

        }
        previousNumber=cs.toString();
    }

    @OnTextChanged(R.id.leadform_name)
    public void onNameTextChange(CharSequence cs, int arg1, int arg2, int arg3) {
        Bundle bundle = getArguments();
        if (!previousName.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName()) && nameFlag) {
            nameFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.name);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillSerpGetCallBack);

        }
        else if (!previousName.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName()) && nameFlag) {
            nameFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.name);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillProjectGetCallBack);

        }
        else if (!previousName.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName()) && nameFlag) {
            nameFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.name);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillPropertyGetCallBack);

        }
        else if (!previousName.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ShortListFavoriteAdapter.class.getName()) && nameFlag) {
            nameFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.name);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillShortListFavGetCallBack);

        }
        else if (!previousName.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ShortListRecentFragment.class.getName()) && nameFlag) {
            nameFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.name);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillShortListRecentGetCallBack);

        }
        previousName=cs.toString();
    }

    @OnTextChanged(R.id.leadform_email)
    public void onEmailTextChange(CharSequence cs, int arg1, int arg2, int arg3) {
        Bundle bundle = getArguments();
        if (!previousEmail.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName()) && emailFlag) {
            emailFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.email);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillSerpGetCallBack);

        }
        else if (!previousEmail.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName()) && emailFlag) {
            emailFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProjectGetCallBack);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.email);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillProjectGetCallBack);

        }
        else if (!previousEmail.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName()) && emailFlag) {
            emailFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.email);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillPropertyGetCallBack);
        }
        else if (!previousEmail.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ShortListFavoriteAdapter.class.getName()) && emailFlag) {
            emailFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.email);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillShortListFavGetCallBack);

        }
        else if (!previousEmail.equalsIgnoreCase(cs.toString())&&
                bundle != null && bundle.getString("source").equalsIgnoreCase(ShortListRecentFragment.class.getName()) && emailFlag) {
            emailFlag=false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.email);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillShortListRecentGetCallBack);
        }
        previousEmail=cs.toString();
    }

    public void setSellerImage(){
        mSellerNameProfileText.setVisibility(View.INVISIBLE);
        mSellerImage.setVisibility(View.VISIBLE);
        int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
        if(null != mLeadFormPresenter.getSellerImageUrl()) {
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(mLeadFormPresenter.getSellerImageUrl(),
                    width, height, false), new CustomImageLoaderListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (!isVisible()) {
                        return;
                    }
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    mSellerImage.setVisibility(View.VISIBLE);
                    mSellerNameProfileText.setVisibility(View.GONE);
                    mSellerImage.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    super.onErrorResponse(volleyError);
                    mSellerImage.setVisibility(View.GONE);
                    mSellerNameProfileText.setVisibility(View.VISIBLE);
                    mSellerNameProfileText.setText(mLeadFormPresenter.getName());
                }
            });
        }
        else {
            mSellerImage.setVisibility(View.INVISIBLE);
            mSellerNameProfileText.setVisibility(View.VISIBLE);
            mSellerNameProfileText.setText(mLeadFormPresenter.getName());
        }
    }

    public void successfulResponse(){
        if(!isVisible()){
            return;
        }
        mCallLaterButton.setText(getResources().getString(R.string.get_call_later));
        mCallLaterButton.setClickable(true);
    }

    public void errorInResponse(){
        if(!isVisible()){
            return;
        }
        mCallLaterButton.setText(getResources().getString(R.string.get_call_later));
        mCallLaterButton.setClickable(true);
    }

    public void sendCallLaterEvent(){
        Bundle bundle=getArguments();
        if(bundle!=null && SerpActivity.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, mLeadFormPresenter.getSerpSubCategory());
            properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(bundle.getString("source")));
            properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.leadSubmitgetCallBAck);
        }
        else if(bundle!=null && ProjectFragment.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.project);
            properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(bundle.getString("source")));
            properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.leadSubmitgetCallBAck);
        }
        else if(bundle!=null && PropertyDetailFragment.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.PropertyInCaps);
            properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(bundle.getString("source")));
            properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.leadSubmitgetCallBAck);
        }
        else if(bundle!=null && ShortListFavoriteAdapter.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCaps);
            properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(bundle.getString("source")));
            properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.leadSubmitgetCallBAck);
        }
        else if(bundle!=null && ShortListRecentFragment.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCaps);
            properties.put(MakaanEventPayload.LABEL, mLeadFormPresenter.getSubmitStoredLabel(bundle.getString("source")));
            properties.put(MakaanEventPayload.VALUE, SINGLE_SELLER);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.leadSubmitgetCallBAck);
        }
    }

    @Override
    public void onFocusChange(View view, boolean focusGain) {
        /*mScrollView.setEnabled(true);
        mScrollView.set*/

        EditText editText=(EditText)view;
        if(null!=editText) {
            switch (editText.getId()){

                case R.id.leadform_name:{
                    if(!focusGain && mName!=null && mName.getText()!=null && mUserNameTextInputLayout!=null &&
                            mName.getText().toString().trim().length()==0){
                        mUserNameTextInputLayout.setError(getActivity().getResources().getString(R.string.add_user_name_toast));
                    }else if(mUserNameTextInputLayout!=null) {
                        mUserNameTextInputLayout.setErrorEnabled(false);
                    }
                    break;
                }
                case R.id.leadform_email:{
                    if(!focusGain && mEmail!=null && mEmail.getText()!=null && mUserEmailTextInputLayout!=null &&
                            !ValidationUtil.isValidEmail(mEmail.getText().toString().trim())){
                        mUserEmailTextInputLayout.setError(getActivity().getResources().getString(R.string.invalid_email));
                    }else if(mUserEmailTextInputLayout!=null){
                        mUserEmailTextInputLayout.setErrorEnabled(false);
                    }
                    break;
                }
                case R.id.leadform_mobileno_edittext:{
                    if(focusGain && mUserMobileNumberTextInputLayout!=null){
                        mUserMobileNumberTextInputLayout.setErrorEnabled(false);
                    }
                    break;
                }
            }
        }
    }
}

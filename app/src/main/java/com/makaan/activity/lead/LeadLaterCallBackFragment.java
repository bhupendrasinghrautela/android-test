package com.makaan.activity.lead;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
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
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PyrService;
import com.makaan.util.ImageUtils;
import com.makaan.util.JsonBuilder;
import com.makaan.util.JsonParser;
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
public class LeadLaterCallBackFragment extends MakaanBaseFragment {

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

    @Override
    protected int getContentViewId() {
        return R.layout.layout_lead_later_callback;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLeadFormPresenter= LeadFormPresenter.getLeadFormPresenter();
        mTextViewSellerName.setText(mLeadFormPresenter.getName().toLowerCase());
        mRatingBarSeller.setRating(Float.valueOf(mLeadFormPresenter.getScore()));
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
    }

    @OnClick(R.id.tv_do_connect_now)
    void doNowClicked(){
        //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        LeadFormPresenter.getLeadFormPresenter().showLeadInstantCallBackFragment();
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
            /*Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.add_user_name_toast),
                    Toast.LENGTH_SHORT).show();*/

            MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                    getActivity().getResources().getString(R.string.add_user_name_toast), "ok");
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
            /*Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalid_email_toast),
                    Toast.LENGTH_SHORT).show();*/

            MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                    getActivity().getResources().getString(R.string.invalid_email), "ok");
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
            /*Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalid_phone_no_toast),
                    Toast.LENGTH_SHORT).show();*/
            MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                    getActivity().getResources().getString(R.string.invalid_phone_no_toast), "ok");
        }
        else {

            //User data prefill
            try{
                UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
                userResponse.getData().contactNumber = mNumber.getText().toString().trim();
                CookiePreferences.setLastUserInfo(getActivity(), JsonBuilder.toJson(userResponse).toString());
            }catch (Exception e){
                //No impact don't do anything
            }

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
            mPyrRequest.setJsonDump(GcmPreferences.getGcmRegId(getContext()));
            mPyrRequest.setCityId(mLeadFormPresenter.getCityId());
            mPyrRequest.setCityName(mLeadFormPresenter.getCityName());
            mPyrRequest.setSalesType(mLeadFormPresenter.getSalesType());
            mPyrRequest.setPageType(null);
            mPyrRequest.setSendOtp(false);
            mPyrRequest.setLocalityIds(new int[]{mLeadFormPresenter.getLocalityId().intValue()});
            mPyrRequest.setListingId(mLeadFormPresenter.getProjectOrListingId());
            mPyrRequest.setProjectId(mLeadFormPresenter.getProjectId());
//            mPyrRequest.setPropertyId(mLeadFormPresenter.getPropertyId());
            mPyrRequest.setProjectName(mLeadFormPresenter.getProjectName());

            Bundle bundle =getArguments();
            String str = new Gson().toJson(mPyrRequest);
         //   Log.e("string==>> ", str);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
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
                    if(!isVisible()){
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
        mCallLaterButton.setText(getResources().getString(R.string.get_call_later));
        mCallLaterButton.setClickable(true);
    }

    public void errorInResponse(){
        mCallLaterButton.setText(getResources().getString(R.string.get_call_later));
        mCallLaterButton.setClickable(true);
    }

}

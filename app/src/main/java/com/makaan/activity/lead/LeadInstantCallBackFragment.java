package com.makaan.activity.lead;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.makaan.response.country.CountryCodeResponse;
import com.makaan.response.leadForm.InstantCallbackResponse;
import com.makaan.response.user.UserResponse;
import com.makaan.service.LeadInstantCallbackService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.ImageUtils;
import com.makaan.util.JsonBuilder;
import com.makaan.util.JsonParser;
import com.makaan.util.StringUtil;
import com.makaan.util.ValidationUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadInstantCallBackFragment extends MakaanBaseFragment {

    @Bind(R.id.select_country_spinner)
    Spinner mCountrySpinner;
    @Bind(R.id.leadform_country_code_textview)
    TextView mCodeTextView;
    @Bind(R.id.leadform_mobileno_edittext)
    EditText mNumber;
    LeadFormPresenter mLeadFormPresenter;
    @Bind(R.id.tv_seller_name)
    TextView mTextViewSellerName;
    @Bind(R.id.seller_ratingbar)
    RatingBar mRatingBarSeller;
    @Bind(R.id.iv_seller_name)
    TextView mSellerNameProfileText;
    @Bind(R.id.iv_seller_image_instant_call_back)
    CircleImageView mSellerImage;
    @Bind(R.id.btn_get_instant_call)
    Button mInstantCallButton;

    private Integer mCountryId;
    private ArrayAdapter<String> mCountryAdapter;
    private List<String> mCountryNames;
    private List<CountryCodeResponse.CountryCodeData> mCountries;
    private static final int MOSTLY_USED_COUNTRIES = 7;
    private boolean mobileFlag=true,defaultCountry=false;

    @Override
    protected int getContentViewId() {
        return R.layout.layout_lead_instant_callback;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLeadFormPresenter= LeadFormPresenter.getLeadFormPresenter();
        mTextViewSellerName.setText(mLeadFormPresenter.getName().toLowerCase());
        mRatingBarSeller.setRating(Float.valueOf(mLeadFormPresenter.getScore()));
        mobileFlag=false;
        defaultCountry=false;
        initializeCountrySpinner();
        //User data prefill
        try{
            UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
            if(!TextUtils.isEmpty(mLeadFormPresenter.getTemporaryPhoneNo())){
                mNumber.setText(mLeadFormPresenter.getTemporaryPhoneNo());
            }
            else {
                if (userResponse != null && userResponse.getData() != null && userResponse.getData().contactNumber != null) {
                    mNumber.setText(userResponse.getData().contactNumber);
                }
            }
            mobileFlag=true;
        }catch (Exception e){
            //No impact don't do anything
        }

        setSellerImage();
    }

    @OnClick(R.id.btn_get_instant_call)
    void getInstantCallClick() {
        if (ValidationUtil.isValidPhoneNumber(mNumber.getText().toString().trim(),mCountrySpinner.getSelectedItem().toString())) {
            //TODO pass values instead of hardcoded values
            JSONObject jsonObject=new JSONObject();
            try {
                Bundle bundle =getArguments();
                if(bundle!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
                    jsonObject.put("listingId", mLeadFormPresenter.getProjectOrListingId());
                }
                else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
                    jsonObject.put("projectId", mLeadFormPresenter.getProjectOrListingId());
                }
                else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
                    jsonObject.put("listingId", mLeadFormPresenter.getProjectOrListingId());
                }
                jsonObject.put("applicationType", "MobileAndroidApp");
                jsonObject.put("cityId", mLeadFormPresenter.getCityId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //User data prefill
            try{
                UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
                userResponse.getData().contactNumber = mNumber.getText().toString().trim();
                CookiePreferences.setLastUserInfo(getActivity(), JsonBuilder.toJson(userResponse).toString());
            }catch (Exception e){
                //No impact don't do anything
            }
            mInstantCallButton.setText(getResources().getString(R.string.connecting));
            mInstantCallButton.setClickable(false);
            ((LeadInstantCallbackService) MakaanServiceFactory.getInstance().getService(LeadInstantCallbackService.class)).makeInstantCallbackRequest(
                    mNumber.getText().toString().trim(), mLeadFormPresenter.getPhone(), mCountryId, jsonObject);
        } else {
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
            if(mNumber.getText().toString().trim().length()>0) {
                /*Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.invalid_phone_no_toast),
                        Toast.LENGTH_SHORT).show();*/

                if(getActivity() != null) {
                    MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                            getActivity().getString(R.string.invalid_phone_no_toast), "ok");
                }
            }
            else if(mNumber.getText().toString().trim().length()==0){
                /*Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.please_enter_phone_no),
                        Toast.LENGTH_SHORT).show();*/
                if(getActivity() != null) {
                    MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                            getActivity().getString(R.string.please_enter_phone_no), "ok");
                }
            }
        }
    }

    @Subscribe
    public void getConnectNowResponse(InstantCallbackResponse response){
        /*if(response.getStatusCode()==null && !response.getStatusCode().equals("2XX")){
            *//*Toast.makeText(getActivity(), getActivity().getString(R.string.generic_error), Toast.LENGTH_SHORT).show();*//*

            if(getActivity() != null) {
                MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                        getActivity().getString(R.string.generic_error), "ok");
            }
        }*/
    }

    @OnClick(R.id.tv_get_call_now)
    void getCallBackLaterClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        LeadFormPresenter.getLeadFormPresenter().showLeadCallNowFragment();
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mCountrySpinner.setDropDownWidth(400);
        }
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = getArguments();
                if (bundle != null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())&& defaultCountry) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillSerpCall);

                } else if (bundle != null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())&& defaultCountry) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProjectCall);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillProjectCall);

                } else if (bundle != null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())&& defaultCountry) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillPropertyCall);

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
        if (bundle != null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName()) && mobileFlag) {
            mobileFlag = false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.mobile);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillSerpCall);

        } else if (bundle != null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName()) && mobileFlag) {
            mobileFlag = false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProjectCall);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.mobile);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillProjectCall);

        } else if (bundle != null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName()) && mobileFlag) {
            mobileFlag = false;
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.mobile);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillPropertyCall);


        }
    }

    public void setSellerImage() {
        mSellerNameProfileText.setVisibility(View.GONE);
        mSellerImage.setVisibility(View.VISIBLE);
        int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
        if (null != mLeadFormPresenter.getSellerImageUrl()) {
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
                    mSellerNameProfileText.setVisibility(View.INVISIBLE);
                    mSellerImage.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    super.onErrorResponse(volleyError);
                    mSellerImage.setVisibility(View.INVISIBLE);
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

    public void successfulInstantResponse(){
        mInstantCallButton.setClickable(true);
        mInstantCallButton.setText(getResources().getString(R.string.connect_now));
    }

    public void errorInInstantResponse(){
        mInstantCallButton.setClickable(true);
        mInstantCallButton.setText(getResources().getString(R.string.connect_now));
    }

}

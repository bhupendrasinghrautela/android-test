package com.makaan.activity.lead;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.MakaanMessageDialogFragment;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.request.pyr.PyrEnquiryType;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.country.CountryCodeResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PyrService;
import com.makaan.util.JsonParser;
import com.makaan.util.StringUtil;
import com.makaan.util.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by proptiger on 25/2/16.
 */
public class MultipleLeadFormFragment extends MakaanBaseFragment {

    @Bind(R.id.multiple_leadform_name)EditText mName;
    @Bind(R.id.multiple_leadform_email)EditText mEmail;
    @Bind(R.id.multiple_leadform_mobileno_edittext)EditText mNumber;
    @Bind(R.id.multiple_lead_form_country_spinner)Spinner mCountrySpinner;
    @Bind(R.id.multiple_leadform_country_code_textview) TextView mCodeTextView;
    @Bind(R.id.multiple_lead_forn_bhk_apartment)TextView mBhkTextView;
    @Bind(R.id.mutltiple_lead_form_area)TextView mArea;
    @Bind(R.id.mutltiple_lead_form_locality)TextView mLocality;
    LeadFormPresenter mLeadFormPresenter;
    private ArrayAdapter<String> mCountryAdapter;
    private List<String> mCountryNames;
    private List<CountryCodeResponse.CountryCodeData> mCountries;
    private static final int MOSTLY_USED_COUNTRIES = 7;
    private Integer mCountryId;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLeadFormPresenter= LeadFormPresenter.getLeadFormPresenter();
        initializeCountrySpinner();
        setData();
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.multiple_lead_form_get_call_back)
    void callLaterClicked() {
        if ((mName.getText().toString().trim().length() == 0)) {
            /*Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.add_user_name_toast),
                    Toast.LENGTH_SHORT).show();*/
            if(getActivity() != null) {
                MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                        getActivity().getResources().getString(R.string.add_user_name_toast), "ok");
            }

        }
        else if (!ValidationUtil.isValidEmail(mEmail.getText().toString().trim())) {
            /*Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalid_email_toast),
                    Toast.LENGTH_SHORT).show();*/
            if(getActivity() != null) {
                MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                        getActivity().getResources().getString(R.string.invalid_email), "ok");
            }

        }
        else if (!ValidationUtil.isValidPhoneNumber(mNumber.getText().toString().trim(), mCountrySpinner.getSelectedItem().toString())) {
            /*Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalid_phone_no_toast),
                    Toast.LENGTH_SHORT).show();*/
            if(getActivity() != null) {
                MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                        getActivity().getResources().getString(R.string.invalid_phone_no_toast), "ok");
            }
        }
        else{
            PyrRequest mPyrRequest = new PyrRequest();
            PyrEnquiryType mPyrEnquiryType = new PyrEnquiryType();
            mPyrRequest.setEnquiryType(mPyrEnquiryType);

            mPyrRequest.setName(mName.getText().toString().trim());
            mPyrRequest.setEmail(mEmail.getText().toString().trim());
            mPyrRequest.setPhone(mNumber.getText().toString().trim());
            mPyrRequest.setMultipleCompanyIds(mLeadFormPresenter.getMultipleSellerIds());
            mPyrRequest.setDomainId(1);
            mPyrRequest.setCountryId(mCountryId);
            mPyrRequest.setApplicationType("MobileAndroidApp");
            mPyrRequest.setCityId(mLeadFormPresenter.getCityId());
            mPyrRequest.setPageType(null);
            mPyrRequest.setSendOtp(true);
            mPyrRequest.setLocalityIds(new int[]{mLeadFormPresenter.getLocalityId().intValue()});
            Bundle bundle =getArguments();
            if(bundle!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
                mPyrRequest.setListingId(mLeadFormPresenter.getProjectOrListingId());

            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
                mPyrRequest.setProjectId(mLeadFormPresenter.getProjectOrListingId());

            }
            else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
                mPyrRequest.setListingId(mLeadFormPresenter.getProjectOrListingId());
            }
            mLeadFormPresenter.setName(mName.getText().toString().trim());
            mLeadFormPresenter.setPhone(mNumber.getText().toString().trim());
            mLeadFormPresenter.setPyrRequest(mPyrRequest);

            String str = new Gson().toJson(mPyrRequest);
            //   Log.e("string==>> ", str);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonObject != null)
                ((PyrService) (MakaanServiceFactory.getInstance().getService(PyrService.class))).makePyrRequest(jsonObject);
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
        mCountrySpinner.setDropDownWidth(400);
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               /* Bundle bundle = getArguments();
                if (bundle != null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillSerpGetCallBack);

                }
                else if (bundle != null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillProjectGetCallBack);

                }
                else if (bundle != null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.country);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.fillPropertyGetCallBack);

                }*/
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

    private void setData(){
        mBhkTextView.setText(mLeadFormPresenter.getBhkAndUnitType());
        mArea.setText(mLeadFormPresenter.getArea());
        mLocality.setText(mLeadFormPresenter.getLocality());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.lead_form_multiple_seller;
    }

}

package com.makaan.activity.Lead;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.activity.pyr.PyrOtpVerification;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.request.pyr.PyrEnquiryType;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.country.CountryCodeResponse;
import com.makaan.response.pyr.PyrPostResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PyrService;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;
import com.makaan.util.StringUtil;
import com.makaan.util.ValidationUtil;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadLaterCallBackFragment extends Fragment {

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
    private Integer mCountryId;
    private ArrayAdapter<String> mCountryAdapter;
    private List<String> mCountryNames;
    private List<CountryCodeResponse.CountryCodeData> mCountries;
    private static final int MOSTLY_USED_COUNTRIES = 7;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_lead_later_callback, container, false);
        ButterKnife.bind(this, view);
        AppBus.getInstance().register(this);
        initializeCountrySpinner();
        return view;
    }

    @OnClick(R.id.tv_do_call_now)
    void doNowClicked(){
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        LeadFormPresenter.getLeadFormPresenter().showLeadCallNowFragment();
    }

    @OnClick(R.id.btn_call_later)
    void callLaterClicked(){

        if(mName.getText().toString().trim().length()==0){
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.add_user_name_toast),
                    Toast.LENGTH_SHORT).show();
        }
        else if(!ValidationUtil.isValidEmail(mEmail.getText().toString().trim())){
            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalid_email_toast),
                    Toast.LENGTH_SHORT).show();
        }
        else if(!ValidationUtil.isValidPhoneNumber(mNumber.getText().toString().trim(),mCountrySpinner.getSelectedItem().toString())){
            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalid_phone_no_toast),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            PyrRequest mPyrRequest = new PyrRequest();
            PyrEnquiryType mPyrEnquiryType = new PyrEnquiryType();
            mPyrRequest.setEnquiryType(mPyrEnquiryType);

            mPyrRequest.setName(mName.getText().toString().trim());
            mPyrRequest.setEmail(mEmail.getText().toString().trim());
            mPyrRequest.setPhone(mNumber.getText().toString().trim());
            mPyrRequest.setMultipleSellerIds(new Long[]{(long) 4353, (long) 64364});
            mPyrRequest.setDomainId(1);
            mPyrRequest.setCountryId(mCountryId);

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
            public boolean isEnabled(int position) {
                return position != MOSTLY_USED_COUNTRIES;
                //to make it non selectable
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }
        };
        mCountrySpinner.setAdapter(mCountryAdapter);
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

    @Subscribe
    public void pyrResponse(PyrPostResponse pyrPostResponse){
        if(pyrPostResponse.getStatusCode().equals("2XX")) {
            Toast.makeText(getActivity(),"Requirement Posted Successfully",Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }
}

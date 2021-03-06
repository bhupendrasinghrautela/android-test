package com.makaan.fragment.pyr;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.makaan.R;
import com.makaan.adapter.listing.ViewAdapterRings;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.agents.TopBuyAgentsPyrEvent;
import com.makaan.event.agents.TopRentAgentsPyrEvent;
import com.makaan.event.agents.callback.TopBuyAgentsPyrCallback;
import com.makaan.event.agents.callback.TopRentAgentsPyrCallback;
import com.makaan.network.VolleyErrorParser;
import com.makaan.notification.GcmPreferences;
import com.makaan.pojo.SerpObjects;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.country.CountryCodeResponse;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.TermFilter;
import com.makaan.response.user.UserResponse;
import com.makaan.response.user.UserResponse.UserData;
import com.makaan.service.AgentService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.pyr.PyrBudgetCardView;
import com.makaan.ui.pyr.PyrPropertyCardView;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/*
import com.makaan.event.pyr.TopAgentsGetEvent;
import com.makaan.request.selector.Selector;
import com.makaan.response.country.CountryCodeResponse;
import com.makaan.response.pyr.TopAgentsResponse;
import com.makaan.response.search.Search;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.pyr.TopAgentsByLocalityService;
import com.makaan.ui.pyr.PyrBudgetCardView;
*/

/**
 * Created by proptiger on 8/1/16.
 */
public class PyrPageFragment extends Fragment implements View.OnFocusChangeListener ,PyrPagePresenter.OnUserInfoErrorListener{
    @Bind(R.id.post_requirements)
    TextView mPostRequirements;

    @Bind(R.id.select_country_spinner)
    Spinner mCountrySpinner;

    @Bind(R.id.leadform_country_code_textview)
    TextView mCodeTextView;

    @Bind(R.id.pyr_selected_locality_count)
    TextView mLocalityCount;

    @Bind(R.id.pyr_selected_propertyt_type_count)
    TextView mPropertyTypeCount;

    @Bind(R.id.pyr_page_name)EditText mUserName;

    @Bind(R.id.pyr_page_email)EditText mUserEmail;

    @Bind(R.id.leadform_mobileno_edittext)EditText mUserMobile;
    @Bind(R.id.property_value)TextView mPropertyString;
    @Bind(R.id.location_value)TextView mLocationString;
    @Bind(R.id.rent)RadioButton mRentButton;
    @Bind(R.id.buy_rent_radiogroup)RadioGroup mRadioGroup;
    @Bind(R.id.pyr_user_name)TextInputLayout mPyrNameTextInputLayout;
    @Bind(R.id.pyr_user_email)TextInputLayout mPyrEmailTextInputLayout;
    @Bind(R.id.pyr_user_mobile_no)TextInputLayout mPyrMobileTextInputLayout;

    private Integer mCountryId;
    private ArrayAdapter<String> mCountryAdapter;
    private List<String> mCountryNames;
    private List<CountryCodeResponse.CountryCodeData> mCountries;
    private static final int MOSTLY_USED_COUNTRIES = 7;
    private String values[]={"1","2","3","3+"};
    private PyrPagePresenter pyrPagePresenter;
    private ArrayList<FilterGroup> mGroupsBuy;
    private ArrayList<FilterGroup> mGroupsRent;

    private PyrBudgetCardView mBudgetCardView;
    private PyrPropertyCardView mPropertyCardView;
    private GridView mGridView;
    private boolean mIsBuySelected = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pyr_page_layout, container, false);
        ButterKnife.bind(this, view);

        mGridView = (GridView) view.findViewById(R.id.pyr_bedroom_grid_view);
        mBudgetCardView = (PyrBudgetCardView) view.findViewById(R.id.pyr_budget_card_view);
        mPropertyCardView = (PyrPropertyCardView) view.findViewById(R.id.select_property_layout);
        mUserEmail.setOnFocusChangeListener(this);
        mUserName.setOnFocusChangeListener(this);
        mUserMobile.setOnFocusChangeListener(this);

        pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();

        // access group
        ArrayList<FilterGroup> grpsBuy = MasterDataCache.getInstance().getAllBuyPyrGroups();
        ArrayList<FilterGroup> grpsRent = MasterDataCache.getInstance().getAllRentPyrGroups();

        setUserInfo();

        try {
            boolean newGroups = false;
            if(mGroupsBuy == null || mGroupsRent == null) {
                newGroups = true;
                mGroupsBuy = getClonedFilterGroups(grpsBuy);
                mGroupsRent = getClonedFilterGroups(grpsRent);
            }
            if(pyrPagePresenter.isFromProject()){
                prefillBudgetValues(pyrPagePresenter.isPyrFromProjectBuySelected());
                pyrPagePresenter.setBuySelected(pyrPagePresenter.isPyrFromProjectBuySelected());
            }else {
                pyrPagePresenter.setBuySelected(SerpObjects.isBuyContext(getActivity()));
            }//pre-fill buy context
            setLocaityInfo();//Pre-fill localities
/*            for(FilterGroup group : mGroupsBuy) {
                String name = group.internalName;
                if("i_beds".equals(name)) {
                    if(newGroups) {
                        group.reset();
                    }
                    ViewAdapterRings viewAdapterRings=new ViewAdapterRings(getActivity(), group.termFilterValues);
                    mGridView.setAdapter(viewAdapterRings);
                } else if("i_budget".equals(name)) {
                    if(newGroups) {
                        group.reset();
                    }
                    mBudgetCardView.setValues(group.rangeFilterValues);

                } else if("i_property_type".equals(name)) {
                    if(newGroups) {
                        group.reset();
                    }
                    mPropertyCardView.setValues(group.termFilterValues);
                }
            }*/
        } catch (CloneNotSupportedException ex) {
            Crashlytics.logException(ex);
            CommonUtil.TLog("exception", ex);
        }

        initializeCountrySpinner();

        return view;
    }

    private ArrayList<FilterGroup> getClonedFilterGroups(ArrayList<FilterGroup> filterGroups) throws CloneNotSupportedException {
        ArrayList<FilterGroup> group = new ArrayList<>(filterGroups.size());
        for(FilterGroup filter : filterGroups) {
            group.add(filter.clone());
        }
        return group;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //AppBus.getInstance().register(this);
        pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
        pyrPagePresenter.setPyrPageFragmentInstance(this);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        AppBus.getInstance().register(this);
    }

    @OnClick(R.id.post_requirements)
    public void postRequirements() {

        ArrayList<String> localities = new ArrayList<>();
        ArrayList<String> bedrooms = new ArrayList<>();
        ArrayList<String>propertyTypes=new ArrayList<>();

        PyrRequest pyrRequest=new PyrRequest();
        boolean makeRequest=pyrPagePresenter.makePartialPyrRequest(getActivity() ,pyrRequest,
                (mIsBuySelected ? mGroupsBuy : mGroupsRent), this);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("gcm_id", GcmPreferences.getGcmRegId(getContext()));
            pyrRequest.setJsonDump(jsonObject.toString());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        try {
        UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
        if(userResponse == null){
            userResponse = new UserResponse();
            userResponse.setData(new UserData());
        }
        userResponse.getData().firstName = mUserName.getText().toString();
        userResponse.getData().email = mUserEmail.getText().toString();
        userResponse.getData().contactNumber = mUserMobile.getText().toString().trim();
            CookiePreferences.setLastUserInfo(getActivity(), JsonBuilder.toJson(userResponse).toString());
        } catch (JSONException e) {
            Crashlytics.logException(e);
            CommonUtil.TLog("exception", e);
        }
        if(null!=pyrRequest.getLocalityIds() && pyrRequest.getLocalityIds().length>0) {
            for (int value : pyrRequest.getLocalityIds()) {
                localities.add(String.valueOf(value));
            }
        }

        if(null!= pyrRequest.getBhk() && pyrRequest.getBhk().size()>0) {
            for (int value : pyrRequest.getBhk()) {
                bedrooms.add(String.valueOf(value));
            }
        }

        if(null!=pyrRequest.getPropertyTypes() && pyrRequest.getPropertyTypes().size()>0) {
            for (String value : pyrRequest.getPropertyTypes()) {
                propertyTypes.add(value);
            }
        }

        if(makeRequest) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerPyr);
            properties.put(MakaanEventPayload.LABEL, pyrPagePresenter.getLabelStringOnNextClick(pyrRequest));
            MakaanEventPayload.endBatch(getContext(), pyrPagePresenter.getScreenNameAction(pyrPagePresenter.getSourceScreenName()));

            Properties properties1 = MakaanEventPayload.beginBatch();
            properties1.put(MakaanEventPayload.CATEGORY, pyrPagePresenter.getCategoryForPyrSubmit(pyrPagePresenter.getSourceScreenName()));
            properties1.put(MakaanEventPayload.LABEL, pyrPagePresenter.getLabelStringOnNextClick(pyrRequest));
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.pyrSubmit);
        }

        pyrPagePresenter.setAlreadySelectedMaxBudget(pyrRequest.getMaxBudget());
        pyrPagePresenter.setAlreadySelectedMinBudget(pyrRequest.getMinBudget());

        if(makeRequest && pyrRequest.getSalesType().equals("buy")) {
            AgentService agentService = ((AgentService) (MakaanServiceFactory.getInstance().getService(AgentService.class)));
            agentService.getTopAgentsForLocality((long) pyrRequest.getCityId(), localities
                    , bedrooms, propertyTypes, 20, false, new TopBuyAgentsPyrCallback(), pyrRequest.getMinBudget(), pyrRequest.getMaxBudget());
        }
        else if(makeRequest && pyrRequest.getSalesType().equals("rent")){
            ((AgentService) (MakaanServiceFactory.getInstance().getService(AgentService.class))).getTopAgentsForLocality((long) pyrRequest.getCityId(), localities,
                    bedrooms, propertyTypes, 20, true, new TopRentAgentsPyrCallback(), pyrRequest.getMinBudget(), pyrRequest.getMaxBudget());
        }
    }

    @Subscribe
    public void onResults(TopRentAgentsPyrEvent topRentAgentsPyrEvent) {
        if(!isVisible()) {
            return;
        }
        if(topRentAgentsPyrEvent.error!=null){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.errorWhileSubmitting);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorPyr);
            String msg = VolleyErrorParser.getMessage(topRentAgentsPyrEvent.error);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<TopAgent> topAgentList = topRentAgentsPyrEvent.topAgents;
        if ( topAgentList.size() > 0) {
            PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
            mPyrPagePresenter.setmTopAgentsDatas(topAgentList);
            mPyrPagePresenter.showTopSellersFragment();
        }
        else {
            PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
            mPyrPagePresenter.showNoSellersFragment();
        }

    }

    @Subscribe
    public void onResults(TopBuyAgentsPyrEvent topBuyAgentsPyrEvent) {
        if(!isVisible()) {
            return;
        }
        if(topBuyAgentsPyrEvent.error!=null){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.errorWhileSubmitting);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorPyr);
            String msg = VolleyErrorParser.getMessage(topBuyAgentsPyrEvent.error);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<TopAgent> topAgentList = topBuyAgentsPyrEvent.topAgents;
        if ( topAgentList.size() > 0) {
            PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
            mPyrPagePresenter.setmTopAgentsDatas(topAgentList);
            mPyrPagePresenter.showTopSellersFragment();
        }
        else {
            PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
            mPyrPagePresenter.showNoSellersFragment();
        }

    }

    @Override
    public void onDetach() {
        AppBus.getInstance().unregister(this);
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        setPropertyCount();
        setLocaityInfo();
        setUserInfo();
    }

    @OnTextChanged(R.id.pyr_page_name)
    public void setName(CharSequence text){
        pyrPagePresenter.setName(String.valueOf(text));
    }

    @OnTextChanged(R.id.pyr_page_email)
    public void setEmail(CharSequence text){
        pyrPagePresenter.setUserEmail(String.valueOf(text));
    }

    @OnTextChanged(R.id.leadform_mobileno_edittext)
    public void setPhone(CharSequence text){
        pyrPagePresenter.setPhoneNumber(String.valueOf(text));
    }

    public void setLocaityInfo(){
        List<SearchResponseItem>selectedProjectsList=pyrPagePresenter.getAlreadySelectedProjects();
        if(selectedProjectsList.size()>0){
            int count = 0;
            StringBuilder stringBuilder=new StringBuilder();
            String space="\n";
            for(SearchResponseItem responseItem:selectedProjectsList){
                if(count<2) {
                    stringBuilder.append(responseItem.entityName);
                    stringBuilder.append(space);
                }
                space=" ";
                count++;
            }
            if(count>2){
                stringBuilder.append("+ "+(count-2));
            }
            mLocationString.setText(stringBuilder.toString().toLowerCase());
            mLocalityCount.setVisibility(View.VISIBLE);
            mLocalityCount.setText(String.valueOf(selectedProjectsList.size()));
        }
        else{
            mLocationString.setText(getResources().getString(R.string.pyr_locality_value_string).toLowerCase());
            mLocalityCount.setVisibility(View.GONE);
        }
    }

    public void setPropertyCount(){
        int count = 0;
        StringBuilder stringBuilder=new StringBuilder();
        String space="\n";
        ArrayList<FilterGroup> grp = mIsBuySelected ? mGroupsBuy : mGroupsRent;
        for(FilterGroup group : grp) {
            String name = group.internalName;
            if("i_property_type".equals(name)) {
                for(TermFilter filter : group.termFilterValues) {
                    if(filter.selected) {
                        if(count<2) {
                            stringBuilder.append(filter.displayName.replace("/flat", ""));
                            stringBuilder.append(space);
                        }
                        space=" ";
                        count++;
                    }
                }
            }
        }

        if(count>2){
            stringBuilder.append("+ "+(count-2));
        }
        if(count>0){
            mPropertyString.setText(stringBuilder.toString());
            mPropertyTypeCount.setVisibility(View.VISIBLE);
            mPropertyTypeCount.setText(String.valueOf(count));
        }
        else{
            mPropertyTypeCount.setVisibility(View.GONE);
        }
    }

    public void setUserInfo(){
        try {
                UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
                if(userResponse != null) {
                    if(userResponse.getData()!=null && userResponse.getData().firstName!=null) {
                        mUserName.setText(userResponse.getData().firstName);
                    }
                    if(userResponse.getData()!=null && userResponse.getData().email!=null) {
                        mUserEmail.setText(userResponse.getData().email);
                    }
                    if(userResponse.getData()!=null && userResponse.getData().contactNumber!=null) {
                        mUserMobile.setText(userResponse.getData().contactNumber);
                    }
                }
                else{
                    mUserName.setText(pyrPagePresenter.getName());
                    mUserEmail.setText(pyrPagePresenter.getEmail());
                    mUserMobile.setText(pyrPagePresenter.getPhonNumber());
                }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void setBedroomInfo(){

    }

    void initializeCountrySpinner()
    {
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
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
            mCountrySpinner.setDropDownWidth(400);
        }
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > MOSTLY_USED_COUNTRIES) {
                    position--;
                }
                mCodeTextView.setText(mCountries.get(position).getCountryCode() + "-");
                mCountryId = mCountries.get(position).getCountryId();
                pyrPagePresenter.setCountryName(mCountries.get(position).getLabel());
                pyrPagePresenter.setCountryId(mCountries.get(position).getCountryId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setBuySelected(boolean selected) {
        ArrayList<FilterGroup> grps = selected ? mGroupsBuy : mGroupsRent;
        if(!selected){
            mRentButton.setChecked(true);
        }
        else {
            mRentButton.setChecked(false);
        }
        mIsBuySelected = selected;
        for(FilterGroup group : grps) {
            String name = group.internalName;
            if("i_beds".equals(name)) {
                ViewAdapterRings viewAdapterRings = new ViewAdapterRings(getActivity(), group.termFilterValues);
                mGridView.setAdapter(viewAdapterRings);
            } else if("i_budget".equals(name)) {
                mBudgetCardView.setValues(group.rangeFilterValues);

            } else if("i_property_type".equals(name)) {
                mPropertyCardView.setValues(group.termFilterValues);
            }
        }
        setPropertyCount();
    }

    public void prefillBudgetValues(boolean pyrFromProjectBuySelected){
        ArrayList<FilterGroup> grps = pyrFromProjectBuySelected ? mGroupsBuy : mGroupsRent;
        mIsBuySelected = pyrFromProjectBuySelected;
        for(FilterGroup group : grps) {
            String name = group.internalName;
            if("i_budget".equals(name)) {
                group.rangeFilterValues.get(0).selectedMinValue = pyrPagePresenter.getAlreadySelectedMinBudget();
                if (pyrPagePresenter.getAlreadySelectedMaxBudget() != 0) {
                    group.rangeFilterValues.get(0).selectedMaxValue = pyrPagePresenter.getAlreadySelectedMaxBudget();
                }
              mBudgetCardView.setValues(group.rangeFilterValues);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText=(EditText)v;
        if(null!=editText) {
            switch (editText.getId()){

                case R.id.pyr_page_name:{
                    if(!hasFocus && mUserName!=null && mUserName.getText()!=null && mPyrNameTextInputLayout!=null &&
                            mUserName.getText().toString().trim().length()==0){
                        mPyrNameTextInputLayout.setError(getActivity().getResources().getString(R.string.add_user_name_toast));
                    }else if(mPyrNameTextInputLayout!=null) {
                        mPyrNameTextInputLayout.setErrorEnabled(false);
                    }
                    break;
                }
                case R.id.pyr_page_email:{
                    if(!hasFocus && mUserEmail!=null && mUserEmail.getText()!=null && mPyrEmailTextInputLayout!=null &&
                            !ValidationUtil.isValidEmail(mUserEmail.getText().toString().trim())){
                        mPyrEmailTextInputLayout.setError(getActivity().getResources().getString(R.string.invalid_email));
                    }else if(mPyrEmailTextInputLayout!=null){
                        mPyrEmailTextInputLayout.setErrorEnabled(false);
                    }
                    break;
                }
                case R.id.leadform_mobileno_edittext:{
                    if(hasFocus && mPyrMobileTextInputLayout!=null){
                        mPyrMobileTextInputLayout.setErrorEnabled(false);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void errorInUserName() {
        if(mPyrNameTextInputLayout!=null) {
            mPyrNameTextInputLayout.setError(getActivity().getResources().getString(R.string.add_user_name_toast));
        }
    }

    @Override
    public void errorInUserEmail() {
        if(mPyrEmailTextInputLayout!=null){
            mPyrEmailTextInputLayout.setError(getActivity().getResources().getString(R.string.invalid_email));
        }
    }

    @Override
    public void errorInUserPhoneNo() {
        if(mPyrMobileTextInputLayout!=null){
            mPyrMobileTextInputLayout.setError(getActivity().getResources().getString(R.string.invalid_phone_no_toast));
        }
    }
}

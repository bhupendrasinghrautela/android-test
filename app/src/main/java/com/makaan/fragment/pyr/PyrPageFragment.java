package com.makaan.fragment.pyr;

import android.app.Activity;
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
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.agents.TopBuyAgentsPyrEvent;
import com.makaan.event.agents.TopRentAgentsPyrEvent;
import com.makaan.event.agents.callback.TopBuyAgentsPyrCallback;
import com.makaan.event.agents.callback.TopRentAgentsPyrCallback;
import com.makaan.pojo.SerpObjects;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.country.CountryCodeResponse;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.TermFilter;
import com.makaan.response.user.UserResponse;
import com.makaan.service.AgentService;
import com.makaan.service.MakaanServiceFactory;

import com.makaan.adapter.listing.ViewAdapterRings;
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

import com.makaan.ui.pyr.PyrBudgetCardView;
import com.makaan.ui.pyr.PyrPropertyCardView;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import butterknife.OnTextChanged;

/**
 * Created by proptiger on 8/1/16.
 */
public class PyrPageFragment extends Fragment {
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

        pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();

        // access group
        ArrayList<FilterGroup> grpsBuy = MasterDataCache.getInstance().getAllBuyPyrGroups();
        ArrayList<FilterGroup> grpsRent = MasterDataCache.getInstance().getAllRentPyrGroups();


        //User data prefill
        try{
            UserResponse userResponse = CookiePreferences.getLastUserInfo(getContext());
            mUserName.setText(userResponse.getData().firstName);
            mUserEmail.setText(userResponse.getData().email);
        }catch (Exception e){}

        try {
            boolean newGroups = false;
            if(mGroupsBuy == null || mGroupsRent == null) {
                newGroups = true;
                mGroupsBuy = getClonedFilterGroups(grpsBuy);
                mGroupsRent = getClonedFilterGroups(grpsRent);
            }

            pyrPagePresenter.setBuySelected(SerpObjects.isBuyContext(getActivity()));//pre-fill buy context
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
            ex.printStackTrace();
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
        boolean makeRequest=pyrPagePresenter.makePartialPyrRequest(getActivity() ,pyrRequest, (mIsBuySelected ? mGroupsBuy : mGroupsRent));

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

        if(makeRequest && pyrRequest.getSalesType().equals("buy")) {
            AgentService agentService = ((AgentService) (MakaanServiceFactory.getInstance().getService(AgentService.class)));
            agentService.getTopAgentsForLocality((long)pyrRequest.getCityId(), localities
                    , bedrooms, propertyTypes, 20, false, new TopBuyAgentsPyrCallback());
        }
        else if(makeRequest && pyrRequest.getSalesType().equals("rent")){
            ((AgentService) (MakaanServiceFactory.getInstance().getService(AgentService.class))).getTopAgentsForLocality((long)pyrRequest.getCityId(), localities,
                    bedrooms, propertyTypes, 20, true, new TopRentAgentsPyrCallback());
        }
    }

    @Subscribe
    public void onResults(TopRentAgentsPyrEvent topRentAgentsPyrEvent) {
        ArrayList<TopAgent> topAgentList = topRentAgentsPyrEvent.topAgents;
        Toast.makeText(getActivity(), "in Contact:" + topAgentList.size(), Toast.LENGTH_SHORT).show();
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
        ArrayList<TopAgent> topAgentList = topBuyAgentsPyrEvent.topAgents;
        Toast.makeText(getActivity(), "in Contact:" + topAgentList.size(), Toast.LENGTH_SHORT).show();
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
            mLocalityCount.setVisibility(View.VISIBLE);
            mLocalityCount.setText(String.valueOf(selectedProjectsList.size()));
        }
        else{
            mLocalityCount.setVisibility(View.GONE);
        }
    }

    public void setPropertyCount(){
        int count = 0;
        ArrayList<FilterGroup> grp = mIsBuySelected ? mGroupsBuy : mGroupsRent;
        for(FilterGroup group : grp) {
            String name = group.internalName;
            if("i_property_type".equals(name)) {
                for(TermFilter filter : group.termFilterValues) {
                    if(filter.selected) {
                        count++;
                    }
                }
            }
        }
        if(count>0){
            mPropertyTypeCount.setVisibility(View.VISIBLE);
            mPropertyTypeCount.setText(String.valueOf(count));
        }
        else{
            mPropertyTypeCount.setVisibility(View.GONE);
        }
    }

    public void setUserInfo(){
        mUserName.setText(pyrPagePresenter.getName());
        mUserEmail.setText(pyrPagePresenter.getEmail());
        mUserMobile.setText(pyrPagePresenter.getPhonNumber());
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
}

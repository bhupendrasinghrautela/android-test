package com.makaan.fragment.pyr;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.constants.RequestConstants;
import com.makaan.event.agents.TopBuyAgentsPyrEvent;
import com.makaan.event.agents.TopRentAgentsPyrEvent;
import com.makaan.event.agents.callback.TopBuyAgentsPyrCallback;
import com.makaan.response.agents.Agent;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.country.CountryCodeResponse;
import com.makaan.service.AgentService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private Integer mCountryId;
    private ArrayAdapter<String> mCountryAdapter;
    private List<String> mCountryNames;
    private List<CountryCodeResponse.CountryCodeData> mCountries;
    private static final int MOSTLY_USED_COUNTRIES = 7;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pyr_page_layout, container, false);
        ButterKnife.bind(this, view);

        initializeCountrySpinner();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        AppBus.getInstance().register(this);
    }

    @OnClick(R.id.post_requirements)
    public void postRequirements() {
        ArrayList<String> localities = new ArrayList<>();
        localities.add("50175");

        ArrayList<String> bedrooms = new ArrayList<>();
        bedrooms.add("2");
        bedrooms.add("3");

        ((AgentService) (MakaanServiceFactory.getInstance().getService(Agent.class))).getTopAgentsForLocality(2L, localities, bedrooms, RequestConstants.APARTMENT, 5, false, new TopBuyAgentsPyrCallback());
        //((AgentService) (MakaanServiceFactory.getInstance().getService(Agent.class))).getTopAgentsForLocality(2L, localities, bedrooms, RequestConstants.APARTMENT, 5, true, new TopBuyAgentsPyrCallback());


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

//        mSellerListingAdapter = new SellerListingAdapter(this,response.getData());
//        mSellerRecyclerView.setAdapter(mSellerListingAdapter);

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

//        mSellerListingAdapter = new SellerListingAdapter(this,response.getData());
//        mSellerRecyclerView.setAdapter(mSellerListingAdapter);

    }

    @Override
    public void onDetach() {
        AppBus.getInstance().unregister(this);
        super.onDetach();
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
}

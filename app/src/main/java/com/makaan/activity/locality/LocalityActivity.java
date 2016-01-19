package com.makaan.activity.locality;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.makaan.R;
import com.makaan.fragment.locality.LocalityPropertiesFragment;
import com.makaan.fragment.locality.NearByLocalitiesFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tusharchaudhary on 1/18/16.
 */
public class LocalityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locality);

        addNearByLocalitiesFragment();
        addTopAgentsFragment();
        addTopBuilders();
        addProperties();
    }

    private void addNearByLocalitiesFragment() {
        Fragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "nearby localities");
        bundle.putInt("placeholder", R.drawable.placeholder_localities_nearby);
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((NearByLocalitiesFragment)newFragment).setData(getDummyData());
    }


    private void addTopAgentsFragment() {
        Fragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "top agents");
        bundle.putInt("placeholder", R.drawable.placeholder_agent);
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_top_agents, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((NearByLocalitiesFragment)newFragment).setData(getDummyDataForAgents());
    }


    private void addTopBuilders() {
        Fragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "top builders");
        bundle.putInt("placeholder", R.drawable.placeholder_localities_builders);
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_top_builders, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((NearByLocalitiesFragment)newFragment).setData(getDummyDataForBuilders());
    }
    private void addProperties() {
        Fragment newFragment = new LocalityPropertiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "properties in electronic city");
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_props, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((LocalityPropertiesFragment)newFragment).setData(getDummyDataForProps());
    }

    private List<LocalityPropertiesFragment.Properties> getDummyDataForProps() {
        List<LocalityPropertiesFragment.Properties> properties = new ArrayList<>();
        LocalityPropertiesFragment.Properties property= new LocalityPropertiesFragment.Properties("","Luxury properties Rs 1.2 Cr onwards");
        properties.add(property);
        properties.add(property);
        properties.add(property);
        properties.add(property);
        properties.add(property);
        properties.add(property);
        return properties;
    }


    private List<NearByLocalitiesFragment.NearByLocalities> getDummyData() {
        List<NearByLocalitiesFragment.NearByLocalities> nearByLocalities = new ArrayList<>();
        NearByLocalitiesFragment.NearByLocalities nearby = new NearByLocalitiesFragment.NearByLocalities("","1090 +","102 +","median price: 2,400 / sq ft","Koramangla");
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);return nearByLocalities;
    }

    private List<NearByLocalitiesFragment.NearByLocalities> getDummyDataForAgents() {
        List<NearByLocalitiesFragment.NearByLocalities> nearByLocalities = new ArrayList<>();
        NearByLocalitiesFragment.NearByLocalities nearby = new NearByLocalitiesFragment.NearByLocalities("","109 +","552 +","investor clinic","Sachin Singh");
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        return nearByLocalities;
    }


    private List<NearByLocalitiesFragment.NearByLocalities> getDummyDataForBuilders() {
        List<NearByLocalitiesFragment.NearByLocalities> nearByLocalities = new ArrayList<>();
        NearByLocalitiesFragment.NearByLocalities nearby = new NearByLocalitiesFragment.NearByLocalities("","42 ","18","experience: 18 years","Supertech Group");
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);return nearByLocalities;
    }




}

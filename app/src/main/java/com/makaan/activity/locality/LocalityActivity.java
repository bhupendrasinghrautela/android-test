package com.makaan.activity.locality;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.event.project.OnSeeOnMapClicked;
import com.makaan.fragment.locality.LocalityFragment;
import com.makaan.fragment.neighborhood.NeighborhoodMapFragment;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.util.KeyUtil;
import com.squareup.otto.Subscribe;

/**
 * Created by tusharchaudhary on 1/18/16.
 */
public class LocalityActivity extends MakaanFragmentActivity {
    public static final String LOCALITY_ID = "localityId";
    private Long localityId;
    private LocalityFragment localityFragment;
    private NeighborhoodMapFragment mNeighborhoodMapFragment;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_locality;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocalityId();
        addLocalityFragment();
    }

    @Subscribe
    public void onResult(OnSeeOnMapClicked onSeeOnMapClicked){
        mNeighborhoodMapFragment = new NeighborhoodMapFragment();
        mNeighborhoodMapFragment.setData(onSeeOnMapClicked.amenityClusters);
        initFragment(R.id.container, mNeighborhoodMapFragment, true);
    }

    private void addLocalityFragment() {
        localityFragment = new LocalityFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KeyUtil.LOCALITY_ID,localityId);
        localityFragment.setArguments(bundle);
        initFragment(R.id.container, localityFragment,false);
    }

    private void setLocalityId() {
        Intent intent = getIntent();
        if(intent != null) {
            localityId = intent.getLongExtra(LOCALITY_ID, 50001); //use  50157 for electronic city
        }else{
            localityId = Long.valueOf(50001);
        }
    }

    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Override
    public String getScreenName() {
        return "Locality";
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}

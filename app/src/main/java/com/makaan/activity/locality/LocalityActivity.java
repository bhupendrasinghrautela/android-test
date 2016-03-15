package com.makaan.activity.locality;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.project.OnSeeOnMapClicked;
import com.makaan.fragment.locality.LocalityFragment;
import com.makaan.fragment.neighborhood.NeighborhoodMapFragment;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.jarvis.event.PageTag;
import com.makaan.util.KeyUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

/**
 * Created by tusharchaudhary on 1/18/16.
 */
public class LocalityActivity extends MakaanFragmentActivity {
    public static final String LOCALITY_ID = "localityId";
    private Long localityId;
    private LocalityFragment localityFragment;
    private NeighborhoodMapFragment mNeighborhoodMapFragment;
    private NeighborhoodMapFragment.EntityInfo mEntityInfo;
    private String mLocalityName;

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
        mNeighborhoodMapFragment.setData(mEntityInfo,onSeeOnMapClicked.amenityClusters);
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

    @Subscribe
    public void onExposeMessage(OnExposeEvent event) {
        if(null==event.message){
            return;
        }
        if(!TextUtils.isEmpty(mLocalityName)) {
            event.message.city = mLocalityName;
            displayPopupWindow(event.message);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.backToHome);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickLocality);
                finish();
                break;
        }
        return true;
    }

    @Subscribe
    public void onResults(LocalityByIdEvent localityByIdEvent) {
        if (null != localityByIdEvent && null != localityByIdEvent.locality) {

            PageTag pageTag = new PageTag();
            pageTag.addLocality(localityByIdEvent.locality.label);
            super.setCurrentPageTag(pageTag);

            if(localityByIdEvent.locality.latitude != null && localityByIdEvent.locality.longitude != null) {
                mEntityInfo = new NeighborhoodMapFragment.EntityInfo(localityByIdEvent.locality.label,
                        localityByIdEvent.locality.latitude,
                        localityByIdEvent.locality.longitude);
            }

            mLocalityName = localityByIdEvent.locality.label;
        }
    }

    @Override
    public void onBackPressed() {
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.backToHome);
        MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickCity);
        super.onBackPressed();
    }
}

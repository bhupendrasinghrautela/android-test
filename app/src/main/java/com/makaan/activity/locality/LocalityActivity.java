package com.makaan.activity.locality;

import android.content.Intent;
import android.os.Bundle;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.fragment.locality.LocalityFragment;

/**
 * Created by tusharchaudhary on 1/18/16.
 */
public class LocalityActivity extends MakaanFragmentActivity {
    private static final String LOCALITY_ID = "localityId";
    private Long localityId;
    private LocalityFragment localityFragment;

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

    private void addLocalityFragment() {
        localityFragment = new LocalityFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("localityId",localityId);
        localityFragment.setArguments(bundle);
        initFragment(R.id.container, localityFragment,false);
    }

    private void setLocalityId() {
        Intent intent = getIntent();
        if(intent != null) {
            localityId = intent.getLongExtra(LOCALITY_ID, 50157); //use  50157 for electronic city
        }else{
            localityId = Long.valueOf(50157);
        }
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }
}

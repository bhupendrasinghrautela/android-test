package com.makaan.fragment.project;

import com.makaan.R;
import com.makaan.event.project.OpenPyrClicked;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.util.AppBus;

import butterknife.OnClick;

/**
 * Created by aishwarya on 04/03/16.
 */
public class NoPropertiesFragment extends MakaanBaseFragment {
    @OnClick(R.id.open_pyr_fragment)
    void pyrOpen(){
        if(getArguments()!=null){
            boolean isRent = getArguments().getBoolean("isRent");
            AppBus.getInstance().post(new OpenPyrClicked(isRent));
        }
    }
    @Override
    protected int getContentViewId() {
        return R.layout.no_property_listed;
    }
}

package com.makaan.fragment.project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.ui.project.ProjectConfigItemView;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/28/16.
 */
public class ProjectConfigFragment extends MakaanBaseFragment{
    @Bind(R.id.project_config_item_view)
    ProjectConfigItemView projectConfigItemView;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_project_config_pager_item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        projectConfigItemView.bindView(getArguments().<ProjectConfigItem>getParcelableArrayList("configs"), projectConfigItemView,getArguments().getBoolean("isRent"));
        return view;
    }
}

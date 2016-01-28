package com.makaan.fragment.project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.pojo.SpecificaitonsUI;
import com.makaan.ui.project.ProjectSpecificationItemView;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ProjectSpecificationPagerFragment extends MakaanBaseFragment{
    @Bind(R.id.project_specification_item_view)
    ProjectSpecificationItemView projectSpecificationItemView;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_project_specification_pager_item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        projectSpecificationItemView.bindView(getArguments().<SpecificaitonsUI>getParcelableArrayList("specs"),projectSpecificationItemView);
        return view;
    }
}

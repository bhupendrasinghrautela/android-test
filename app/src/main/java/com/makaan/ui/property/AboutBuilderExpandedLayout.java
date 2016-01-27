package com.makaan.ui.property;

import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.project.Builder;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.ui.ExpandableLinearLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by aishwarya on 24/01/16.
 */
public class AboutBuilderExpandedLayout extends BaseLinearLayout<Builder> {

    @Bind(R.id.builder_logo)
    FadeInNetworkImageView mBuilderLogo;

    @Bind(R.id.builder_expandable)
    ExpandableLinearLayout mExpandableLayout;

    private boolean isExpanded = false;

    @OnClick(R.id.about_builder)
    public void onChange(){
        isExpanded = !isExpanded;
        if(isExpanded){
            mExpandableLayout.expand();
        }
        else{
            mExpandableLayout.collapse();
        }
    }

    public AboutBuilderExpandedLayout(Context context) {
        super(context);
    }

    public AboutBuilderExpandedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AboutBuilderExpandedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Builder item) {
        if(item != null) {
            mBuilderLogo.setImageUrl(item.imageURL, MakaanNetworkClient.getInstance().getImageLoader());
        }
    }
}

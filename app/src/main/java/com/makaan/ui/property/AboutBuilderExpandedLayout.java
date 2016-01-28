package com.makaan.ui.property;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

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

    @Bind(R.id.about_builder)
    TextView aboutBuilderTv;

    private boolean isExpanded = false;

    @OnClick(R.id.about_builder)
    public void onChange(){
        isExpanded = !isExpanded;
        if(isExpanded){
            mExpandableLayout.expand();
            aboutBuilderTv.setText(getResources().getString(R.string.less_about_builder));
        }
        else{
            mExpandableLayout.collapse();
            aboutBuilderTv.setText(getResources().getString(R.string.more_about_builder));
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
        aboutBuilderTv.setText(getResources().getString(R.string.more_about_builder));
        if(item != null) {
            mBuilderLogo.setImageUrl(item.imageURL, MakaanNetworkClient.getInstance().getImageLoader());
        }
    }
}

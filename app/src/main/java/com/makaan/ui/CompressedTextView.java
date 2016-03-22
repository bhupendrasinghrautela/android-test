package com.makaan.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.jarvis.BaseJarvisActivity;
import com.segment.analytics.Properties;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by aishwarya on 08/01/16.
 */
public class CompressedTextView extends BaseLinearLayout<String> {

    @Nullable
    @Bind(R.id.header_text)
    TextView mHeaderText;
    @Nullable
    @Bind(R.id.content_text)
    TextView mContentText;
    @Nullable
    @Bind(R.id.read_more)
    TextView mReadMore;
    private Boolean isCollapsed = true;
    private static int MAX_LINE = 5;
    private boolean workedOnce = false;
    @OnClick(R.id.read_more) void click(){
        workedOnce = true;
        if (isCollapsed) {
            if (OverviewActivity.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity) mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.descriptionMore);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);
            } else if (OverviewActivity.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity) mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.descriptionMore);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectOverView);
            } else if (OverviewActivity.SCREEN_NAME_CITY.equalsIgnoreCase(((BaseJarvisActivity) mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.descriptionMore);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickCityOverView);
            } else if (OverviewActivity.SCREEN_NAME_LOCALITY.equalsIgnoreCase(((BaseJarvisActivity) mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.descriptionMore);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickLocalityOverView);
            }

            mReadMore.setText(mContext.getString(R.string.read_less));
            mContentText.setMaxLines(Integer.MAX_VALUE);
        } else {
            if (OverviewActivity.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity) mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.descriptionLess);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);
            } else if (OverviewActivity.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity) mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.descriptionLess);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectOverView);
            } else if (OverviewActivity.SCREEN_NAME_CITY.equalsIgnoreCase(((BaseJarvisActivity) mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.descriptionLess);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickCityOverView);
            } else if (OverviewActivity.SCREEN_NAME_LOCALITY.equalsIgnoreCase(((BaseJarvisActivity) mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.descriptionLess);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickLocalityOverView);
            }

            mReadMore.setText(mContext.getString(R.string.read_more));
            mContentText.setMaxLines(MAX_LINE);
        }
        isCollapsed=!isCollapsed;
    }

    public void setMaxLines(int line){
        MAX_LINE = line;
    }

    public CompressedTextView(Context context) {
        super(context);
    }

    public CompressedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompressedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
    @Override
    public void bindView(String item) {

    }

    public void removeMore(boolean shouldHide) {
        if(!workedOnce) {
            if (shouldHide) {
                mReadMore.setVisibility(GONE);
            } else {
                mReadMore.setVisibility(VISIBLE);
            }
        }
    }
}

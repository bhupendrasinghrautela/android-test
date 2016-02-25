package com.makaan.activity.lead;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.project.ProjectFragment;
import com.segment.analytics.Properties;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadCallNowFragment extends MakaanBaseFragment {

     LeadFormPresenter mLeadFormPresenter;
    @Bind(R.id.tv_seller_name)
    TextView mTextViewSellerName;
    @Bind(R.id.seller_ratingbar)
    RatingBar mRatingBarSeller;
    @Bind(R.id.btn_call)
    Button mButtonCall;
    @Override
    protected int getContentViewId() {
        return R.layout.layout_lead_call_now;
    }

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_lead_call_now, container, false);
        ButterKnife.bind(this, view);
        return view;
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLeadFormPresenter= LeadFormPresenter.getLeadFormPresenter();
        mTextViewSellerName.setText(mLeadFormPresenter.getName());
        mRatingBarSeller.setRating(Float.valueOf(mLeadFormPresenter.getScore()));
        if(mLeadFormPresenter.getPhone()!=null) {
            mButtonCall.setText("call +91" + PhoneNumberUtils.formatNumber(mLeadFormPresenter.getPhone()));
        }
        else{
            mButtonCall.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_call)
    void callClick(){
        Bundle bundle =getArguments();
        if(bundle!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.callNow);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickSerpCallConnect);
        }
        else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.callNow);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectCallConnect);
        }
        else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.callNow);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyCallConnect);
        }
        //TODO remove this hardcoded number
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + mLeadFormPresenter.getPhone()));
        startActivity(intent);
    }

    @OnClick(R.id.tv_get_callback)
    void getCallBackClick(){
        Bundle bundle =getArguments();
        if(bundle!=null && bundle.getString("source").equalsIgnoreCase(SerpActivity.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.getCallBackFromSeller);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickSerpCallConnect);
        }
        else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(ProjectFragment.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.getCallBackFromSeller);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectCallConnect);
        }
        else if(bundle!=null && bundle.getString("source").equalsIgnoreCase(PropertyDetailFragment.class.getName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.getCallBackFromSeller);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyCallConnect);
        }
        LeadFormPresenter.getLeadFormPresenter().showLeadInstantCallBackFragment();
    }
}

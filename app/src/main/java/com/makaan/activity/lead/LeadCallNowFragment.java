package com.makaan.activity.lead;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;

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
        mButtonCall.setText("call +91"+mLeadFormPresenter.getPhone());
    }

    @OnClick(R.id.btn_call)
    void callClick(){
        //TODO remove this hardcoded number
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + mLeadFormPresenter.getPhone()));
        startActivity(intent);
    }

    @OnClick(R.id.tv_get_callback)
    void getCallBackClick(){
        LeadFormPresenter.getLeadFormPresenter().showLeadInstantCallBackFragment();
    }
}

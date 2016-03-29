package com.makaan.activity.lead;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.util.ImageUtils;
import com.makaan.util.PermissionManager;
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
    @Bind(R.id.iv_seller_name)
    TextView mSellerNameProfileText;
    @Bind(R.id.iv_seller_image_call_now)
    de.hdodenhof.circleimageview.CircleImageView mSellerImage;
    private boolean mAlreadyLoaded=false;

    @Override
    protected int getContentViewId() {
        return R.layout.layout_lead_call_now;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLeadFormPresenter= LeadFormPresenter.getLeadFormPresenter();
        mTextViewSellerName.setText(mLeadFormPresenter.getName().toLowerCase());
        mRatingBarSeller.setRating(Float.valueOf(mLeadFormPresenter.getScore()));
        if(mLeadFormPresenter.getPhone()!=null) {
            mButtonCall.setText("call " + PhoneNumberUtils.formatNumber(mLeadFormPresenter.getPhone()));
        }
        else{
            if(mLeadFormPresenter.getId()!=null && !TextUtils.isEmpty(mLeadFormPresenter.getId()) && !mAlreadyLoaded){
                Properties properties= MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorUsability);
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",
                        MakaanTrackerConstants.Label.leadForm, mLeadFormPresenter.getId()));
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorNa);
                mAlreadyLoaded=true;
            }
            mButtonCall.setText("na");
            mButtonCall.setClickable(false);
        }
        setSellerImage();
    }

    @OnClick(R.id.btn_call)
    void callClick(){
        Bundle bundle =getArguments();
        if(bundle!=null && SerpActivity.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.callNow);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickSerpCallConnect);
        }
        else if(bundle!=null && ProjectFragment.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.callNow);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectCallConnect);
        }
        else if(bundle!=null && PropertyDetailFragment.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.callNow);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyCallConnect);
        }
        //TODO remove this hardcoded number
        if(PermissionManager.isPermissionRequestRequired(getActivity(), Manifest.permission.CALL_PHONE)) {
            PermissionManager.begin().addRequest(PermissionManager.CALL_PHONE_REQUEST).request(getActivity());
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                    + mLeadFormPresenter.getPhone()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.tv_share_your_deatils)
    void getCallBackClick(){
        Bundle bundle =getArguments();
        if(bundle!=null && SerpActivity.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.getCallBackFromSeller);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickSerpCallConnect);
        }
        else if(bundle!=null && ProjectFragment.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.getCallBackFromSeller);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectCallConnect);
        }
        else if(bundle!=null && PropertyDetailFragment.class.getName().equalsIgnoreCase(bundle.getString("source"))) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.getCallBackFromSeller);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyCallConnect);
        }
        LeadFormPresenter.getLeadFormPresenter().showLeadLaterCallBAckFragment();
    }

    public void setSellerImage() {
        mSellerNameProfileText.setVisibility(View.GONE);
        mSellerImage.setVisibility(View.VISIBLE);
        int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
        if (null != mLeadFormPresenter.getSellerImageUrl()) {
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(mLeadFormPresenter.getSellerImageUrl(),
                    width, height, false), new CustomImageLoaderListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    if(isFragmentVisible()) {
                        mSellerImage.setVisibility(View.VISIBLE);
                        mSellerNameProfileText.setVisibility(View.INVISIBLE);
                        mSellerImage.setImageBitmap(imageContainer.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    super.onErrorResponse(volleyError);
                    if(isFragmentVisible()) {
                        mSellerImage.setVisibility(View.INVISIBLE);
                        mSellerNameProfileText.setVisibility(View.VISIBLE);
                        mSellerNameProfileText.setText(mLeadFormPresenter.getName());
                    }
                }
            });
        }
        else {
            mSellerImage.setVisibility(View.INVISIBLE);
            mSellerNameProfileText.setVisibility(View.VISIBLE);
            mSellerNameProfileText.setText(mLeadFormPresenter.getName());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode & PermissionManager.CALL_PHONE_REQUEST)
                == PermissionManager.CALL_PHONE_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + mLeadFormPresenter.getPhone()));
                startActivity(intent);
            } else if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // TODO show message or something
            }
        }
    }

}

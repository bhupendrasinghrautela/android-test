package com.makaan.fragment.buyerJourney;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.request.buyerjourney.AgentRating;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.JsonBuilder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/17/16.
 */
public class ReviewAgentFragment extends MakaanBaseFragment {
    @Bind(R.id.fragment_review_agent_image_view)
    ImageView mAgentImageView;

    @Bind(R.id.fragment_review_agent_name_text_view)
    TextView mAgentNameTextView;
    @Bind(R.id.fragment_review_agent_type_text_view)
    TextView mAgentTypeTextView;
    @Bind(R.id.fragment_review_agent_review_text_view)
    TextView mAgentReviewTextView;
    @Bind(R.id.fragment_review_agent_logo_text_view)
    TextView mLogoTextView;

    @Bind(R.id.fragment_review_agent_listed_property_toggle_button)
    ToggleButton mListedPropertyToggleButton;
    @Bind(R.id.fragment_review_agent_unreachable_toggle_button)
    ToggleButton mUnreachableToggleButton;
    @Bind(R.id.fragment_review_agent_poor_knowledge_toggle_button)
    ToggleButton mPoorKnowledgeToggleButton;
    @Bind(R.id.fragment_review_agent_unprofessional_behaviour_toggle_button)
    ToggleButton mUnprofessionalBehaviourToggleButton;
    @Bind(R.id.fragment_review_agent_other_toggle_button)
    ToggleButton mOtherToggleButton;
    private ClientCompanyLeadFragment.ClientCompanyLeadsObject mObj;

    @Bind(R.id.fragment_review_agent_rating_bar)
    RatingBar mRatingBar;

    @Bind(R.id.fragment_review_agent_comment_edit_text)
    EditText mCommentEditText;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_review_agent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(mObj == null || mObj.clientLeadObject == null || mObj.clientLeadObject.company == null
                || mObj.clientLeadObject.company.name == null) {
            // TODO
            return null;
        }
        mAgentNameTextView.setText(mObj.clientLeadObject.company.name.toLowerCase());
        mAgentImageView.setVisibility(View.GONE);

        if(mObj.clientLeadObject.company.name != null) {
            mLogoTextView.setText(String.valueOf(mObj.clientLeadObject.company.name.charAt(0)));

            mLogoTextView.setVisibility(View.VISIBLE);

            int[] bgColorArray = getResources().getIntArray(R.array.bg_colors);

            Random random = new Random();
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mLogoTextView.setBackground(drawable);
            } else {
                mLogoTextView.setBackgroundDrawable(drawable);
            }
        }

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch((int)rating) {
                    case 1:
                        mAgentReviewTextView.setText("it was worst");
                        break;
                    case 2:
                        mAgentReviewTextView.setText("it was bad");
                        break;
                    case 3:
                        mAgentReviewTextView.setText("it was okay");
                        break;
                    case 4:
                        mAgentReviewTextView.setText("it was good");
                        break;
                    case 5:
                        mAgentReviewTextView.setText("it was awesome");
                        break;
                }
            }
        });

        return view;
    }

    @OnClick({R.id.fragment_review_agent_listed_property_relative_layout,
            R.id.fragment_review_agent_unreachable_relative_layout,
            R.id.fragment_review_agent_poor_knowledge_relative_layout,
            R.id.fragment_review_agent_unprofessional_behaviour_relative_layout,
            R.id.fragment_review_agent_other_relative_layout})
    void onToggleClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_review_agent_listed_property_relative_layout:
                mListedPropertyToggleButton.setChecked(!mListedPropertyToggleButton.isChecked());
                break;
            case R.id.fragment_review_agent_unreachable_relative_layout:
                mUnreachableToggleButton.setChecked(!mUnreachableToggleButton.isChecked());
                break;
            case R.id.fragment_review_agent_poor_knowledge_relative_layout:
                mPoorKnowledgeToggleButton.setChecked(!mPoorKnowledgeToggleButton.isChecked());
                break;
            case R.id.fragment_review_agent_unprofessional_behaviour_relative_layout:
                mUnprofessionalBehaviourToggleButton.setChecked(!mUnprofessionalBehaviourToggleButton.isChecked());
                break;
            case R.id.fragment_review_agent_other_relative_layout:
                mOtherToggleButton.setChecked(!mOtherToggleButton.isChecked());
                break;
        }
    }

    public void setData(Object obj) {
        if(obj instanceof ClientCompanyLeadFragment.ClientCompanyLeadsObject) {
            mObj = (ClientCompanyLeadFragment.ClientCompanyLeadsObject)obj;
        }
    }

    @OnClick(R.id.fragment_review_agent_next_button)
    void onNextClicked(View view) {
        if(mOtherToggleButton.isChecked() && TextUtils.isEmpty(mCommentEditText.getText().toString())) {
            mCommentEditText.setText("enter some data");
        } else if(getActivity() instanceof BuyerDashboardCallbacks) {
            if(mObj != null) {
                /*((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_REVIEW_AGENT,
                        true, null, "cashback request", mObj);*/
                if(mRatingBar.getRating() < 1) {
                    // TODO
                } else {
                    AgentRating rating = new AgentRating();
                    if(mObj.clientLeadObject != null && mObj.clientLeadObject.company != null
                            && mObj.clientLeadObject.company.id != null) {
                        rating.sellerId = mObj.clientLeadObject.company.id;
                    }
                    rating.rating = (int)mRatingBar.getRating();
                    if(mObj.listingDetail != null) {
                        if(mObj.listingDetail.id != null) {
                            rating.listingId = mObj.listingDetail.id;
                        }
                        if(mObj.listingDetail.property != null && mObj.listingDetail.property.project != null && mObj.listingDetail.property.project.locality != null) {
                            rating.localityName = mObj.listingDetail.property.project.locality.label;
                            if(mObj.listingDetail.property.project.locality.suburb != null
                                    && mObj.listingDetail.property.project.locality.suburb.city != null
                                    && mObj.listingDetail.property.project.locality.suburb.city.id != null) {
                                rating.cityId = mObj.listingDetail.property.project.locality.suburb.city.id;
                            }
                        }
                    }
                    if(!TextUtils.isEmpty(mCommentEditText.getText().toString())) {
                        rating.reviewComment = mCommentEditText.getText().toString();
                    }

                    if(mListedPropertyToggleButton.isChecked()) {
                        if(rating.sellerRatingParameters == null) {
                            rating.sellerRatingParameters = new ArrayList<>();
                        }
                        rating.sellerRatingParameters.add(rating.new SellerRatingParameter(1));
                    }
                    if(mUnreachableToggleButton.isChecked()) {
                        if(rating.sellerRatingParameters == null) {
                            rating.sellerRatingParameters = new ArrayList<>();
                        }
                        rating.sellerRatingParameters.add(rating.new SellerRatingParameter(2));
                    }
                    if(mPoorKnowledgeToggleButton.isChecked()) {
                        if(rating.sellerRatingParameters == null) {
                            rating.sellerRatingParameters = new ArrayList<>();
                        }
                        rating.sellerRatingParameters.add(rating.new SellerRatingParameter(3));
                    }
                    if(mUnprofessionalBehaviourToggleButton.isChecked()) {
                        if(rating.sellerRatingParameters == null) {
                            rating.sellerRatingParameters = new ArrayList<>();
                        }
                        rating.sellerRatingParameters.add(rating.new SellerRatingParameter(4));
                    }
                    if(mOtherToggleButton.isChecked()) {
                        if(rating.sellerRatingParameters == null) {
                            rating.sellerRatingParameters = new ArrayList<>();
                        }
                        rating.sellerRatingParameters.add(rating.new SellerRatingParameter(5));
                    }

                    try {
                        ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class))
                                .postSellerRating(JsonBuilder.toJson(rating));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((BuyerDashboardCallbacks)getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_UPLOAD_DOCUMENTS, true, null, null, mObj);
                }
            }
        }
    }
}

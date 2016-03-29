package com.makaan.fragment.buyerJourney;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.request.buyerjourney.AgentRating;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonBuilder;

import org.json.JSONException;

import java.util.ArrayList;

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

    @Bind(R.id.fragment_review_agent_feedback_scroll_view)
    View mFeedbackScrollView;

    @Bind(R.id.fragment_review_agent_rating_bar)
    RatingBar mRatingBar;

    @Bind(R.id.fragment_review_agent_comment_edit_text)
    EditText mCommentEditText;

    @Bind(R.id.fragment_review_agent_top_layout)
    View mReviewTopLayout;

    private ClientCompanyLeadFragment.ClientCompanyLeadsObject mObj;

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

        if(!TextUtils.isEmpty(mObj.clientLeadObject.company.name)) {
            mLogoTextView.setText(String.valueOf(mObj.clientLeadObject.company.name.charAt(0)));
        }

        mLogoTextView.setVisibility(View.VISIBLE);

//            int[] bgColorArray = getResources().getIntArray(R.array.bg_colors);

//            Random random = new Random();
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//            drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
        drawable.getPaint().setColor(CommonUtil.getColor(mObj.clientLeadObject.company.name, getContext()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mLogoTextView.setBackground(drawable);
        } else {
            mLogoTextView.setBackgroundDrawable(drawable);
        }

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mReviewTopLayout.getLayoutParams();
                switch((int)rating) {
                    case 1:
                        mFeedbackScrollView.setVisibility(View.VISIBLE);
                        mAgentReviewTextView.setText("it was worst");

                        params.addRule(RelativeLayout.ALIGN_TOP);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                        mReviewTopLayout.setLayoutParams(params);
                        break;
                    case 2:
                        mFeedbackScrollView.setVisibility(View.VISIBLE);
                        mAgentReviewTextView.setText("it was bad");

                        params.addRule(RelativeLayout.ALIGN_TOP);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                        mReviewTopLayout.setLayoutParams(params);
                        break;
                    case 3:
                        mFeedbackScrollView.setVisibility(View.VISIBLE);
                        mAgentReviewTextView.setText("it was okay");

                        params.addRule(RelativeLayout.ALIGN_TOP);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                        mReviewTopLayout.setLayoutParams(params);
                        break;
                    case 4:
                        mFeedbackScrollView.setVisibility(View.GONE);
                        mAgentReviewTextView.setText("it was good");

                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        params.addRule(RelativeLayout.ALIGN_TOP, 0);
                        mReviewTopLayout.setLayoutParams(params);
                        break;
                    case 5:
                        mFeedbackScrollView.setVisibility(View.GONE);
                        mAgentReviewTextView.setText("it was awesome");

                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        params.addRule(RelativeLayout.ALIGN_TOP, 0);
                        mReviewTopLayout.setLayoutParams(params);
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
        if(mOtherToggleButton.isChecked() && mRatingBar.getRating() <= 3 && TextUtils.isEmpty(mCommentEditText.getText().toString())) {
            mCommentEditText.setError("please enter specific feedback");
        } else if(getActivity() instanceof BuyerDashboardCallbacks) {
            if(mObj != null) {
                /*((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_REVIEW_AGENT,
                        true, null, "cashback request", mObj);*/
                if(mRatingBar.getRating() < 1) {
                    // TODO
                } else {
                    AgentRating rating = new AgentRating();
                    Long sellerId = null;
                    if(mObj.clientLeadObject != null && mObj.clientLeadObject.company != null
                            && mObj.clientLeadObject.company.id != null) {
                        rating.sellerId = mObj.clientLeadObject.company.id;
                        sellerId=rating.sellerId;
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

                    if(rating.rating <= 3) {
                        if (mListedPropertyToggleButton.isChecked()) {
                            if (rating.sellerRatingParameters == null) {
                                rating.sellerRatingParameters = new ArrayList<>();
                            }
                            rating.sellerRatingParameters.add(rating.new SellerRatingParameter(1));
                        }
                        if (mUnreachableToggleButton.isChecked()) {
                            if (rating.sellerRatingParameters == null) {
                                rating.sellerRatingParameters = new ArrayList<>();
                            }
                            rating.sellerRatingParameters.add(rating.new SellerRatingParameter(2));
                        }
                        if (mPoorKnowledgeToggleButton.isChecked()) {
                            if (rating.sellerRatingParameters == null) {
                                rating.sellerRatingParameters = new ArrayList<>();
                            }
                            rating.sellerRatingParameters.add(rating.new SellerRatingParameter(3));
                        }
                        if (mUnprofessionalBehaviourToggleButton.isChecked()) {
                            if (rating.sellerRatingParameters == null) {
                                rating.sellerRatingParameters = new ArrayList<>();
                            }
                            rating.sellerRatingParameters.add(rating.new SellerRatingParameter(4));
                        }
                        if (mOtherToggleButton.isChecked()) {
                            if (rating.sellerRatingParameters == null) {
                                rating.sellerRatingParameters = new ArrayList<>();
                            }
                            rating.sellerRatingParameters.add(rating.new SellerRatingParameter(5));
                        }
                    }

                    try {
                        ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class))
                                .postSellerRating(JsonBuilder.toJson(rating), getActivity(), mRatingBar.getRating(),
                                        mCommentEditText.getText().toString(), sellerId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((BuyerDashboardCallbacks)getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_UPLOAD_DOCUMENTS, true, null, null, mObj);
                }
            }
        }
    }
}

package com.makaan.fragment.buyerJourney;

import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.fragment.MakaanBaseFragment;

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

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_review_agent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(mObj != null) {
            mAgentNameTextView.setText(mObj.clientLeadObject.company.name);
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
        // TODO we are not checking any details filled by user
        if(getActivity() instanceof BuyerDashboardCallbacks) {
            if(mObj != null) {
                ((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_REVIEW_AGENT,
                        true, null, "cashback request", mObj);
            }
        }
    }

    class AgentRating {
        long sellerId;
        int rating;
        long listingId;
        String listingName;
        String localityName;
        long cityId;
        String reviewComment;
        ArrayList<SellerRatingParameter> sellerRatingParameters;
        class SellerRatingParameter {
            int id;
        }
    }
}

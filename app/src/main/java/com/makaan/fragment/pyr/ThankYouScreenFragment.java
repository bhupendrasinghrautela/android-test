package com.makaan.fragment.pyr;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by proptiger on 22/1/16.
 */
public class ThankYouScreenFragment extends Fragment {
    public static final String IS_DASHBOARD = "is_pyr";

    @Nullable
    @Bind(R.id.hurray)
    TextView mHurrayTextView;

    @Nullable
    @Bind(R.id.congratulation_message)
    TextView mCongratulationTextView;

    @Nullable
    @Bind(R.id.message)
    TextView mMessageTextView;

    @Nullable
    @Bind(R.id.seller_name)
    TextView mSellerNameTextView;

    @Nullable
    @Bind(R.id.thank_you_continue)
    TextView mThankYouContinueButton;

    private boolean makaanAssist=false;
    private boolean noSellers=false;
    TextView mSellerName;
    TextView mAssistMessage;
    private boolean mIsDashboard = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if(getArguments() != null) {
            mIsDashboard = getArguments().getBoolean(IS_DASHBOARD);
            if(!mIsDashboard) {
                makaanAssist = getArguments().getBoolean(PyrPagePresenter.MAKAAN_ASSIST_VALUE);
                noSellers = getArguments().getBoolean(PyrPagePresenter.NO_SELLERS_FRAGMENT);
                if (makaanAssist) {
                    view = inflater.inflate(R.layout.thank_you_screen_makaan_assist, container, false);
                    mAssistMessage = (TextView) view.findViewById(R.id.makaan_assist_mesg);
                } else {
                    view = inflater.inflate(R.layout.thank_you_screen_makaan, container, false);
                    mSellerName = (TextView) view.findViewById(R.id.seller_name);
                }
            } else {
                view = inflater.inflate(R.layout.thank_you_screen_makaan, container, false);
            }
        }
        ButterKnife.bind(this, view);
        if(mIsDashboard) {
            if(mHurrayTextView != null) {
                mHurrayTextView.setVisibility(View.GONE);
            }
            if(mSellerNameTextView != null) {
                mSellerNameTextView.setVisibility(View.GONE);
            }
            if(mCongratulationTextView != null) {
                mCongratulationTextView.setText("congratulations for finding your dream home !!");
            }
            if(mMessageTextView != null) {
                mMessageTextView.setText("makaan customer support team will get in touch with you shortly for your cash back request.");
            }
            if(mThankYouContinueButton != null) {
                mThankYouContinueButton.setText("close");
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!mIsDashboard) {
            PyrPagePresenter pyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
            if (pyrPagePresenter.getNonMakaanAssistCount() == 1 && mSellerName != null) {
                mSellerName.setText(pyrPagePresenter.getSellerName());
            }
            if (noSellers) {
                mAssistMessage.setText(getResources().getString(R.string.no_sellers_assist_string));
            }
        }

    }

    @OnClick(R.id.thank_you_continue)
    public void onContinueClick(){
        if(getActivity() != null) {
            if (mIsDashboard) {
                getActivity().finish();
            } else {
                getActivity().onBackPressed();
            }
        }
    }

}

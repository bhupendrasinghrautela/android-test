package com.makaan.activity.buyerJourney;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.fragment.WebViewFragment;
import com.makaan.fragment.buyerJourney.BlogContentFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/29/16.
 */
public class BuyerDashboardActivity extends MakaanFragmentActivity implements BuyerDashboardCallbacks {
    @Bind(R.id.activity_base_buyer_journey_layout_type_text_view)
    TextView mTitleTextView;

    public static final int LOAD_FRAGMENT_WEB_VIEW = 0x01;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_base_buyer_journey;
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFragment(R.id.activity_base_buyer_journey_content_frame_layout, new BlogContentFragment(), false);
    }

    @Override
    public void loadFragment(int type, boolean shouldAddToBackStack, Bundle data) {
        switch (type) {
            case LOAD_FRAGMENT_WEB_VIEW:
                WebViewFragment fragment = new WebViewFragment();
                if(data != null) {
                    fragment.setArguments(data);
                }
                initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                break;
        }
    }


    @OnClick(R.id.activity_base_buyer_journey_layout_back_button)
    public void onBackPressed(View view) {
        onBackPressed();
    }

    /**
     * set title of the activity
     * @param title title to display in the toolbar*
     */
    protected void setTitle(String title) {
        mTitleTextView.setText(title);
    }
}

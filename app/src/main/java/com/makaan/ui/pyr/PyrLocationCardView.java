package com.makaan.ui.pyr;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.ui.listing.BaseCardView;
import com.makaan.fragment.pyr.PyrPagePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by proptiger on 6/1/16.
 */
public class PyrLocationCardView extends BaseCardView {
    @Bind(R.id.location_value)TextView mLocatioValue;
    PyrPagePresenter pyrPagePresenter;
    public PyrLocationCardView(Context context) {
        super(context);
    }

    public PyrLocationCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PyrLocationCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, Object item) {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.location_value)
    public void onPropertyClick(){
        pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
        pyrPagePresenter.showPropertySearchFragment();
    }
}

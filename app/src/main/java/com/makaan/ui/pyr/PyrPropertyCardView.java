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
public class PyrPropertyCardView extends BaseCardView {
    @Bind(R.id.property_value)TextView mPropertyValue;
    PyrPagePresenter pyrPagePresenter;
    private Context mContext;

    public PyrPropertyCardView(Context context) {
        super(context);
        mContext=context;
    }

    public PyrPropertyCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public PyrPropertyCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(Context context, Object item) {

    }

    @OnClick(R.id.select_property_layout)
    public void onPropertyClick(){
        pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
        pyrPagePresenter.showPropertyTypeFragment();
    }

}

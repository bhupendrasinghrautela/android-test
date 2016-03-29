package com.makaan.ui.locality;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by proptiger on 17/1/16.
 */
public class LocalityApartmentsView extends LinearLayout {
    @Bind(R.id.apartment_row_one_bhk)TextView mRowOneBhk;
    @Bind(R.id.apartment_row_one_price)TextView mRowOnePrice;
    @Bind(R.id.apartment_row_one_area)TextView mRowOneArea;
    @Bind(R.id.apartment_row_one_price_per_sqft)TextView mRowOnePriceSqft;
    @Bind(R.id.apartment_row_two_bhk)TextView mRowTwoBhk;
    @Bind(R.id.apartment_row_two_price)TextView mRowTwoPrice;
    @Bind(R.id.apartment_row_two_area)TextView mRowTwoArea;
    @Bind(R.id.apartment_row_two_price_per_sqft)TextView mRowTwoPriceSqft;
    @Bind(R.id.apartment_row_three_bhk)TextView mRowThreeBhk;
    @Bind(R.id.apartment_row_three_price)TextView mRowThreePrice;
    @Bind(R.id.apartment_row_three_area)TextView mRowThreeArea;
    @Bind(R.id.apartment_row_three_price_per_sqft)TextView mRowThreePriceSqft;

    public LocalityApartmentsView(Context context) {
        super(context);
    }

    public LocalityApartmentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalityApartmentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindApartmentData(){

    }

}

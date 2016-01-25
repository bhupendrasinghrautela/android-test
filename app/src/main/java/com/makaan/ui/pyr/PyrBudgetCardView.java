package com.makaan.ui.pyr;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.response.serp.RangeFilter;
import com.makaan.ui.listing.BaseCardView;
import com.makaan.fragment.pyr.PyrPagePresenter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by proptiger on 6/1/16.
 */
public class PyrBudgetCardView extends BaseCardView {
    @Bind(R.id.budget_range_seek_bar)RangeSeekBar mBudgetSeekBar;
    @Bind(R.id.min_budget)TextView mMinBudget;
    @Bind(R.id.max_budget)TextView mMaxBudget;
    @Bind(R.id.budget_layout)LinearLayout mBudgetLayout;
    public static final long MIN_BUDGET = 0l;
    public static final long MAX_BUY_BUDGET = 100000000l;
    public static final long MAX_RENT_BUDGET = 500000l;
    private PyrPagePresenter pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
    private double rentConvert[] = {0,0.005,0.01,0.015,0.020,0.025,0.030,0.035,0.040,0.045,0.05, 0.055, 0.06, 0.065,0.07, 0.075, 0.080, 0.085, 0.090, 0.095, 0.10,
                                    0.12, 0.14 ,0.16, 0.18, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.5,1.0};
    private double buyConvert[] = {0,0.005,0.01,0.015,0.020,0.025,0.030,0.035,0.040,0.045,0.05, 0.06,0.07,0.08,0.09, 0.1, 0.12, 0.14, 0.16, 0.18, 0.2, 0.3, 0.4 ,0.5, 1.0};
    private RangeFilter mRangeFilter;


    public PyrBudgetCardView(Context context) {
        super(context);
    }

    public PyrBudgetCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PyrBudgetCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        pyrPagePresenter.setBudgetCardViewInstance(this);
    }

    @Override
    public void bindView(Context context, Object item) {

    }

    private void initBudgetSeekbar() {
        mBudgetSeekBar.setInitialValues((long) mRangeFilter.minValue, (long) mRangeFilter.maxValue);


        mBudgetSeekBar.setSelectedMinValue(mRangeFilter.selectedMinValue);
        mBudgetSeekBar.setSelectedMaxValue(mRangeFilter.selectedMaxValue);

        setMinMaxBudgetValues((long) mRangeFilter.selectedMinValue, (long) mRangeFilter.selectedMaxValue);


        mBudgetSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minBudgetValue,
                                                    Long maxBudgetValue) {
                setMinMaxBudgetValues(minBudgetValue, maxBudgetValue);
            }
        });

        mBudgetSeekBar.setNotifyWhileDragging(true);

    }

    public void setMinMaxBudgetValues(Long minBudget, Long maxBudget){
        mRangeFilter.selectedMinValue = minBudget;
        mRangeFilter.selectedMaxValue = maxBudget;
        mMinBudget.setText(pyrPagePresenter.getPrice((long)mRangeFilter.selectedMinValue));
        mMaxBudget.setText(pyrPagePresenter.getPrice((long)mRangeFilter.selectedMaxValue));
    }

    public void setMinMaxBudgetPosition(Float leftThumbPosition, Float rightThumbPosition){
        if(leftThumbPosition!=0 && rightThumbPosition!=0){
            if(leftThumbPosition.equals(rightThumbPosition)){
                mMinBudget.setX(leftThumbPosition);
                mMaxBudget.setX(leftThumbPosition);
            }
            else{
                mMinBudget.setX(leftThumbPosition);
                mMaxBudget.setX(rightThumbPosition - 30);
            }
        }
    }


    public void setValues(ArrayList<RangeFilter> rangeFilterValues) {

        mBudgetSeekBar.setStepValues(buyConvert);
        mBudgetSeekBar.setStepCount(25);
        mRangeFilter = rangeFilterValues.get(0);
        initBudgetSeekbar();
    }
}

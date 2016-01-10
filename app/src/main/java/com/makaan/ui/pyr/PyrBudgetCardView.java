package com.makaan.ui.pyr;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.ui.listing.BaseCardView;
import com.makaan.fragment.pyr.PyrPagePresenter;

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
    private static final long MIN_BUDGET = 0l;
    private static final long MAX_BUDGET = 100000000l;
    private PyrPagePresenter pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();

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
        initBudgetSeekbar();
    }

    @Override
    public void bindView(Context context, Object item) {

    }

    private void initBudgetSeekbar() {
        mBudgetSeekBar.setInitialValues(MIN_BUDGET, MAX_BUDGET);
        setMinMaxBudgetValues(MIN_BUDGET, MAX_BUDGET);
        mBudgetSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minBudgetValue,
                                                    Long maxBudgetValue) {
                setMinMaxBudgetValues(minBudgetValue, maxBudgetValue);
                setMinMaxBudgetPosition(bar.getLeftThumbPosition(), bar.getRightThumbPosition());
            }
        });

        mBudgetSeekBar.setNotifyWhileDragging(true);

    }

    public void setMinMaxBudgetValues(Long minBudget, Long maxBudget){
        mMinBudget.setText(pyrPagePresenter.getPrice(minBudget));
        mMaxBudget.setText(pyrPagePresenter.getPrice(maxBudget));
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



}

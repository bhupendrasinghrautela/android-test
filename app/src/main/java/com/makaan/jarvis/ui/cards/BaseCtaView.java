package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.ButterKnife;

/**
 * Created by sunil on 27/01/16.
 */
public abstract class BaseCtaView<D> extends LinearLayout {

    public interface OnApplyClickListener{
        void onApplyClick();
    }

    public interface OnCancelClickListener{
        void onCancelClick();
    }

    protected OnApplyClickListener mOnApplyClickListener;
    protected OnCancelClickListener mOnCancelClickListener;

    public BaseCtaView(Context context) {
        super(context);
    }

    public BaseCtaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCtaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setOnApplyClickListener(OnApplyClickListener onApplyClickListener){
        mOnApplyClickListener = onApplyClickListener;
    }

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener){
        mOnCancelClickListener = onCancelClickListener;
    }

    public abstract void bindView(Context context, D item);
}

package com.makaan.ui.pyr;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by proptiger on 7/1/16.
 */
public class PyrPageView extends LinearLayout {
    @Bind(R.id.buy_rent_radiogroup) RadioGroup buyRent;
    @Bind(R.id.buy) RadioButton buy;
    @Bind(R.id.rent) RadioButton rent;
    @Bind(R.id.pyr_page_name)EditText mClientName;
    @Bind(R.id.leadform_mobileno_edittext)EditText mClientMobile;
    @Bind(R.id.pyr_page_email)EditText mClientEmail;

    private Context mContext;

    public PyrPageView(Context context) {
        super(context);
        mContext=context;
    }

    public PyrPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public PyrPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @OnCheckedChanged(R.id.buy) void buyCheckChange(RadioButton buyButton){
        LinearLayout.LayoutParams params;
        if(!buyButton.isChecked()){
            params = new LinearLayout.LayoutParams(buyButton.getWidth() ,(int) dpToPixel(mContext, 32));
            buyButton.setLayoutParams(params);
        }
        else{
            params = new LinearLayout.LayoutParams(buyButton.getWidth() , (int) dpToPixel(mContext, 35));
            buyButton.setLayoutParams(params);
        }

    }

    @OnCheckedChanged(R.id.rent) void rentCheckChange(RadioButton sellButton) {
        LinearLayout.LayoutParams params;
        if(!sellButton.isChecked()){
            params = new LinearLayout.LayoutParams(sellButton.getWidth() ,(int) dpToPixel(mContext, 33));
            sellButton.setLayoutParams(params);
        }
        else{
            params = new LinearLayout.LayoutParams(sellButton.getWidth(), (int) dpToPixel(mContext, 35));
            sellButton.setLayoutParams(params);
        }
    }

    public static float dpToPixel(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

}

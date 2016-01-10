package com.makaan.ui.pyr;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.makaan.R;
import com.makaan.ui.listing.BaseCardView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by proptiger on 8/1/16.
 */
public class PyrPropertySearchCardView extends BaseCardView {

    public PyrPropertySearchCardView(Context context) {
        super(context);
    }

    public PyrPropertySearchCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PyrPropertySearchCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, Object item) {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

}

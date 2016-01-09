package com.makaan.ui.listing;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by sunil on 07/12/15.
 */
public abstract class BaseCardView<D> extends CardView {

    public BaseCardView(Context context) {
        super(context);
    }

    public BaseCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * This method binds this card view with data D
     *
     * @param context
     * @param item
     * */
    public abstract void bindView(Context context, D item);
}

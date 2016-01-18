package com.makaan.ui.listing;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.adapter.listing.CustomAbstractHorizontalScrollViewAdapter;


/**
 * Created by aishwarya on 05/12/15.
 */
public class CustomHorizontalScrollViewContainer extends LinearLayout {

    private Context mContext;
    private String mScrollViewHeader;
    private TextView mHeaderTextView;
    private CustomHorizontalScrollView mCustomHorizontalScrollView;
    private CustomAbstractHorizontalScrollViewAdapter mCustomAdapter;
    private static final int TEXT_SIZE = 22;
    
    public CustomHorizontalScrollViewContainer(Context context) {
        this(context,null);
    }

    public CustomHorizontalScrollViewContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHorizontalScrollViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = mContext.obtainStyledAttributes(
                attrs, R.styleable.customContainer, defStyleAttr, 0);
        mScrollViewHeader = a.getString(R.styleable.customContainer_cont_text);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.setOrientation(VERTICAL);
        initViews();
    }

    private void initViews() {
        if(mScrollViewHeader != null && !TextUtils.isEmpty(mScrollViewHeader)) {
            mHeaderTextView = new TextView(mContext);
            mHeaderTextView.setText(mScrollViewHeader);
            mHeaderTextView.setTextSize(TEXT_SIZE);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,0,0,20);
            this.addView(mHeaderTextView,lp);
        }
        mCustomHorizontalScrollView = new CustomHorizontalScrollView(mContext);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        this.addView(mCustomHorizontalScrollView,lp);
    }

    /**
     * @param adapter A CustomAbstractHorizontalScrollViewAdapter which will be used to get the list
     *                of views and inflate it inside of a linearlayout.
     */
    public void setAdapter(CustomAbstractHorizontalScrollViewAdapter adapter){
        if(mCustomHorizontalScrollView != null){
            mCustomHorizontalScrollView.setAdapter(adapter);
            mCustomAdapter = adapter;
        }
    }

    public CustomAbstractHorizontalScrollViewAdapter getAdapter(){
        return mCustomAdapter;
    }
}

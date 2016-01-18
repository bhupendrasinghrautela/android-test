package com.makaan.ui.listing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.makaan.adapter.listing.CustomAbstractHorizontalScrollViewAdapter;

import java.util.List;

/**
 * Created by aishwarya on 02/12/15.
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {

    private CustomAbstractHorizontalScrollViewAdapter mAdapter;
    private LinearLayout mBaseLinearLayout;
    private Context mContext;

    public CustomHorizontalScrollView(Context context) {
        this(context,null);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    /**
     * @param adapter A CustomAbstractHorizontalScrollViewAdapter which will be used to get the list
     *                of views and inflate it inside of a linearlayout.
     */
    public void setAdapter(CustomAbstractHorizontalScrollViewAdapter adapter){
        this.mAdapter = adapter;
        addViewsFromAdapter();
    }

    public void addViewDynamically(View view){
        if(mBaseLinearLayout != null){
            mBaseLinearLayout.addView(view);
        }
    }

    public void removeViewDynamically(View view){
        if(mBaseLinearLayout != null){
            mBaseLinearLayout.removeView(view);
        }
    }

    public View getFirstChild(){
        if(mBaseLinearLayout.getChildCount()>0){
            return mBaseLinearLayout.getChildAt(0);
        }
        else{
            return null;
        }
    }

    /**
     * adding the base LinearLayout and the list of views inside LinearLayout
     */
    private void init() {
        addBaseLayout();
    }

    private void addBaseLayout() {
        mBaseLinearLayout = new LinearLayout(mContext);
        this.addView(mBaseLinearLayout);
    }

    private void addViewsFromAdapter() {
        List<View> viewList = mAdapter.getAllViews();
        for(View view : viewList){
            if(mBaseLinearLayout == null) {
                addBaseLayout();
            }
            mBaseLinearLayout.addView(view);
        }
    }

}

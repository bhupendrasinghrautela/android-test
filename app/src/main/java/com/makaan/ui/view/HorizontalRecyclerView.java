package com.makaan.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.makaan.util.CommonUtil;

/**
 * Created by tusharchaudhary on 1/22/16.
 */
public class HorizontalRecyclerView extends RecyclerView implements View.OnTouchListener {

    public HorizontalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                CommonUtil.TLog("Event","down");
                break;

            case MotionEvent.ACTION_CANCEL:
                CommonUtil.TLog("Event","cancel");
                break;

            case MotionEvent.ACTION_UP:
                CommonUtil.TLog("Event","up");
                break;
        }
        v.onTouchEvent(event);
        return true;
    }

}

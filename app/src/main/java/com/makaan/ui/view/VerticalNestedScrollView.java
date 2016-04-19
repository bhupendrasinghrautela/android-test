package com.makaan.ui.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.makaan.util.CommonUtil;

/**
 * Created by tusharchaudhary on 1/22/16.
 */
public class VerticalNestedScrollView extends NestedScrollView {
    private float xDistance, yDistance, lastX, lastY;

    public VerticalNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                CommonUtil.TLog("ScrollEvent", "down");
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                CommonUtil.TLog("ScrollEvent", "move");
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if (xDistance > yDistance)
                    return false;
        }

        return super.onInterceptTouchEvent(ev);
    }
}

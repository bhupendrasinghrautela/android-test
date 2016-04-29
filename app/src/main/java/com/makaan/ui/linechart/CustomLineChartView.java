package com.makaan.ui.linechart;

import android.content.Context;
import android.util.AttributeSet;

import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by rohitgarg on 4/21/16.
 */
public class CustomLineChartView extends LineChartView {
    public CustomLineChartView(Context context) {
        super(context);
    }

    public CustomLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        touchHandler = new TouchHandler(context, this);
    }

    public void destroyView() {
        if(touchHandler != null && touchHandler instanceof TouchHandler) {
            ((TouchHandler)touchHandler).destroyView();
        }
        chartRenderer = null;
        touchHandler = null;
    }
}

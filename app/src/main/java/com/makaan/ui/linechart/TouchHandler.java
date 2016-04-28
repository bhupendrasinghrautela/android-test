package com.makaan.ui.linechart;

import android.content.Context;

import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.view.Chart;

/**
 * Created by rohitgarg on 4/21/16.
 */
public class TouchHandler extends ChartTouchHandler {
    public TouchHandler(Context context, Chart chart) {
        super(context, chart);
    }

    public void destroyView() {
        if(gestureDetector != null) {
            gestureDetector = null;
        }
        if(scaleGestureDetector != null) {
            scaleGestureDetector = null;
        }
    }
}

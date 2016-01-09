package com.makaan.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class DashedTextView extends TextView {
    Paint paint;
    public DashedTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setARGB(255, 255, 0,0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
    }

    public DashedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if(width > 0 && height > 0) {
            canvas.drawLine(0, height - 2, width, height - 2, paint);
        }
    }
}

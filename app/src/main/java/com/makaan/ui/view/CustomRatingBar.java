package com.makaan.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RatingBar;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;

/**
 * Created by rohitgarg on 1/25/16.
 */
public class CustomRatingBar extends RatingBar {

    private static int DEFAULT_ITEM_WIDTH = 36;
    private static int DEFAULT_ITEM_HEIGHT = 36;
    private static int DEFAULT_ITEM_HORIZONTAL_MARGIN_LEFT = 0;
    private static int DEFAULT_ITEM_HORIZONTAL_MARGIN_RIGHT = 6;
    private int mItemHeight;
    private int mItemWidth;
    private int mItemHorizontalMarginLeft;
    private int mItemHorizontalMarginRight;
    private int mProgressDrawable;
    private int mIntermediateProgressDrawable;

    // TODO need to cache processed bitmap
    private Bitmap mProgressBitmap;
    private Bitmap mScaledProgressBitmap;
    private Bitmap mIntermediateProgressBitmap;
    private Bitmap mScaledIntermediateProgressBitmap;
    private Paint mFilterBitmapPaint;
    private Paint mEmptyPaint;

    public CustomRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CustomRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mFilterBitmapPaint = new Paint();
        mFilterBitmapPaint.setFilterBitmap(true);
        mEmptyPaint = new Paint();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.RatingBar, 0, 0);

        try {
            mItemHeight = a.getDimensionPixelSize(R.styleable.RatingBar_itemHeight, DEFAULT_ITEM_WIDTH);
            mItemWidth = a.getDimensionPixelSize(R.styleable.RatingBar_itemWidth, DEFAULT_ITEM_WIDTH);
            mItemHorizontalMarginLeft = a.getDimensionPixelSize(R.styleable.RatingBar_itemMarginLeft, DEFAULT_ITEM_WIDTH);
            mItemHorizontalMarginRight = a.getDimensionPixelSize(R.styleable.RatingBar_itemMarginRight, DEFAULT_ITEM_WIDTH);

            mProgressDrawable = a.getResourceId(R.styleable.RatingBar_itemProgressIcon, 0);
            mIntermediateProgressDrawable = a.getResourceId(R.styleable.RatingBar_itemSecondaryProgressIcon, 0);
        } finally {
            a.recycle();
        }

        String savedId = String.valueOf(mProgressDrawable);

        Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap(savedId);
        if (bitmap != null) {
            mProgressBitmap = bitmap;
        } else {
            Bitmap b = BitmapFactory.decodeResource(getResources(), mProgressDrawable);
            mProgressBitmap = b;
            MakaanBuyerApplication.bitmapCache.putBitmap(savedId, b);
        }
        mScaledProgressBitmap = Bitmap.createScaledBitmap(mProgressBitmap, mItemWidth, mItemHeight, true);

        savedId = String.valueOf(mIntermediateProgressDrawable);
        bitmap = MakaanBuyerApplication.bitmapCache.getBitmap(savedId);
        if (bitmap != null) {
            mIntermediateProgressBitmap = bitmap;
        } else {
            Bitmap b = BitmapFactory.decodeResource(getResources(), mIntermediateProgressDrawable);
            mIntermediateProgressBitmap = b;
            MakaanBuyerApplication.bitmapCache.putBitmap(savedId, b);
        }
        mScaledIntermediateProgressBitmap = Bitmap.createScaledBitmap(mIntermediateProgressBitmap, mItemWidth, mItemHeight, true);
    }

    public CustomRatingBar(Context context) {
        super(context);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
            int stars = getNumStars();
        float rating = getRating();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        for (int i = 1; i <= stars; i++) {
            Bitmap bitmap = null;
            Resources res = getResources();
            canvas.translate(mItemHorizontalMarginLeft, 0);

            if (i <= Math.floor(rating)) {
                canvas.drawBitmap(mScaledProgressBitmap, 0, 0, mEmptyPaint);
                canvas.save();
            } else if(i > Math.ceil(rating)) {
                canvas.drawBitmap(mScaledIntermediateProgressBitmap, 0, 0, mEmptyPaint);
                canvas.save();
            } else {
                // calculate filled drawable width
                int right = (int) (mItemWidth * (rating - (i - 1)));

                // width and height of single item
                int targetWidth  = mItemWidth;
                int targetHeight = mItemHeight;

                // scale bitmap for scaling source bitmap to this bitmap
                Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

                // clip rectangle to crop filled drawable to required width
                RectF rectf = new RectF(0, 0, right, targetHeight);

                // use canvas with clipped path to draw on target bitmap
                Canvas c = new Canvas(targetBitmap);
                Path path = new Path();
                path.addRect(rectf, Path.Direction.CW);
                c.clipPath(path);

                // draw bitmap with required height and width
                c.drawBitmap(mProgressBitmap, new Rect(0, 0, mProgressBitmap.getWidth(), mProgressBitmap.getHeight()),
                        new Rect(0, 0, targetWidth, targetHeight), mFilterBitmapPaint);

                // use matrix with 100% width and height scale
                Matrix matrix = new Matrix();
                matrix.postScale(1f, 1f);
                // draw target bitmap on different bitmap
                Bitmap resizedBitmap = Bitmap.createBitmap(targetBitmap, 0, 0, targetWidth, targetHeight, matrix, true);

                // draw on rating bar's canvas
                canvas.drawBitmap(resizedBitmap, 0, 0, mEmptyPaint);

                // no need to translate canvas, because clipping will handle it
//                canvas.translate(right, 0);

                // scale bitmap for scaling source bitmap to this bitmap
                targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

                // clip rectangle to crop filled drawable to required width
                rectf = new RectF(right, 0, targetWidth, targetHeight);

                // use canvas with clipped path to draw on target bitmap
                c = new Canvas(targetBitmap);
                path = new Path();
                path.addRect(rectf, Path.Direction.CW);
                c.clipPath(path);

                // draw bitmap with required height and width
                c.drawBitmap(mIntermediateProgressBitmap, new Rect(0, 0, mIntermediateProgressBitmap.getWidth(), mIntermediateProgressBitmap.getHeight()),
                        new Rect(0, 0, targetWidth, targetHeight), mFilterBitmapPaint);


                // use matrix with 100% width and height scale
                matrix = new Matrix();
                matrix.postScale(1f, 1f);
                // draw target bitmap on different bitmap
                resizedBitmap = Bitmap.createBitmap(targetBitmap, 0, 0, targetWidth, targetHeight, matrix, true);

                // draw on rating bar's canvas
                canvas.drawBitmap(resizedBitmap, 0, 0, mEmptyPaint);

                canvas.save();
            }
            // translate canvas with item width
            canvas.translate(mItemWidth, 0);
            canvas.translate(mItemHorizontalMarginRight, 0);
        }
    }
}

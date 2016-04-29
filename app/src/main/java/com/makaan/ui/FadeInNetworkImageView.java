package com.makaan.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by rohitgarg on 4/29/16.
 */
public class FadeInNetworkImageView extends ImageView {
    private FadeInNetworkImageView.ResponseObserver mObserver;
    private boolean isFullScreen = false;
    private static final int FADE_IN_TIME_MS = 250;
    private String mUrl;
    private int mDefaultImageId;
    private int mErrorImageId;
    private ImageLoader mImageLoader;
    private ImageLoader.ImageContainer mImageContainer;

    public void setResponseObserver(FadeInNetworkImageView.ResponseObserver observer) {
        this.mObserver = observer;
    }

    public FadeInNetworkImageView(Context context) {
        super(context);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageBitmap(Bitmap bm) {
        TransitionDrawable td = new TransitionDrawable(new Drawable[]{new ColorDrawable(17170445), new BitmapDrawable(this.getContext().getResources(), bm)});
        this.setImageDrawable(td);
        td.startTransition(250);
    }

    public void setImageUrl(String url, ImageLoader imageLoader) {
        this.mUrl = url;
        this.mImageLoader = imageLoader;
        this.loadImageIfNecessary(false);
    }

    public void setFullScreenScrollable() {
        this.isFullScreen = true;
    }

    public void setDefaultImageResId(int defaultImage) {
        this.mDefaultImageId = defaultImage;
    }

    public void setErrorImageResId(int errorImage) {
        this.mErrorImageId = errorImage;
    }

    void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = this.getWidth();
        int height = this.getHeight();
        boolean wrapWidth = false;
        boolean wrapHeight = false;
        if(this.getLayoutParams() != null) {
            wrapWidth = this.getLayoutParams().width == -2;
            wrapHeight = this.getLayoutParams().height == -2;
        }

        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if(width != 0 || height != 0 || isFullyWrapContent) {
            if(TextUtils.isEmpty(this.mUrl)) {
                if(this.mImageContainer != null) {
                    this.mImageContainer.cancelRequest();
                    this.mImageContainer = null;
                }

                this.setDefaultImageOrNull();
            } else {
                if(this.mImageContainer != null && this.mImageContainer.getRequestUrl() != null) {
                    if(this.mImageContainer.getRequestUrl().equals(this.mUrl)) {
                        return;
                    }

                    this.mImageContainer.cancelRequest();
                    this.setDefaultImageOrNull();
                }

                int maxWidth = wrapWidth?0:width;
                int maxHeight = wrapHeight?0:height;
                ImageLoader.ImageContainer newContainer = this.mImageLoader.get(this.mUrl, new ImageLoader.ImageListener() {
                    public void onErrorResponse(VolleyError error) {
                        if(FadeInNetworkImageView.this.mErrorImageId != 0) {
                            FadeInNetworkImageView.this.setImageResource(FadeInNetworkImageView.this.mErrorImageId);
                        }

                        if(FadeInNetworkImageView.this.mObserver != null) {
                            FadeInNetworkImageView.this.mObserver.onError();
                        }

                    }

                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        if(isImmediate && isInLayoutPass) {
                            FadeInNetworkImageView.this.post(new Runnable() {
                                public void run() {
                                    onResponse(response, false);
                                    if(FadeInNetworkImageView.this.mObserver != null) {
                                        FadeInNetworkImageView.this.mObserver.onSuccess();
                                    }

                                }
                            });
                        } else {
                            if(response.getBitmap() != null) {
                                FadeInNetworkImageView.this.setImageBitmap(response.getBitmap());
                            } else if(FadeInNetworkImageView.this.mDefaultImageId != 0) {
                                FadeInNetworkImageView.this.setImageResource(FadeInNetworkImageView.this.mDefaultImageId);
                            }

                        }
                    }
                }, maxWidth, maxHeight);
                this.mImageContainer = newContainer;
            }
        }
    }

    private void setDefaultImageOrNull() {
        if(this.mDefaultImageId != 0) {
            this.setImageResource(this.mDefaultImageId);
        } else if(!this.isFullScreen) {
            this.setImageBitmap((Bitmap)null);
        }

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.loadImageIfNecessary(true);
    }

    protected void onDetachedFromWindow() {
        if(this.mImageContainer != null) {
            this.mImageContainer.cancelRequest();
            this.setImageBitmap((Bitmap)null);
            this.mImageContainer = null;
        }

        super.onDetachedFromWindow();
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.invalidate();
    }

    public interface ResponseObserver {
        void onError();

        void onSuccess();
    }
}

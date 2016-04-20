package com.makaan.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by sunil on 08/12/15.
 *
 * LruBitmap cache to be used for
 * Volley ImageLoader
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageLoader.ImageCache {
    public static int getDefaultLruCacheSize(int sizePortion) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        return maxMemory / sizePortion;
    }

    public LruBitmapCache(int sizePortion) {
        super(getDefaultLruCacheSize(sizePortion));
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if(null!=url && null !=bitmap) {
            put(url, bitmap);
        }
    }
}

package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.content.BlogByTagEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.response.content.BlogItem;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by vaibhav on 23/01/16.
 */
public class BlogService implements MakaanService {

    /**
     * http://marketplace-qa.makaan-ws.com/data/v1/entity/blog?tag=Home&sourceDomain=Makaan
     *
     * @param tag
     */
    public void getBlogs(final String tag) {

        String blogUrl = ApiConstants.BLOG_URL.concat("?tag=").concat(tag);
        Type type = new TypeToken<ArrayList<BlogItem>>() {
        }.getType();
        MakaanNetworkClient.getInstance().get(blogUrl, type, new ObjectGetCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                ArrayList<BlogItem> blogItems = (ArrayList<BlogItem>) responseObject;
                AppBus.getInstance().post(new BlogByTagEvent(tag, blogItems));
            }
        }, true);

    }
}

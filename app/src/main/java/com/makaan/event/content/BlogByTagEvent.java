package com.makaan.event.content;

import com.makaan.response.content.BlogItem;

import java.util.ArrayList;

/**
 * Created by vaibhav on 25/01/16.
 */
public class BlogByTagEvent {

    public String  tag;
    public ArrayList<BlogItem> blogItems;

    public BlogByTagEvent(String tag, ArrayList<BlogItem> blogItems) {
        this.tag = tag;
        this.blogItems = blogItems;
    }


    public BlogByTagEvent() {
    }
}

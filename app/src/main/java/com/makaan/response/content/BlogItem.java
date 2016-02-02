package com.makaan.response.content;

import java.util.HashMap;

/**
 * Created by vaibhav on 23/01/16.
 */
public class BlogItem {

    public Long id;
    public String postTitle;
    public String postContent, username;
    public String postDate, epochPostDate;
    public String primaryImageUrl;
    public String guid;

    public HashMap<String, ContentImage> imageSizeMap;
}

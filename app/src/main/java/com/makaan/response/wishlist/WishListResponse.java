package com.makaan.response.wishlist;

import com.makaan.response.BaseResponse;
import com.makaan.response.project.Project;

import java.util.List;

/**
 * Created by sunil on 25/01/16.
 */
public class WishListResponse extends BaseResponse {
    public int totalCount;
    public List<WishList> data;
}

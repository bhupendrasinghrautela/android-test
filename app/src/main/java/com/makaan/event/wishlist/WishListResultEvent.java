package com.makaan.event.wishlist;

import com.makaan.response.BaseEvent;
import com.makaan.response.wishlist.WishListResponse;

/**
 * Created by sunil on 25/01/16.
 */
public class WishListResultEvent extends BaseEvent {
    public int requestMethod = -1;
    public WishListResponse wishListResponse;
}

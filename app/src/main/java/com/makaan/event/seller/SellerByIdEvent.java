package com.makaan.event.seller;

import com.makaan.event.MakaanEvent;
import com.makaan.response.project.CompanySeller;

/**
 * Created by vaibhav on 22/01/16.
 */
public class SellerByIdEvent extends MakaanEvent{

    public CompanySeller seller;

    public SellerByIdEvent(CompanySeller seller) {
        this.seller = seller;
    }

    public SellerByIdEvent() {
    }
}

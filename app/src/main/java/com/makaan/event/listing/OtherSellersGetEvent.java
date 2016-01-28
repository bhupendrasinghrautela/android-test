package com.makaan.event.listing;

import com.makaan.event.MakaanEvent;
import com.makaan.pojo.SellerCard;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vaibhav on 25/01/16.
 */
public class OtherSellersGetEvent extends MakaanEvent {

    public ArrayList<SellerCard> sellerCards = new ArrayList<>();

    public OtherSellersGetEvent(ArrayList<SellerCard> sellerCards) {
        this.sellerCards = sellerCards;
    }

    public OtherSellersGetEvent(Collection<SellerCard> values) {
        sellerCards.addAll(values);
    }

    public OtherSellersGetEvent() {
    }


}

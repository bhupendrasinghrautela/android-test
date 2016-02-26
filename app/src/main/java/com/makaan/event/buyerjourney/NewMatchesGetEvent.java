package com.makaan.event.buyerjourney;

import com.makaan.event.MakaanEvent;
import com.makaan.response.buyerjourney.NewMatch;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/26/16.
 */
public class NewMatchesGetEvent extends MakaanEvent {
    public int totalCount;
    public ArrayList<NewMatch> data;
}

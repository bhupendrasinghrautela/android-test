package com.makaan.event.buyerjourney;

import com.makaan.event.MakaanEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rohitgarg on 2/26/16.
 */
public class NewMatchesGetEvent extends MakaanEvent {
    public int totalCount;
    public HashMap<String, Integer> data;
}

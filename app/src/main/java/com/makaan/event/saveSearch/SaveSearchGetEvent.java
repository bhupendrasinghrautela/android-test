package com.makaan.event.saveSearch;

import com.makaan.response.saveSearch.SaveSearch;

import java.util.ArrayList;

/**
 * Created by aishwarya on 26/01/16.
 */
public class SaveSearchGetEvent {
    public ArrayList<SaveSearch> saveSearchArrayList;

    public SaveSearchGetEvent(ArrayList<SaveSearch> saveSearchGetEvent) {
        this.saveSearchArrayList = saveSearchGetEvent;
    }

    public SaveSearchGetEvent() {
    }
}

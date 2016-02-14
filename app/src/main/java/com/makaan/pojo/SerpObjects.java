package com.makaan.pojo;

import android.content.Context;

import com.makaan.MakaanBuyerApplication;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.constants.PreferenceConstants;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.FilterGroup;
import com.makaan.util.Preference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rohitgarg on 2/3/16.
 */
public class SerpObjects {
    public Selector selector;
    public ArrayList<FilterGroup> filterGroups;
    public boolean isBuy;

    public SerpObjects getSerpObject(Context context) {
        return MakaanBuyerApplication.serpObjects.get(context.hashCode());
    }

    public static void putSerpObject(Context context, SerpObjects obj) {
        MakaanBuyerApplication.serpObjects.put(context.hashCode(), obj);
    }

    public static void removeSerpObject(Context context) {
        MakaanBuyerApplication.serpObjects.remove(context.hashCode());
    }

    public static Selector getSerpSelector(Context context) {
        SerpObjects obj = MakaanBuyerApplication.serpObjects.get(context.hashCode());
        if(obj != null) {
            return obj.selector;
        }
        return null;
    }

    public static ArrayList<FilterGroup> getFilterGroups(Context context) {
        SerpObjects obj = MakaanBuyerApplication.serpObjects.get(context.hashCode());
        if(obj != null) {
            return obj.filterGroups;
        }
        return null;
    }

    public static int getAppliedFilterCount(Context context) {
        SerpObjects obj = MakaanBuyerApplication.serpObjects.get(context.hashCode());
        if(obj != null) {
            return getAppliedFilterCount(obj.filterGroups);
        }
        return 0;
    }

    public static int getAppliedFilterCount(ArrayList<FilterGroup> filterGroups) {
        int count = 0;
        for (FilterGroup grp : filterGroups) {
            if (grp.isSelected) {
                count++;
            }
        }
        return count;
    }

    public static boolean isBuyContext(Context context) {
        SerpObjects obj = MakaanBuyerApplication.serpObjects.get(context.hashCode());
        if(obj != null) {
            return obj.isBuy;
        }
        return (Preference.getInt(context.getSharedPreferences(PreferenceConstants.PREF, Context.MODE_PRIVATE),
                PreferenceConstants.PREF_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_BUY) == MakaanBaseSearchActivity.SERP_CONTEXT_BUY);
    }

    public static Set<String> getSelectedFilterNames(Context context){
        SerpObjects obj = MakaanBuyerApplication.serpObjects.get(context.hashCode());
        if(null!=obj &&  null!=obj.filterGroups) {
            Set<String> selectedFilterNames = new HashSet();
            for(FilterGroup filterGroup : obj.filterGroups){
                if(filterGroup.isSelected){
                    selectedFilterNames.add(filterGroup.internalName);
                }
            }
            return selectedFilterNames;
        }
        return null;
    }
}

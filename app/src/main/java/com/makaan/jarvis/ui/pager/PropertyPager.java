package com.makaan.jarvis.ui.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.jarvis.message.ChatObject;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.MessageType;
import com.makaan.util.JsonBuilder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 01/02/16.
 */
public class PropertyPager extends ViewPager {

    private PropertyPagerAdapter mPropertyPagerAdapter;

    public PropertyPager(Context context) {
        super(context);
    }


    public PropertyPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void bindView(Context context, Object content){
        JSONArray jsonArray = null;
        try {
            jsonArray = JsonBuilder.toJsonArray(content);
        } catch (Exception e){
            Crashlytics.logException(e);
        }

        if (jsonArray==null) {
            setVisibility(View.GONE);
            return;
        }

        List<Property> listings = MakaanBuyerApplication.gson.fromJson(jsonArray.toString(), new TypeToken<List<Property>>() {
        }.getType());

        if (listings == null || listings.isEmpty()) {
            setVisibility(View.GONE);
            return;
        }
        mPropertyPagerAdapter = new PropertyPagerAdapter(context);
        setAdapter(mPropertyPagerAdapter);
        setClipToPadding(false);
        setPageMargin(context.getResources().getDimensionPixelSize(R.dimen.map_listing_view_pager_page_gap));
        setPadding(context.getResources().getDimensionPixelSize(R.dimen.map_listing_view_pager_page_left_padding),
                0, 70,
                context.getResources().getDimensionPixelSize(R.dimen.map_listing_view_pager_page_bottom_padding));
        setOffscreenPageLimit(2);
        mPropertyPagerAdapter.setData(getTranslatedData(listings));

    }

    private List<Message> getTranslatedData(List<Property> listings){
        List<Message> properties = new ArrayList<>();
        for(Property property : listings){
            Message message = new Message();
            message.chatObj = new ChatObject();
            message.messageType = MessageType.propertyOverview;
            message.chatObj.image = property.property.project.imageURL;
            message.chatObj.locality = property.property.project.builder.name + " " + property.property.project.name;
            message.chatObj.area = property.property.project.locality.label + " " + property.property.project.locality.suburb.city.label;
            message.chatObj.propertyId = property.id;
            if(null!=property.currentListingPrice){
                message.chatObj.price = property.currentListingPrice.price;
            }
            properties.add(message);
        }

        return properties;
    }
}

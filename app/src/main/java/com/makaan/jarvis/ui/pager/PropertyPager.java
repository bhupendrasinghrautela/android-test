package com.makaan.jarvis.ui.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.makaan.R;
import com.makaan.adapter.listing.ListingPagerAdapter;
import com.makaan.jarvis.message.ChatObject;
import com.makaan.jarvis.message.Message;
import com.makaan.response.listing.Listing;

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


    public void bindView(Context context, List<Property> listings){
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
            message.chatObj.image = property.property.project.imageURL;
            message.chatObj.builderName = property.property.project.builder.name;
            message.chatObj.projectName = property.property.project.name;
            message.chatObj.cityName = property.property.project.locality.label;
            message.chatObj.localityName = property.property.project.locality.suburb.city.label;
            properties.add(message);
        }

        return properties;
    }
}

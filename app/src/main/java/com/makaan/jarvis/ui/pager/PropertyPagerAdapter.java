package com.makaan.jarvis.ui.pager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.ui.cards.PropertyCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 01/02/16.
 */
public class PropertyPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<Message> mPropertyList = new ArrayList<>();
    private int realCount;

    public PropertyPagerAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Message> list){
        mPropertyList.clear();
        mPropertyList.addAll(list);
        setItemCount(mPropertyList.size());
        this.notifyDataSetChanged();
    }

    private void setItemCount(int size) {
        realCount = size;
    }

    @Override
    public int getCount() {
        return realCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view =
                mLayoutInflater.inflate(R.layout.jarvis_card_property, null);

        PropertyCard propertyCard = (PropertyCard) view;
        propertyCard.bindView(mContext, mPropertyList.get(position));
        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

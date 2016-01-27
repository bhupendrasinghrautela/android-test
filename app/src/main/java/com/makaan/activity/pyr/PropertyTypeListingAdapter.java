package com.makaan.activity.pyr;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.makaan.R;
import com.makaan.fragment.pyr.PropertyTypeFragment;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.response.serp.TermFilter;

import java.util.ArrayList;

/**
 * Created by makaanuser on 6/1/16.
 */
public class PropertyTypeListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    ArrayList<TermFilter> listPropertyType;
    PyrPagePresenter pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
    PropertyTypeFragment propertyTypeFragment;

    public PropertyTypeListingAdapter(Context mContext , ArrayList<TermFilter> listPropertyType, PropertyTypeFragment propertyTypeFragment) {
        this.mContext = mContext;
        this.listPropertyType=listPropertyType;
        this.propertyTypeFragment=propertyTypeFragment;
        int count = getSelectedCount();
        propertyTypeFragment.updateCount(count);
    }

    private int getSelectedCount() {
        int count = 0;
        for(TermFilter filter : listPropertyType) {
            if(filter.selected) {
                count++;
            }
        }
        return count;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_property_list_item, parent, false);
        PropertyTypeViewHolder mPropertyTypeViewHolder = new PropertyTypeViewHolder(view);
        return mPropertyTypeViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final PropertyTypeViewHolder mPropertyTypeViewHolder = (PropertyTypeViewHolder) holder;
        mPropertyTypeViewHolder.mTextViewPropertyType.setText(listPropertyType.get(position).displayName.toLowerCase());
        if(listPropertyType.get(position).selected){
            mPropertyTypeViewHolder.mTextViewPropertyType.setTextColor(mContext.getResources().getColor(R.color.appThemeRed));
            mPropertyTypeViewHolder.mCheckboxSelect.setChecked(true);
        }

        mPropertyTypeViewHolder.mCheckboxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listPropertyType.get(position).selected = isChecked;
                if (isChecked)
                {
                    mPropertyTypeViewHolder.mTextViewPropertyType.setTextColor(mContext.getResources().getColor(R.color.appThemeRed));
                }
                else
                {
                    mPropertyTypeViewHolder.mTextViewPropertyType.setTextColor(Color.GRAY);
                }
                propertyTypeFragment.updateCount(getSelectedCount());
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPropertyType.size();
    }



}

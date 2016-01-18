package com.makaan.activity.pyr;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.makaan.R;
import com.makaan.response.master.ApiIntLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makaanuser on 6/1/16.
 */
public class PropertyTypeListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<ApiIntLabel> listPropertyType;
    ArrayList<String> listPropertyTypeSelected= new ArrayList<String>();

    public PropertyTypeListingAdapter(Context mContext , List<ApiIntLabel> listPropertyType) {
        this.mContext = mContext;
        this.listPropertyType=listPropertyType;
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
        mPropertyTypeViewHolder.mTextViewPropertyType.setText(listPropertyType.get(position).name.toLowerCase());

        mPropertyTypeViewHolder.mCheckboxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    listPropertyTypeSelected.add("" + position);
                    mPropertyTypeViewHolder.mTextViewPropertyType.setTextColor(mContext.getResources().getColor(R.color.appThemeRed));
                    //((PyrPageActivity) mContext).updateCount(listPropertyTypeSelected.size());
                }
                else
                {
                    listPropertyTypeSelected.remove("" + position);
                    mPropertyTypeViewHolder.mTextViewPropertyType.setTextColor(Color.GRAY);
                    //((SelectPropertyActivity) mContext).updateCount(listPropertyTypeSelected.size());
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return listPropertyType.size();
    }



}

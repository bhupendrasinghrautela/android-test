package com.makaan.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.response.trend.PriceTrendKey;

import java.util.List;

/**
 * Created by aishwarya on 06/01/16.
 */
public class LegendAdapter extends AbstractBaseAdapter<PriceTrendKey> {
    private OnLegendsTouchListener mListener;

    public interface OnLegendsTouchListener{
        public void legendTouched(View view);
    }
    public LegendAdapter(Context context,List<PriceTrendKey>list) {
        super(context, list);
    }

    public void setLegendTouchListener(OnLegendsTouchListener listener){
        this.mListener = listener;
    }

    @Override
    protected View newView(int position) {
        ViewHolder h = new ViewHolder();
        View newView = mInflater.inflate(R.layout.legends_layout, null);
        h.mLegendsView = newView.findViewById(R.id.legend_view);
        h.mLegendsLabel = (TextView) newView.findViewById(R.id.legend_text);
        h.mLegendsHolder = (LinearLayout) newView.findViewById(R.id.legends_holder);
        newView.setTag(h);
        return newView;
    }

    @Override
    protected void bindView(View view, final int position, final PriceTrendKey item) {
        final ViewHolder h = (ViewHolder) view.getTag();
        h.mLegendsLabel.setText(item.label);
        h.mLegendsView.setBackgroundColor(item.colorId);
        h.mLegendsHolder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isActive = !item.isActive;
                if(item.isActive) {
                    h.mLegendsView.setBackgroundColor(item.colorId);
                }
                else{
                    h.mLegendsView.setBackgroundColor(mContext.getResources().getColor(R.color.grayDivider));
                }
                if(mListener!=null){
                    mListener.legendTouched(v);
                }
            }
        });
    }

    private class ViewHolder {
        View mLegendsView;
        TextView mLegendsLabel;
        LinearLayout mLegendsHolder;
    }
}

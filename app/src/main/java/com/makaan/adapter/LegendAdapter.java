package com.makaan.adapter;

import android.content.Context;
import android.graphics.CornerPathEffect;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.response.trend.PriceTrendKey;
import com.makaan.util.CommonUtil;

import java.util.List;

/**
 * Created by aishwarya on 06/01/16.
 */
public class LegendAdapter extends AbstractBaseAdapter<PriceTrendKey> {
    private OnLegendsTouchListener mListener;

    public interface OnLegendsTouchListener{
        public void legendTouched(View view, int position);
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
        if(!TextUtils.isEmpty(item.label)) {
            h.mLegendsLabel.setText(item.label.toLowerCase());
            h.mLegendsLabel.setVisibility(View.VISIBLE);
        } else {
            h.mLegendsLabel.setVisibility(View.INVISIBLE);
        }
        h.mLegendsView.setBackgroundColor(item.colorId);
        final ShapeDrawable coloredDrawable = new ShapeDrawable(new RectShape());
        coloredDrawable.getPaint().setColor(item.colorId);
        coloredDrawable.getPaint().setStyle(Style.FILL_AND_STROKE);
        coloredDrawable.setPadding(1, 1, 1, 1);
        coloredDrawable.getPaint().setPathEffect(
                new CornerPathEffect(CommonUtil.pixelToDp(mContext,30)));
        h.mLegendsView.setBackgroundDrawable(coloredDrawable);
        final ShapeDrawable greyedDrawable = new ShapeDrawable(new RectShape());
        greyedDrawable.getPaint().setColor(mContext.getResources().getColor(R.color.grayDivider));
        greyedDrawable.getPaint().setStyle(Style.FILL_AND_STROKE);
        greyedDrawable.setPadding(1, 1, 1, 1);
        greyedDrawable.getPaint().setPathEffect(
                new CornerPathEffect(CommonUtil.pixelToDp(mContext,30)));
        h.mLegendsHolder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isActive = !item.isActive;
                if(item.isActive) {
                    h.mLegendsView.setBackgroundDrawable(coloredDrawable);
                }
                else{
                    h.mLegendsView.setBackgroundDrawable(greyedDrawable);
                }
                if(mListener!=null){
                    mListener.legendTouched(v,position);
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

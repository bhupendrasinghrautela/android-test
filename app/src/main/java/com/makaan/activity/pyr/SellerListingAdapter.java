package com.makaan.activity.pyr;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.makaan.R;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.fragment.pyr.TopSellersFragment;
import com.makaan.response.agents.Agent;
import com.makaan.response.agents.TopAgent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by makaanuser on 6/1/16.
 */
public class SellerListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    ArrayList<Integer> listSelectedSellers = new ArrayList<Integer>();
    ArrayList<TopAgent> topAgentsList;
    PyrPagePresenter mPyrPresenter = PyrPagePresenter.getPyrPagePresenter();
    TopSellersFragment topSellersFragment;

    ArrayList<Long> selectedValues;

    public SellerListingAdapter(Context mContext, ArrayList<TopAgent> topAgentsList, TopSellersFragment topSellersFragment) {
        this.mContext = mContext;
        this.topAgentsList = topAgentsList;
        this.topSellersFragment=topSellersFragment;
        selectedValues = new ArrayList<>();
        for(int i = 0; i < (topAgentsList.size() > 4 ? 4 : topAgentsList.size()); i++) {
            if(topAgentsList.get(i).agent != null) {
                selectedValues.add(topAgentsList.get(i).agent.company.id);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_seller_list_item, parent, false);
        SellerViewHolder mSellerViewHolder = new SellerViewHolder(view);
        return mSellerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SellerViewHolder mSellerViewHolder = (SellerViewHolder) holder;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        shapeDrawable.getPaint().setColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mSellerViewHolder.mSellerImage.setBackground(shapeDrawable);
        }
        else{
            mSellerViewHolder.mSellerImage.setBackgroundDrawable(shapeDrawable);
        }

        TopAgent topAgent = topAgentsList.get(position);
        if (null != topAgent) {
            final Agent agent = topAgent.agent;
            if (null != agent) {
                mSellerViewHolder.mSellerName.setText(topAgent.agent.user.fullName);
                mSellerViewHolder.mSellerImage.setText(topAgent.agent.user.fullName);
                //mSellerViewHolder.mSellerRatingBar.setRating(agent.score);        //TODO: change double to int
                mSellerViewHolder.mCheckBoxTick.setOnCheckedChangeListener(null);
                mPyrPresenter.setSellerIds(agent.company.id, selectedValues.contains(agent.company.id));
                if (mPyrPresenter.getSellerIdStatus(agent.company.id)) {
                    mSellerViewHolder.mCheckBoxTick.setBackgroundResource(R.drawable.check_tick_red);
                    mSellerViewHolder.mCheckBoxTick.setChecked(true);
                } else {
                    mSellerViewHolder.mCheckBoxTick.setBackgroundResource(R.drawable.check_tick);
                    mSellerViewHolder.mCheckBoxTick.setChecked(false);
                }

                mSellerViewHolder.mCheckBoxTick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            buttonView.setBackgroundResource(R.drawable.check_tick_red);
                            selectedValues.add(agent.company.id);
                            mPyrPresenter.setSellerIds(agent.company.id, true);
                            topSellersFragment.changeSellerCount(mPyrPresenter.getContactAdvisorsCount());
                        } else {
                            buttonView.setBackgroundResource(R.drawable.check_tick);
                            selectedValues.remove((Long)agent.company.id);
                            mPyrPresenter.setSellerIds(agent.company.id, false);
                            topSellersFragment.changeSellerCount(mPyrPresenter.getContactAdvisorsCount());
                        }
                    }
                });

            }
        }

    }

    @Override
    public int getItemCount() {
        return topAgentsList.size();
    }

}

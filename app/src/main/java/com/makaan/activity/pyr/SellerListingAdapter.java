package com.makaan.activity.pyr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.makaan.R;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.response.agents.Agent;
import com.makaan.response.agents.TopAgent;

import java.util.ArrayList;

/**
 * Created by makaanuser on 6/1/16.
 */
public class SellerListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    ArrayList<Integer> listSelectedSellers = new ArrayList<Integer>();
    ArrayList<TopAgent> topAgentsList;
    PyrPagePresenter mPyrPresenter = PyrPagePresenter.getPyrPagePresenter();

    public SellerListingAdapter(Context mContext, ArrayList<TopAgent> topAgentsList) {
        this.mContext = mContext;
        this.topAgentsList = topAgentsList;
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
//        if(position<4)
//        {
//            mSellerViewHolder.mCheckBoxTick.setChecked(true);
//            listSelectedSellers.add(position);
//        }

        TopAgent topAgent = topAgentsList.get(position);
        if (null != topAgent) {
            final Agent agent = topAgent.agent;
            if (null != agent) {
                mSellerViewHolder.mSellerName.setText(topAgent.agent.name);
                //mSellerViewHolder.mSellerRatingBar.setRating(agent.score);        //TODO: change double to int
                if (mPyrPresenter.getSellerIdStatus(agent.id)) {
                    Log.e("true for ", "position" + position);
                    mSellerViewHolder.mCheckBoxTick.setChecked(true);
                } else {
                    Log.e("false for ", "position" + position);
                    mSellerViewHolder.mCheckBoxTick.setChecked(false);
                }


                mSellerViewHolder.mCheckBoxTick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            Log.e("set ", "true");
                            mPyrPresenter.setSellerIds(agent.id, true);
                        } else {
                            Log.e("set ", "false");
                            mPyrPresenter.setSellerIds(agent.id, false);
                        }
//                if(isChecked) {
//                    listSelectedSellers.add(position);
//                    ((ContactSellersListActivity) mContext).updateButtonText(listSelectedSellers.size());
//                }
//                else
//                {
//                    listSelectedSellers.remove(position);
//                    ((ContactSellersListActivity) mContext).updateButtonText(listSelectedSellers.size());
//
//                }

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

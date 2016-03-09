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
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.fragment.pyr.TopSellersFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.agents.Agent;
import com.makaan.response.agents.TopAgent;
import com.makaan.util.ImageUtils;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSellerViewHolder.mTextSellerImage.setBackground(shapeDrawable);
        }
        else{
            mSellerViewHolder.mTextSellerImage.setBackgroundDrawable(shapeDrawable);
        }

        TopAgent topAgent = topAgentsList.get(position);
        if (null != topAgent) {
            final Agent agent = topAgent.agent;
            if (null != agent) {
                if(agent.company!=null){
                    if(agent.company.assist){
                        mSellerViewHolder.mPlusBadge.setVisibility(View.VISIBLE);
                    }else {
                        mSellerViewHolder.mPlusBadge.setVisibility(View.INVISIBLE);
                    }
                }
                mSellerViewHolder.mSellerName.setText(topAgent.agent.user.fullName.toLowerCase());
                mSellerViewHolder.mTextSellerImage.setText(topAgent.agent.user.fullName);
                setAgentImage(agent, mSellerViewHolder.mTextSellerImage, mSellerViewHolder.mSellerImage);
                mSellerViewHolder.mSellerRatingBar.setRating((float) (agent.company.score/2));        //TODO: change double to int
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
                            selectedValues.remove(agent.company.id);
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

    public void setAgentImage(Agent agent, final TextView mNameImage, final CircleImageView imageView){
        if(agent.user.profilePictureURL!=null && agent.user.profilePictureURL.length()>0 )
        {
            int width = mContext.getResources().getDimensionPixelSize(R.dimen.seller_image_width_height);
            int height = mContext.getResources().getDimensionPixelSize(R.dimen.seller_image_width_height);
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(agent.user.profilePictureURL, width, height, false),
                    new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(imageContainer.getBitmap());
                    mNameImage.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mNameImage.setVisibility(View.VISIBLE);
                }
            });
        }
        else {
            imageView.setVisibility(View.INVISIBLE);
            mNameImage.setVisibility(View.VISIBLE);
        }
    }

}

package com.makaan.fragment.property;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SellerCard;
import com.makaan.ui.view.CustomRatingBar;
import com.makaan.util.AppBus;
import com.makaan.util.ImageUtils;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by aishwarya on 03/02/16.
 */
public class ViewSellersDialogFragment extends DialogFragment {

    @Bind(R.id.fragment_dialog_contact_sellers_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.fragment_dialog_contact_sellers_select_all_checkbox)
    CheckBox selectSeller;
    @Bind(R.id.fragment_dialog_contact_sellers_submit_button)
    Button mSubmitButton;


    @OnCheckedChanged(R.id.fragment_dialog_contact_sellers_select_all_checkbox)
    public void checkChanged(){
        if(selectSeller.isChecked()){
            Bundle bundle=this.getArguments();
            for(SellerCard sellerCard : mSellerCards){
                if(getActivity() instanceof ProjectActivity) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.LABEL, bundle.get(MakaanEventPayload.PROJECT_ID) + "_" + sellerCard.sellerId + "_all");
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectViewOtherSellers);
                }
                else if(getActivity() instanceof PropertyActivity) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    properties.put(MakaanEventPayload.LABEL, bundle.get(MakaanEventPayload.PROJECT_ID) + "_" + sellerCard.sellerId + "_all");
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyViewOtherSellers);
                }
                sellerCard.isChecked = true;
            }
            mChecked = mSellerCards.size();
        }
        else{
            for(SellerCard sellerCard : mSellerCards){
                sellerCard.isChecked = false;
            }
            mChecked = 0;
        }
        if(mAdapter!=null) {
            mAdapter.updateData();
        }
        setButtonText();
    }

    @OnClick(R.id.fragment_dialog_contact_sellers_back_button)
    public void onBackClicked(View view) {
        dismiss();
    }

    @OnClick(R.id.fragment_dialog_contact_sellers_submit_button)
    public void openLeadForm(){
        Intent intent = new Intent(getActivity(), LeadFormActivity.class);
        boolean assist=false;
        ArrayList<Integer> ids = new ArrayList<>();
        for(SellerCard sellerCard:mSellerCards){
            if(sellerCard.isChecked){
                ids.add(sellerCard.sellerId.intValue());
            }
            if(sellerCard.assist){
                assist=true;
            }
        }
        try {
            //Todo add new multi lead fragment
            Bundle bundle=getArguments();
            //SellerCard sellerCard = mSellerCards.get(3);
            /*intent.putExtra("name", sellerCard.name);
            if(sellerCard.rating != null) {
                intent.putExtra("score", sellerCard.rating.toString());
            }
            else{
                intent.putExtra("score","0");
            }*/
            //intent.putExtra("phone",sellerCard.contactNo);//todo: not available in pojo
            //intent.putExtra("id", sellerCard.sellerId.toString());
            intent.putIntegerArrayListExtra("multipleSellerIds", ids);
            if(bundle!=null){
                intent.putExtra("cityId", bundle.getLong("cityId"));
                intent.putExtra("listingId", bundle.getLong("listingId"));
                intent.putExtra("locality", bundle.getString("locality"));
                intent.putExtra("area", bundle.getString("area"));
                intent.putExtra("bhkAndUnitType", bundle.getString("bhkAndUnitType"));
                if(assist){
                    intent.putExtra("assist", assist);
                }
            }
            getActivity().startActivity(intent);
        }
        catch (NullPointerException e){
        }
    }

    private ArrayList<SellerCard> mSellerCards;
    private AllSellersAdapter mAdapter;
    private int mChecked = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_contact_sellers, container, false);

        AppBus.getInstance().register(this); //TODO: move to base fragment
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog_fragment_theme);
    }

    private void init() {
        LayoutManager layoutManager= new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AllSellersAdapter(mSellerCards);
        mRecyclerView.setAdapter(mAdapter);
        mSubmitButton.setText(getString(R.string.contact_top_sellers));
        mSubmitButton.setEnabled(true);
        if(mSellerCards.size()<=3){
            selectSeller.setChecked(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppBus.getInstance().unregister(this);
    }

    public void bindView(ArrayList<SellerCard> sellerCards) {
        Collections.sort(sellerCards);
        mSellerCards = sellerCards;
        int count = 0;
        for(SellerCard sellerCard:mSellerCards){
            if(count <3){
                sellerCard.isChecked = true;
            }
            count++;
        }
        mChecked = 3;
    }

    void onItemClicked(boolean isClicked){
        if(isClicked){
            mChecked ++;
        }
        else{
            mChecked --;
        }
        setButtonText();
    }

    private void setButtonText() {
        if(mChecked == 0){
            mSubmitButton.setEnabled(false);
            mSubmitButton.setText(getString(R.string.contact_sellers));
        }
        else if(mChecked == mSellerCards.size()){
            mSubmitButton.setText(getString(R.string.contact_all_seller));
            mSubmitButton.setEnabled(true);
        }
        else{
            mSubmitButton.setText(getString(R.string.contact) + " " + mChecked + " " + getString(R.string.seller));
            mSubmitButton.setEnabled(true);
        }
    }

    public class AllSellersAdapter extends RecyclerView.Adapter<AllSellersAdapter.ViewHolder> {
        private List<SellerCard> sellerCards;
        int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            CircleImageView mSellerImageView;
            TextView mSellerLogoTextView;
            TextView mSellerTotalProperty;
            TextView mSellerName;
            CustomRatingBar  mSellerRating;
            CheckBox mSellerCheckBox;
            public ViewHolder(View v) {
                super(v);
                mSellerImageView = (CircleImageView) v.findViewById(R.id.seller_image_view);
                mSellerName = (TextView) v.findViewById(R.id.seller_name_text_view);
                mSellerLogoTextView = (TextView) v.findViewById(R.id.seller_logo_text_view);
                mSellerTotalProperty = (TextView) v.findViewById(R.id.seller_total_property_text_view);
                mSellerRating = (CustomRatingBar)v.findViewById(R.id.seller_rating);
                mSellerCheckBox = (CheckBox)v.findViewById(R.id.fragment_dialog_contact_sellers_select_item_checkbox);
            }
        }

        public AllSellersAdapter(List<SellerCard> sellerCards) {
            this.sellerCards = sellerCards;
        }

        public void updateData(){
            notifyDataSetChanged();
        }

        @Override
        public AllSellersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_dialog_contact_seller_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final SellerCard sellerCard = mSellerCards.get(position);
            if(!TextUtils.isEmpty(sellerCard.imageUrl)) {
                holder.mSellerLogoTextView.setVisibility(View.GONE);
                holder.mSellerImageView.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(sellerCard.imageUrl, width, height, false), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        holder.mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showTextAsImage(position,holder);
                    }
                });
            } else {
                showTextAsImage(position,holder);
            }
            if(sellerCard.noOfProperties!=null) {
                holder.mSellerTotalProperty.setText(String.valueOf(sellerCard.noOfProperties) + " " + getString(R.string.property));
            }
            if(sellerCard.rating!=null){
                holder.mSellerRating.setRating(sellerCard.rating.floatValue());
            }
            holder.mSellerCheckBox.setOnCheckedChangeListener(null);
            holder.mSellerCheckBox.setChecked(sellerCard.isChecked);
            holder.mSellerCheckBox.setTag(position +1);
            holder.mSellerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked && getActivity() instanceof ProjectActivity){
                        Bundle bundle=getArguments();
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                        properties.put(MakaanEventPayload.LABEL, bundle.get(MakaanEventPayload.PROJECT_ID)+"_"+
                                mSellerCards.get((int)buttonView.getTag()-1).sellerId+"_"+(int)buttonView.getTag()+"_"+ MakaanTrackerConstants.Label.checked);
                        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectViewOtherSellers);
                    }
                    else if(getActivity() instanceof PropertyActivity){
                        Bundle bundle=getArguments();
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                        properties.put(MakaanEventPayload.LABEL, bundle.get(MakaanEventPayload.PROJECT_ID)+"_"+
                                mSellerCards.get((int)buttonView.getTag()-1).sellerId+"_"+(int)buttonView.getTag()+"_"+ MakaanTrackerConstants.Label.unChecked);
                        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyViewOtherSellers);
                    }

                    onItemClicked(isChecked);
                    sellerCard.isChecked = isChecked;
                }
            });
        }

        private void showTextAsImage(int position,ViewHolder holder) {
            Random random = new Random();
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            int color = ChartUtils.COLORS[position%5];
            drawable.getPaint().setColor(color);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.mSellerLogoTextView.setBackground(drawable);
            } else {
                holder.mSellerLogoTextView.setBackgroundDrawable(drawable);
            }
            if(mSellerCards.get(position).name!=null) {
                holder.mSellerName.setText(mSellerCards.get(position).name + (mSellerCards.get(position).type==null?"":"("+mSellerCards.get(position).type+")"));
                holder.mSellerLogoTextView.setText(String.valueOf(mSellerCards.get(position).name.charAt(0)));
            }
        }

        @Override
        public int getItemCount() {
            return sellerCards.size();
        }

    }
}

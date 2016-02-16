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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.pojo.SellerCard;
import com.makaan.ui.view.CustomRatingBar;
import com.makaan.util.AppBus;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
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
            for(SellerCard sellerCard : mSellerCards){
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
        try {
            SellerCard sellerCard = mSellerCards.get(3);
            intent.putExtra("name", sellerCard.name);
            if(sellerCard.rating != null) {
                intent.putExtra("score", sellerCard.rating.toString());
            }
            else{
                intent.putExtra("score","0");
            }
            intent.putExtra("phone",sellerCard.contactNo);//todo: not available in pojo
            intent.putExtra("id", sellerCard.sellerId.toString());
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            CircularImageView mSellerImageView;
            TextView mSellerLogoTextView;
            TextView mSellerTotalProperty;
            TextView mSellerName;
            CustomRatingBar  mSellerRating;
            CheckBox mSellerCheckBox;
            public ViewHolder(View v) {
                super(v);
                mSellerImageView = (CircularImageView) v.findViewById(R.id.seller_image_view);
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
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mSellerImageView.setVisibility(View.GONE);
            final SellerCard sellerCard = mSellerCards.get(position);
            Random random = new Random();
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            int color = ChartUtils.COLORS[position%5];
            drawable.getPaint().setColor(color);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.mSellerLogoTextView.setBackground(drawable);
            } else {
                holder.mSellerLogoTextView.setBackgroundDrawable(drawable);
            }
            if(sellerCard.name!=null) {
                holder.mSellerName.setText(String.format("%s(%s)", sellerCard.name, mSellerCards.get(position).type));
                holder.mSellerLogoTextView.setText(String.valueOf(sellerCard.name.charAt(0)));
            }
            if(sellerCard.noOfProperties!=null) {
                holder.mSellerTotalProperty.setText(String.valueOf(sellerCard.noOfProperties) + " " + getString(R.string.property));
            }
            if(sellerCard.rating!=null){
                holder.mSellerRating.setRating(sellerCard.rating.floatValue());
            }
            holder.mSellerCheckBox.setOnCheckedChangeListener(null);
            holder.mSellerCheckBox.setChecked(sellerCard.isChecked);
            holder.mSellerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onItemClicked(isChecked);
                    sellerCard.isChecked = isChecked;
                }
            });
        }

        @Override
        public int getItemCount() {
            return sellerCards.size();
        }

    }
}

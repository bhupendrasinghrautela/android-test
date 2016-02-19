package com.makaan.activity.sitevisit;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Project;
import com.makaan.response.property.Property;
import com.makaan.response.user.Company;
import com.makaan.response.user.CompanySeller;
import com.makaan.response.user.User;
import com.makaan.service.ClientEventsService;
import com.makaan.util.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by aishwarya on 18/02/16.
 */
public class SiteVisitUpcommingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SiteVisitCallbacks {
    Context mContext;
    ArrayList<Enquiry> mEnquiries;
    private int width,height;

    public SiteVisitUpcommingAdapter(Context mContext){
        this.mContext=mContext;
        width = mContext.getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        height = mContext.getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
    }

    public void setData(HashMap<Long,Enquiry> enquiries){
        this.mEnquiries = (new ArrayList<Enquiry>(enquiries.values()));
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        try {
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_sitevisit, parent, false);
            SiteVisitViewHolder siteVisitViewHolder = new SiteVisitViewHolder(view);
            siteVisitViewHolder.setCallback(this);
            return siteVisitViewHolder;
        } catch(InflateException ex) {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SiteVisitViewHolder shortListEnquiredViewHolder = (SiteVisitViewHolder)holder;
        shortListEnquiredViewHolder.setPosition(position);
        Enquiry enquiry = mEnquiries.get(position);

        shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.locality_hero);
        Date date = new Date(enquiry.time);

        // S is the millisecond
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h.mm a");
        shortListEnquiredViewHolder.mSiteVisitDate.setText(simpleDateFormat.format(enquiry.time));
        shortListEnquiredViewHolder.mSiteVisitTime.setText(timeFormat.format(enquiry.time));
        if(enquiry.type == EnquiryType.LISTING){
            if(enquiry.listingDetail!=null){
                populateListingDetail(enquiry.listingDetail, shortListEnquiredViewHolder);
            }
            else{
                shortListEnquiredViewHolder.mAddress.setText("");
                shortListEnquiredViewHolder.mName.setText("");
            }
        }
        else if(enquiry.type == EnquiryType.PROJECT){
            if(enquiry.project!=null){
                populateProjectDetail(enquiry.project,shortListEnquiredViewHolder);
                if(enquiry.company!=null) {
                    shortListEnquiredViewHolder.mName.setText(enquiry.company.name);
                    shortListEnquiredViewHolder.mRating.setRating(enquiry.company.score/2);
                    showTextAsImage(shortListEnquiredViewHolder, enquiry.company.name);
                }
            }
            else{
                shortListEnquiredViewHolder.mAddress.setText("");
                shortListEnquiredViewHolder.mName.setText("");
            }
        }
        else if(enquiry.type == EnquiryType.SELLER){
            if(enquiry.company!=null) {
                shortListEnquiredViewHolder.mAddress.setText("");
                shortListEnquiredViewHolder.mName.setText(enquiry.company.name);
                shortListEnquiredViewHolder.mRating.setRating(enquiry.company.score / 2);
                showTextAsImage(shortListEnquiredViewHolder, enquiry.company.name);
            }
            else{
                shortListEnquiredViewHolder.mAddress.setText("");
                shortListEnquiredViewHolder.mName.setText("");
            }
        }
    }

    private void populateProjectDetail(Project project, SiteVisitViewHolder holder) {
        StringBuilder name = new StringBuilder();
        if(project.builder!=null && project.builder.name!=null) {
            name.append(project.builder.name+" ");
        }
        if(project.name!=null) {
            name.append(project.name);
        }
        name.append("\n");
        if(project.locality!=null){
            Locality locality = project.locality;
            if(locality.label!=null){
                name.append(project.locality.label+", ");
            }
            if(locality.suburb!=null && locality.suburb.city!=null && locality.suburb.city.label!=null){
                name.append(locality.suburb.city.label);
            }
        }
        holder.mAddress.setText(name.toString().toLowerCase());
        holder.mMainImage.setImageUrl(project.imageURL, MakaanNetworkClient.getInstance().getImageLoader());
    }

    private void populateListingDetail(ListingDetail listingDetail, final SiteVisitViewHolder holder) {
        StringBuilder name = new StringBuilder();
        if(listingDetail.property!=null) {
            Property property = listingDetail.property;
            if (property.bedrooms != null) {
                name.append(property.bedrooms + "bhk ");
            }
            if (property.unitType != null) {
                name.append(property.unitType);
            }
            name.append("\n");
            if(property.project!=null && property.project.locality!=null){
                Locality locality = property.project.locality;
                if(locality.label!=null){
                    name.append(property.project.locality.label+", ");
                }
                if(locality.suburb!=null && locality.suburb.city!=null && locality.suburb.city.label!=null){
                    name.append(locality.suburb.city.label);
                }
            }
        }
        populateCompany(listingDetail.companySeller, holder);
        holder.mAddress.setText(name.toString().toLowerCase());
        holder.mMainImage.setImageUrl(listingDetail.mainImageURL, MakaanNetworkClient.getInstance().getImageLoader());
    }

    private void populateCompany(CompanySeller companySeller, final SiteVisitViewHolder holder) {
        if(companySeller!=null) {
            final Company company = companySeller.company;
            User user = companySeller.user;
            if (!TextUtils.isEmpty(company.logo)) {
                holder.mSellerText.setVisibility(View.GONE);
                holder.mSellerImage.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(company.logo, width, height, false), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        holder.mSellerImage.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showTextAsImage(holder,company.name);
                    }
                });
            } else if (user != null && !TextUtils.isEmpty(user.profilePictureURL)) {
                holder.mSellerText.setVisibility(View.GONE);
                holder.mSellerImage.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(user.profilePictureURL, width, height, false), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        holder.mSellerImage.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showTextAsImage(holder,company.name);
                    }
                });
            } else {
                showTextAsImage(holder,company.name);
            }
            holder.mRating.setRating((float)(company.score/2));
            holder.mName.setText(companySeller.company.name.toLowerCase());
        }
    }

    private void showTextAsImage(SiteVisitViewHolder holder,String name) {
        if(name == null) {
            holder.mSellerText.setVisibility(View.INVISIBLE);
            return;
        }
        if(!TextUtils.isEmpty(name)) {
            holder.mSellerText.setText(String.valueOf(name.charAt(0)));
        }
        holder.mSellerText.setVisibility(View.VISIBLE);
        holder.mSellerImage.setVisibility(View.GONE);
        // show seller first character as logo

        int[] bgColorArray = mContext.getResources().getIntArray(R.array.bg_colors);

        Random random = new Random();
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//        int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.mSellerText.setBackground(drawable);
        } else {
            holder.mSellerText.setBackgroundDrawable(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return mEnquiries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void openDirections(int position) {
        Enquiry enquiry = mEnquiries.get(position);
        if(enquiry.latitude== null || enquiry.longitude == null){
            return;
        }
        else{
            Intent myIntent = new Intent(Intent.ACTION_VIEW, ClientEventsService.buildNavigationIntentUri(
                    enquiry.latitude, enquiry.longitude));
            mContext.startActivity(myIntent);
        }
    }

    @Override
    public void callNumber(int position) {

    }

    public enum  EnquiryType{
        LISTING,PROJECT,SELLER
    }

    public class Enquiry{
        public Long id;
        public Double latitude;
        public Double longitude;
        public Long projectId;
        public Long listingId;
        public Long clientId;
        public Long time;
        public ListingDetail listingDetail;
        public Project project;
        public com.makaan.response.buyerjourney.Company company;
        public EnquiryType type;
    }
}

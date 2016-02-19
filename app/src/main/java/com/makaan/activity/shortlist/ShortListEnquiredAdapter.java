package com.makaan.activity.shortlist;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.shortlist.ShortListEnquiredViewHolder.ScheduleSiteVisit;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.buyerjourney.SiteVisit;
import com.makaan.request.buyerjourney.SiteVisit.ListingDetails;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Project;
import com.makaan.response.property.Property;
import com.makaan.response.user.Company;
import com.makaan.response.user.CompanySeller;
import com.makaan.response.user.User;
import com.makaan.service.ClientEventsService;
import com.makaan.util.ImageUtils;
import com.makaan.util.JsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListEnquiredAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ScheduleSiteVisit {
    Context mContext;
    ArrayList<Enquiry> mEnquiries;
    private int width,height;

    public ShortListEnquiredAdapter(Context mContext){
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_shortlist_enquired, parent, false);
            ShortListEnquiredViewHolder shortListEnquiredViewHolder = new ShortListEnquiredViewHolder(view);
            shortListEnquiredViewHolder.setListener(this);
            return shortListEnquiredViewHolder;
        } catch(InflateException ex) {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShortListEnquiredViewHolder shortListEnquiredViewHolder = (ShortListEnquiredViewHolder)holder;
        shortListEnquiredViewHolder.setPosition(position);
        Enquiry enquiry = mEnquiries.get(position);

        shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.locality_hero);
        if(enquiry.type == EnquiryType.LISTING){
            if(enquiry.listingDetail!=null){
                populateListingDetail(enquiry.listingDetail,shortListEnquiredViewHolder);
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

    private void populateProjectDetail(Project project, ShortListEnquiredViewHolder holder) {
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

    private void populateListingDetail(ListingDetail listingDetail, final ShortListEnquiredViewHolder holder) {
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

    private void populateCompany(CompanySeller companySeller, final ShortListEnquiredViewHolder holder) {
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

    private void showTextAsImage(ShortListEnquiredViewHolder holder,String name) {
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
    public void onSiteVisitClicked(final int position) {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(mContext, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                makeSiteVisitPostRequest(position,newDate.getTimeInMillis());
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        fromDatePickerDialog.show();
    }

    private void makeSiteVisitPostRequest(int position, long timeInMillis) {
        Enquiry enquiry = mEnquiries.get(position);
        SiteVisit siteVisit = new SiteVisit();
        siteVisit.eventTypeId = 1;
        siteVisit.performTime = timeInMillis;
        if(enquiry.type == EnquiryType.LISTING){
            siteVisit.listingDetails = new ArrayList<>();
            ListingDetails listingDetails = siteVisit.new ListingDetails();
            listingDetails.listingId = enquiry.id;
            siteVisit.listingDetails.add(listingDetails);
            siteVisit.agentId = enquiry.listingDetail.companySeller.userId;
        }
        else  if(enquiry.type == EnquiryType.PROJECT){
            ArrayList<Long> projectIds = new ArrayList<>();
            projectIds.add(enquiry.id);
            siteVisit.projectIds = projectIds;
            siteVisit.agentId = enquiry.company.id;
        }
        else{
            siteVisit.agentId = enquiry.company.id;
        }
        try {
            JSONObject jsonObject = JsonBuilder.toJson(siteVisit);
            ClientEventsService.postSiteVisitSchedule(jsonObject,enquiry.leadId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public enum  EnquiryType{
        LISTING,PROJECT,SELLER
    }

    public class Enquiry{
        public Long id;
        public Long leadId;
        public Long projectId;
        public Long clientId;
        public ListingDetail listingDetail;
        public Project project;
        public com.makaan.response.buyerjourney.Company company;
        public EnquiryType type;
    }
}

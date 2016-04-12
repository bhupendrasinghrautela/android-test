package com.makaan.activity.sitevisit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
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
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Project;
import com.makaan.response.property.Property;
import com.makaan.response.user.Company;
import com.makaan.response.user.CompanySeller;
import com.makaan.response.user.User;
import com.makaan.service.ClientEventsService;
import com.makaan.util.CommonUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.PermissionManager;
import com.segment.analytics.Properties;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by aishwarya on 18/02/16.
 */
public class SiteVisitUpcommingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SiteVisitCallbacks {
    Context mContext;
    Activity mActivity;
    ArrayList<Enquiry> mEnquiries;
    private int width, height;

    public SiteVisitUpcommingAdapter(Context mContext, Activity activity) {
        this.mContext = mContext;
        mActivity = activity;
        width = mContext.getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        height = mContext.getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
    }

    public void setData(HashMap<Long, Enquiry> enquiries) {
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
        } catch (InflateException ex) {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SiteVisitViewHolder shortListEnquiredViewHolder = (SiteVisitViewHolder) holder;
        shortListEnquiredViewHolder.setPosition(position);
        Enquiry enquiry = mEnquiries.get(position);

        if (enquiry.latitude == null || enquiry.longitude == null
                || Double.isNaN(enquiry.latitude) || Double.isNaN(enquiry.longitude)) {
            shortListEnquiredViewHolder.mDirection.setEnabled(false);
            shortListEnquiredViewHolder.mDirectionImage.setAlpha(0.5f);
        } else {
            shortListEnquiredViewHolder.mDirection.setEnabled(true);
            shortListEnquiredViewHolder.mDirectionImage.setAlpha(1f);
        }
        shortListEnquiredViewHolder.mCallNow.setEnabled(false);
        shortListEnquiredViewHolder.mCallImage.setAlpha(0.5f);
        if (enquiry.time != null) {
            Date date = new Date(enquiry.time);

            // S is the millisecond
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("h.mm a");
            shortListEnquiredViewHolder.mSiteVisitDate.setText(simpleDateFormat.format(enquiry.time).toLowerCase());
            shortListEnquiredViewHolder.mSiteVisitTime.setText(timeFormat.format(enquiry.time).toLowerCase());
        }
        if (enquiry.type == EnquiryType.LISTING) {
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.property_placeholder);
            if (enquiry.listingDetail != null) {
                populateListingDetail(enquiry.listingDetail, shortListEnquiredViewHolder);
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
                shortListEnquiredViewHolder.mName.setText("");
            }
        } else if (enquiry.type == EnquiryType.PROJECT) {
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.project_placeholder);
            if (enquiry.project != null) {
                populateProjectDetail(enquiry.project, shortListEnquiredViewHolder);
                if (enquiry.company != null) {
                    if (enquiry.company.name != null) {
                        shortListEnquiredViewHolder.mName.setText(enquiry.company.name.toLowerCase());
                    }
                    if(enquiry.company.score != null) {
                        shortListEnquiredViewHolder.mRating.setVisibility(View.VISIBLE);
                        shortListEnquiredViewHolder.mRating.setRating(enquiry.company.score / 2);
                    } else {
                        shortListEnquiredViewHolder.mRating.setVisibility(View.INVISIBLE);
                    }
                    showTextAsImage(shortListEnquiredViewHolder, enquiry.company.name);
                }
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
                shortListEnquiredViewHolder.mName.setText("");
            }
        } else if (enquiry.type == EnquiryType.SELLER) {
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.seller_placeholder);
            if (enquiry.company != null) {
                shortListEnquiredViewHolder.mAddress.setText("");
                if (enquiry.company.name != null) {
                    shortListEnquiredViewHolder.mName.setText(enquiry.company.name.toLowerCase());
                }
                if(enquiry.company.score != null) {
                    shortListEnquiredViewHolder.mRating.setVisibility(View.VISIBLE);
                    shortListEnquiredViewHolder.mRating.setRating(enquiry.company.score / 2);
                } else {
                    shortListEnquiredViewHolder.mRating.setVisibility(View.INVISIBLE);
                }
                showTextAsImage(shortListEnquiredViewHolder, enquiry.company.name);
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
                shortListEnquiredViewHolder.mName.setText("");
            }
        }
    }

    private void populateProjectDetail(Project project, SiteVisitViewHolder holder) {
        StringBuilder name = new StringBuilder();
        if (project.builder != null && project.builder.name != null) {
            name.append(project.builder.name).append(" ");
        }
        if (project.name != null) {
            name.append(project.name);
        }
        name.append("\n");
        if (project.locality != null) {
            Locality locality = project.locality;
            if (locality.label != null) {
                name.append(project.locality.label).append(", ");
            }
            if (locality.suburb != null && locality.suburb.city != null && locality.suburb.city.label != null) {
                name.append(locality.suburb.city.label);
            }
        }
        holder.mAddress.setText(name.toString().toLowerCase());
        holder.mMainImage.setImageUrl(project.imageURL, MakaanNetworkClient.getInstance().getImageLoader());
    }

    private void populateListingDetail(ListingDetail listingDetail, final SiteVisitViewHolder holder) {
        StringBuilder name = new StringBuilder();
        if (listingDetail.property != null) {
            Property property = listingDetail.property;
            if (property.bedrooms != null) {
                name.append(property.bedrooms).append("bhk ");
            }
            if (property.unitType != null) {
                name.append(property.unitType);
            }
            name.append("\n");
            if (property.project != null && property.project.locality != null) {
                Locality locality = property.project.locality;
                if (locality.label != null) {
                    name.append(property.project.locality.label).append(", ");
                }
                if (locality.suburb != null && locality.suburb.city != null && locality.suburb.city.label != null) {
                    name.append(locality.suburb.city.label);
                }
            }
        }
        populateCompany(listingDetail.companySeller, holder);
        holder.mAddress.setText(name.toString().toLowerCase());
        int height = (int) Math.ceil(mContext.getResources().getDimension(R.dimen.enq_card_height));
        int width = (int) (mContext.getResources().getConfiguration().screenWidthDp * Resources.getSystem().getDisplayMetrics().density);
        holder.mMainImage.setImageUrl(ImageUtils.getImageRequestUrl(listingDetail.mainImageURL, width, height, false),
                MakaanNetworkClient.getInstance().getImageLoader());
    }

    private void populateCompany(CompanySeller companySeller, final SiteVisitViewHolder holder) {
        if (companySeller != null) {
            final Company company = companySeller.company;
            User user = companySeller.user;
            if(user!=null && user.contactNumbers!=null && user.contactNumbers.size()>0){
                holder.mCallNow.setEnabled(true);
                holder.mCallImage.setAlpha(1f);
            }
            if (!TextUtils.isEmpty(company.logo)) {
                holder.mSellerText.setVisibility(View.GONE);
                holder.mSellerImage.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(company.logo, width, height, false), new CustomImageLoaderListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        if(holder!=null && holder.mSellerImage!=null && holder.mSellerText!=null) {
                            holder.mSellerText.setVisibility(View.GONE);
                            holder.mSellerImage.setVisibility(View.VISIBLE);
                            holder.mSellerImage.setImageBitmap(imageContainer.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showTextAsImage(holder, company.name);
                    }
                });
            } else if (user != null && !TextUtils.isEmpty(user.profilePictureURL)) {
                holder.mSellerText.setVisibility(View.GONE);
                holder.mSellerImage.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(user.profilePictureURL, width, height, false), new CustomImageLoaderListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        if(holder!=null && holder.mSellerImage!=null && holder.mSellerText!=null) {
                            holder.mSellerText.setVisibility(View.GONE);
                            holder.mSellerImage.setVisibility(View.VISIBLE);
                            holder.mSellerImage.setImageBitmap(imageContainer.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showTextAsImage(holder, company.name);
                    }
                });
            } else {
                showTextAsImage(holder, company.name);
            }
            if(company.score != null) {
                holder.mRating.setRating((float) (company.score / 2));
            }
            holder.mName.setText(companySeller.company.name.toLowerCase());
        }
    }

    private void showTextAsImage(SiteVisitViewHolder holder, String name) {
        if (name == null) {
            holder.mSellerText.setVisibility(View.INVISIBLE);
            return;
        }
        if (!TextUtils.isEmpty(name)) {
            holder.mSellerText.setText(String.valueOf(name.charAt(0)));
        }
        holder.mSellerText.setVisibility(View.VISIBLE);
        holder.mSellerImage.setVisibility(View.GONE);
        // show seller first character as logo

//        int[] bgColorArray = mContext.getResources().getIntArray(R.array.bg_colors);

//        Random random = new Random();
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//        int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
//        drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
        drawable.getPaint().setColor(CommonUtil.getColor(name, mContext));
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
        /*----- track events--------*/
        if (mEnquiries.get(position).id != null) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardSiteVisits);
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", mEnquiries.get(position).id,
                    MakaanTrackerConstants.Label.direction));
            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.click);
        }
        /*-------------------------*/
        if (enquiry.latitude == null || enquiry.longitude == null
                || Double.isNaN(enquiry.latitude) || Double.isNaN(enquiry.longitude)) {
            return;
        } else if (enquiry.latitude > 0 && enquiry.longitude > 0) {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, ClientEventsService.buildNavigationIntentUri(
                    enquiry.latitude, enquiry.longitude));
            mContext.startActivity(myIntent);
        }
    }

    @Override
    public void callNumber(int position) {
        Enquiry enquiry = mEnquiries.get(position);
        /*----- track events--------*/
        if (mEnquiries.get(position).id != null) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardSiteVisits);
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", mEnquiries.get(position).id,
                    MakaanTrackerConstants.Label.call));
            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.click);
        }
        /*-------------------------*/
        if (enquiry.listingDetail == null) {
            return;
        } else if (enquiry.listingDetail.companySeller != null && enquiry.listingDetail.companySeller.user != null
                && enquiry.listingDetail.companySeller.user.contactNumbers != null
                && enquiry.listingDetail.companySeller.user.contactNumbers.size() > 0) {
            if (PermissionManager.isPermissionRequestRequired(mActivity, Manifest.permission.CALL_PHONE)) {
                PermissionManager.begin().addRequest(PermissionManager.CALL_PHONE_REQUEST).request(mActivity);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + enquiry.listingDetail.companySeller.user.contactNumbers.get(0).contactNumber));
                mActivity.startActivity(intent);
            }
        }
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

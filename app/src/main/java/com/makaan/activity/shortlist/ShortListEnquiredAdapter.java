package com.makaan.activity.shortlist;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
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
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.activity.shortlist.ShortListEnquiredViewHolder.ScheduleSiteVisit;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.request.buyerjourney.SiteVisit;
import com.makaan.request.buyerjourney.SiteVisit.ListingDetails;
import com.makaan.response.buyerjourney.ClientLead;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.locality.Locality;
import com.makaan.response.locality.Suburb;
import com.makaan.response.project.Project;
import com.makaan.response.property.Property;
import com.makaan.response.user.Company;
import com.makaan.response.user.CompanySeller;
import com.makaan.response.user.User;
import com.makaan.service.ClientEventsService;
import com.makaan.util.CommonUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.JsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListEnquiredAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ScheduleSiteVisit {
    Context mContext;
    ArrayList<Enquiry> mEnquiries;
    private int width, height;

    public ShortListEnquiredAdapter(Context mContext) {
        this.mContext = mContext;
        width = mContext.getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        height = mContext.getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
    }

    public void setData(HashMap<Long, Enquiry> enquiries) {
        this.mEnquiries = (new ArrayList<>(enquiries.values()));
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        try {
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_shortlist_enquired, parent, false);
            ShortListEnquiredViewHolder shortListEnquiredViewHolder = new ShortListEnquiredViewHolder(view);
            shortListEnquiredViewHolder.setListener(this);
            return shortListEnquiredViewHolder;
        } catch (InflateException ex) {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShortListEnquiredViewHolder shortListEnquiredViewHolder = (ShortListEnquiredViewHolder) holder;
        shortListEnquiredViewHolder.setPosition(position);
        Enquiry enquiry = mEnquiries.get(position);

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
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        } else if (enquiry.type == EnquiryType.LOCALITY) {
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.locality_placeholder);
            if (enquiry.locality != null) {
                populateLocalityDetail(enquiry.locality, shortListEnquiredViewHolder);
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        } else if (enquiry.type == EnquiryType.SUBURB) {
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.locality_placeholder);
            if (enquiry.project != null) {
                populateSuburbDetail(enquiry.suburb, shortListEnquiredViewHolder);
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        } else if (enquiry.type == EnquiryType.BEDROOM) {
            // todo change this image
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.seller_placeholder);
            if (enquiry.propertyRequirement != null && enquiry.propertyRequirement.bedroom != null) {
                // todo bedroom value is wrong, need change from icrm team
                shortListEnquiredViewHolder.mAddress.setText(enquiry.propertyRequirement.bedroom + "bhk");
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        } /*else if (enquiry.type == EnquiryType.UNIT_TYPE) {
            // todo change this image
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.seller_placeholder);
            if (enquiry.propertyRequirement != null && enquiry.propertyRequirement.areaUnitTypeId != null) {
                // todo bedroom value is wrong, need change from icrm team
                shortListEnquiredViewHolder.mAddress.setText(String.valueOf(enquiry.propertyRequirement.areaUnitTypeId) + "(unit)");
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        }*/ else if (enquiry.type == EnquiryType.BUDGET) {
            // todo change this image
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.seller_placeholder);
            if (enquiry.propertyRequirement != null) {
                if (enquiry.propertyRequirement.minBudget != null
                        && enquiry.propertyRequirement.maxBudget != null) {
                    shortListEnquiredViewHolder.mAddress.setText(enquiry.propertyRequirement.minBudget + " - " + enquiry.propertyRequirement.maxBudget);
                } else if (enquiry.propertyRequirement.minBudget != null) {
                    shortListEnquiredViewHolder.mAddress.setText(String.valueOf(enquiry.propertyRequirement.minBudget));
                } else if (enquiry.propertyRequirement.maxBudget != null) {
                    shortListEnquiredViewHolder.mAddress.setText(String.valueOf(enquiry.propertyRequirement.maxBudget));
                } else {
                    shortListEnquiredViewHolder.mAddress.setText("");
                }
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        } else if (enquiry.type == EnquiryType.SIZE) {
            // todo change this image
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.seller_placeholder);
            if (enquiry.propertyRequirement != null) {
                if (enquiry.propertyRequirement.minSize != null
                        && enquiry.propertyRequirement.maxSize != null) {
                    shortListEnquiredViewHolder.mAddress.setText(enquiry.propertyRequirement.minSize + " - " + enquiry.propertyRequirement.maxSize);
                } else if (enquiry.propertyRequirement.minSize != null) {
                    shortListEnquiredViewHolder.mAddress.setText(String.valueOf(enquiry.propertyRequirement.minSize));
                } else if (enquiry.propertyRequirement.maxSize != null) {
                    shortListEnquiredViewHolder.mAddress.setText(String.valueOf(enquiry.propertyRequirement.maxSize));
                } else {
                    shortListEnquiredViewHolder.mAddress.setText("");
                }
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        } else if (enquiry.type == EnquiryType.LAT_LON) {
            // todo change this image
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.seller_placeholder);
            if (enquiry.propertyRequirement != null) {
                if (enquiry.propertyRequirement.latitude != null
                        && enquiry.propertyRequirement.longitude != null) {
                    shortListEnquiredViewHolder.mAddress.setText("latitude : " + enquiry.propertyRequirement.latitude
                            + ", longitude : " + enquiry.propertyRequirement.longitude);
                } else if (enquiry.propertyRequirement.latitude != null) {
                    shortListEnquiredViewHolder.mAddress.setText("latitude : " + enquiry.propertyRequirement.latitude);
                } else if (enquiry.propertyRequirement.longitude != null) {
                    shortListEnquiredViewHolder.mAddress.setText("longitude : " + enquiry.propertyRequirement.longitude);
                } else {
                    shortListEnquiredViewHolder.mAddress.setText("");
                }
            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        } else if (enquiry.type == EnquiryType.RADIUS) {
            // todo change this image
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.seller_placeholder);
            if (enquiry.propertyRequirement != null && enquiry.propertyRequirement.radiusKm != null) {
                if (enquiry.propertyRequirement.latitude != null
                        && enquiry.propertyRequirement.longitude != null) {
                    shortListEnquiredViewHolder.mAddress.setText(enquiry.propertyRequirement.radiusKm + " Km");
                }

            } else {
                shortListEnquiredViewHolder.mAddress.setText("");
            }
        } else if(enquiry.type == EnquiryType.SELLER){
            shortListEnquiredViewHolder.mMainImage.setDefaultImageResId(R.drawable.seller_placeholder);

            shortListEnquiredViewHolder.mAddress.setText("");
        } else {
            shortListEnquiredViewHolder.mAddress.setText("");
        }

        populateCompanyInfo(shortListEnquiredViewHolder, enquiry);
    }

    private void populateSuburbDetail(Suburb suburb, ShortListEnquiredViewHolder holder) {
        StringBuilder name = new StringBuilder();
        if (suburb.label != null) {
            name.append(suburb.label).append(", ");
        }
        if (suburb.city != null && suburb.city.label != null) {
            name.append(suburb.city.label);
        }
        holder.mAddress.setText(name.toString().toLowerCase());
//        holder.mMainImage.setImageUrl(ImageUtils.getImageRequestUrl(suburb., 0, 0, false), MakaanNetworkClient.getInstance().getImageLoader());
    }

    private void populateLocalityDetail(Locality locality, ShortListEnquiredViewHolder holder) {
        StringBuilder name = new StringBuilder();
        if (locality.label != null) {
            name.append(locality.label).append(", ");
        }
        if (locality.suburb != null && locality.suburb.city != null && locality.suburb.city.label != null) {
            name.append(locality.suburb.city.label);
        }
        holder.mAddress.setText(name.toString().toLowerCase());
        holder.mMainImage.setImageUrl(ImageUtils.getImageRequestUrl(locality.localityHeroshotImageUrl, 0, 0, false), MakaanNetworkClient.getInstance().getImageLoader());
    }

    private void populateCompanyInfo(ShortListEnquiredViewHolder shortListEnquiredViewHolder, Enquiry enquiry) {
        if (enquiry.company != null) {
            if (enquiry.company.name != null) {
                shortListEnquiredViewHolder.mName.setText(enquiry.company.name.toLowerCase());
            }
            shortListEnquiredViewHolder.mRating.setRating(enquiry.company.score / 2);
            showTextAsImage(shortListEnquiredViewHolder, enquiry.company.name);

            shortListEnquiredViewHolder.mRating.setVisibility(View.VISIBLE);
        } else {
            shortListEnquiredViewHolder.mRating.setVisibility(View.INVISIBLE);
            shortListEnquiredViewHolder.mSellerText.setVisibility(View.INVISIBLE);
            shortListEnquiredViewHolder.mSellerImage.setVisibility(View.INVISIBLE);
        }
    }

    private void populateProjectDetail(Project project, ShortListEnquiredViewHolder holder) {
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
        holder.mMainImage.setImageUrl(ImageUtils.getImageRequestUrl(project.imageURL, 0, 0, false), MakaanNetworkClient.getInstance().getImageLoader());
    }

    private void populateListingDetail(ListingDetail listingDetail, final ShortListEnquiredViewHolder holder) {
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
//        populateCompany(listingDetail.companySeller, holder);
        holder.mAddress.setText(name.toString().toLowerCase());
        holder.mMainImage.setImageUrl(ImageUtils.getImageRequestUrl(listingDetail.mainImageURL, 0, 0, false), MakaanNetworkClient.getInstance().getImageLoader());
    }

    private void populateCompany(CompanySeller companySeller, final ShortListEnquiredViewHolder holder) {
        if (companySeller != null) {
            final Company company = companySeller.company;
            User user = companySeller.user;
            if (!TextUtils.isEmpty(company.logo)) {
                holder.mSellerText.setVisibility(View.GONE);
                holder.mSellerImage.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(company.logo, width, height, false), new CustomImageLoaderListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        if (holder != null && holder.mSellerImage != null && holder.mSellerText != null) {
                            holder.mSellerText.setVisibility(View.GONE);
                            holder.mSellerImage.setVisibility(View.VISIBLE);
                            holder.mSellerImage.setImageBitmap(imageContainer.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
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
                        if (holder != null && holder.mSellerImage != null && holder.mSellerText != null) {
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
            if (company.score != null) {
                holder.mRating.setRating((float) (company.score / 2));
            }
            holder.mName.setText(companySeller.company.name.toLowerCase());
        }
    }

    private void showTextAsImage(ShortListEnquiredViewHolder holder, String name) {
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
    public void onSiteVisitClicked(final int position) {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(mContext, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                makeSiteVisitPostRequest(position, newDate.getTimeInMillis());
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        fromDatePickerDialog.show();
    }

    @Override
    public void onItemClick(int position) {
        if(mEnquiries != null && position < mEnquiries.size()) {
            Enquiry enquiry = mEnquiries.get(position);
            if(enquiry != null && enquiry.propertyRequirement != null) {

                if(enquiry.propertyRequirement.listingId != null
                        && enquiry.propertyRequirement.listingId > 0) {
                    Intent overviewIntent = new Intent(mContext, OverviewActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putLong(OverviewActivity.ID, enquiry.propertyRequirement.listingId);
                    bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROPERTY.ordinal());

                    overviewIntent.putExtras(bundle);
                    mContext.startActivity(overviewIntent);

                } else if(enquiry.propertyRequirement.projectId != null
                        && enquiry.propertyRequirement.projectId > 0) {
                    Intent overviewIntent = new Intent(mContext, OverviewActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putLong(OverviewActivity.ID, enquiry.propertyRequirement.projectId);
                    bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROJECT.ordinal());

                    overviewIntent.putExtras(bundle);
                    mContext.startActivity(overviewIntent);

                } else if(enquiry.propertyRequirement.localityId != null
                        && enquiry.propertyRequirement.localityId > 0) {
                    Intent overviewIntent = new Intent(mContext, OverviewActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putLong(OverviewActivity.ID, enquiry.propertyRequirement.localityId);
                    bundle.putInt(OverviewActivity.TYPE, OverviewItemType.LOCALITY.ordinal());

                    overviewIntent.putExtras(bundle);
                    mContext.startActivity(overviewIntent);

                }
            }
        }
    }

    private void makeSiteVisitPostRequest(int position, long timeInMillis) {
        Enquiry enquiry = mEnquiries.get(position);
        SiteVisit siteVisit = new SiteVisit();
        siteVisit.eventTypeId = 1;
        siteVisit.performTime = timeInMillis;
        if (enquiry.type == EnquiryType.LISTING) {
            siteVisit.listingDetails = new ArrayList<>();
            ListingDetails listingDetails = siteVisit.new ListingDetails();
            listingDetails.listingId = enquiry.id;
            siteVisit.listingDetails.add(listingDetails);
            siteVisit.agentId = enquiry.listingDetail.companySeller.userId;
        } else if (enquiry.type == EnquiryType.PROJECT) {
            ArrayList<Long> projectIds = new ArrayList<>();
            projectIds.add(enquiry.id);
            siteVisit.projectIds = projectIds;
            siteVisit.agentId = enquiry.company.id;
        } else {
            siteVisit.agentId = enquiry.company.id;
        }
        try {
            JSONObject jsonObject = JsonBuilder.toJson(siteVisit);
            ClientEventsService.postSiteVisitSchedule(jsonObject, enquiry.leadId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public enum EnquiryType {
        LISTING, PROJECT, SELLER, LOCALITY, SUBURB, BEDROOM, UNIT_TYPE, BUDGET, SIZE, LAT_LON, RADIUS
    }

    public class Enquiry {
        public Long id;
        public Long leadId;
        public ClientLead.PropertyRequirement propertyRequirement;
        public ListingDetail listingDetail;
        public Project project;
        public Locality locality;
        public Suburb suburb;
        public com.makaan.response.buyerjourney.Company company;
        public EnquiryType type;
        public Long companyId;
    }
}

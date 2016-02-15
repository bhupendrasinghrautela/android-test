package com.makaan.ui.listing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.listing.Listing;
import com.makaan.response.serp.ListingInfoMap;
import com.makaan.ui.view.WishListButton;
import com.makaan.ui.view.WishListButton.WishListDto;
import com.makaan.ui.view.WishListButton.WishListType;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.makaan.util.StringUtil;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class DefaultListingView extends AbstractListingView{

    @Bind(R.id.serp_default_listing_property_shortlist_checkbox)
    public WishListButton mPropertyWishListCheckbox;

    @Bind(R.id.serp_default_listing_property_image_image_view)
    FadeInNetworkImageView mPropertyImageView;
    @Bind(R.id.serp_default_listing_property_price_difference_image_view)
    ImageView mPropertyPriceDifferenceImageView;
    @Bind(R.id.serp_default_listing_badge_Image_view)
    ImageView mBadgeImageView;

    @Bind(R.id.serp_default_listing_property_possession_image_view)
    ImageView mPossessionImageView;
    @Bind(R.id.serp_default_listing_property_floor_image_view)
    ImageView mFloorImageView;
    @Bind(R.id.serp_default_listing_property_bathroom_image_view)
    ImageView mBathroomImageView;

    @Bind(R.id.serp_default_listing_seller_image_view)
    CircularImageView mSellerImageView;

    @Bind(R.id.serp_default_listing_seller_logo_text_view)
    TextView mSellerLogoTextView;

    @Bind(R.id.serp_default_listing_property_price_text_view)
    TextView mPropertyPriceTextView;
    @Bind(R.id.serp_default_listing_property_price_unit_text_view)
    TextView mPropertyPriceUnitTextView;
    @Bind(R.id.serp_default_listing_property_price_sq_ft_text_view)
    TextView mPropertyPriceSqFtTextView;
    @Bind(R.id.serp_default_listing_property_bhk_info_text_view)
    TextView mPropertyBhkInfoTextView;
    @Bind(R.id.serp_default_listing_property_size_info_text_view)
    TextView mPropertySizeInfoTextView;
    @Bind(R.id.serp_default_listing_property_address_text_view)
    TextView mPropertyAddressTextView;
    /*@Bind(R.id.serp_default_listing_property_tagline_text_view)
    TextView mPropertyDescriptionTextView;*/
    @Bind(R.id.serp_default_listing_seller_name_text_view)
    TextView mPropertySellerNameTextView;
    @Bind(R.id.serp_default_listing_badge_text_view)
    TextView mBadgeTextView;

    @Bind(R.id.serp_default_listing_property_possession_date_text_view)
    TextView mPropertyPossessionDateTextView;
    @Bind(R.id.serp_default_listing_property_floor_info_text_view)
    TextView mPropertyFloorInfoTextView;
    @Bind(R.id.serp_default_listing_property_bathroom_number_date_text_view)
    TextView mPropertyBathroomNumberTextView;

    @Bind(R.id.serp_default_listing_property_possession_text_view)
    TextView mPossesionTextView;
    @Bind(R.id.serp_default_listing_property_floor_text_view)
    TextView mFloorTextView;
    @Bind(R.id.serp_default_listing_property_bathroom_text_view)
    TextView mBathroomTextView;

    @Bind(R.id.serp_default_listing_assist_button)
    Button mAssistButton;
    @Bind(R.id.serp_default_listing_call_button)
    Button mCallButton;

    @Bind(R.id.serp_default_listing_seller_rating)
    RatingBar mSellerRatingBar;

    @Bind(R.id.serp_default_listing_property_seller_info_relative_layout)
    RelativeLayout mSellerInfoRelativeLayout;

    private SharedPreferences mPreferences;
    private Listing mListing;
    private SerpRequestCallback mCallback;

    ArrayList<ImageView> mPropertyInfoImageViews = new ArrayList<>();
    ArrayList<TextView> mPropertyInfoTextViews = new ArrayList<>();
    ArrayList<TextView> mPropertyInfoNameTextViews = new ArrayList<>();

    public DefaultListingView(Context context) {
        super(context);
    }

    public DefaultListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback) {
        super.populateData(data, callback);
        if(!(data instanceof Listing)) {
            return;
        }

        mPropertyInfoImageViews.clear();
        mPropertyInfoTextViews.clear();
        mPropertyInfoNameTextViews.clear();

        mPropertyInfoImageViews.add(mPossessionImageView);
        mPropertyInfoImageViews.add(mFloorImageView);
        mPropertyInfoImageViews.add(mBathroomImageView);

        mPropertyInfoTextViews.add(mPropertyPossessionDateTextView);
        mPropertyInfoTextViews.add(mPropertyFloorInfoTextView);
        mPropertyInfoTextViews.add(mPropertyBathroomNumberTextView);

        mPropertyInfoNameTextViews.add(mPossesionTextView);
        mPropertyInfoNameTextViews.add(mFloorTextView);
        mPropertyInfoNameTextViews.add(mBathroomTextView);

        mCallback = callback;

        boolean isBuy = SerpObjects.isBuyContext(getContext());

        mListing = (Listing)data;
        mPropertyWishListCheckbox.bindView(new WishListDto(mListing.lisitingId.longValue(), mListing.projectId.longValue(), WishListType.listing));

        if(mListing.mainImageUrl != null && !TextUtils.isEmpty(mListing.mainImageUrl)) {
            int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_property_image_width);
            int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_property_image_height);
            mPropertyImageView.setImageUrl(ImageUtils.getImageRequestUrl(mListing.mainImageUrl, width, height, false), MakaanNetworkClient.getInstance().getImageLoader());
        } else {
            //TODO this is just a dummy image
            String url = "https://im.proptiger-ws.com/1/644953/6/imperial-project-image-460007.jpeg";
            mPropertyImageView.setImageUrl(url, MakaanNetworkClient.getInstance().getImageLoader());
        }

        if(mListing.images != null && mListing.images.size() > 0) {

           /* ImageRequest request = new ImageRequest(listing.images.get(0).url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            mPropertyImageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            mPropertyImageView.setImageResource(R.drawable.temp_bulding);
                        }
                    });
            MakaanBuyerApplication.getInstance().addToRequestQueue(request, "");

            request = new ImageRequest(listing.images.get(0).url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            mSellerImageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            mSellerImageView.setImageResource(R.drawable.temp_builder);
                        }
                    });
            MakaanBuyerApplication.getInstance().addToRequestQueue(request, "");*/
        }

        if(mListing.lisitingPostedBy == null) {
            mSellerRatingBar.setVisibility(View.GONE);
        } else {
            mSellerRatingBar.setRating(mListing.lisitingPostedBy.rating);
        }
        mapPropertyInfo(isBuy);


        // TODO check for unit info
        String priceString = StringUtil.getDisplayPrice(mListing.price);
        String priceUnit = "";
        if(priceString.indexOf("\u20B9") == 0) {
            priceString = priceString.replace("\u20B9", "");
        }
        String[] priceParts = priceString.split(" ");
        priceString = priceParts[0];
        if(priceParts.length > 1) {
            priceUnit = priceParts[1];
        }

        // set price info
        mPropertyPriceTextView.setText(priceString.toLowerCase());
        mPropertyPriceUnitTextView.setText(priceUnit);
        mPropertyPriceSqFtTextView.setText(String.format("%s%s/sqft", "\u20B9", StringUtil.getFormattedNumber(mListing.pricePerUnitArea)).toLowerCase());

        // set property bhk and size info
        if(mListing.bhkInfo == null) {
            mPropertyBhkInfoTextView.setVisibility(View.GONE);
        } else {
            mPropertyBhkInfoTextView.setVisibility(View.VISIBLE);
            mPropertyBhkInfoTextView.setText(mListing.bhkInfo.toLowerCase());
        }

        if(mListing.sizeInfo == null) {
            mPropertySizeInfoTextView.setVisibility(View.GONE);
        } else {
            mPropertySizeInfoTextView.setVisibility(View.VISIBLE);
            mPropertySizeInfoTextView.setText(mListing.sizeInfo.toLowerCase());
        }

        // set property address info {project_name},{localityName}_{cityName}
        if(mListing.project.name != null) {
            mPropertyAddressTextView.setText(String.format("%s, %s, %s", mListing.project.name, mListing.localityName, mListing.cityName).toLowerCase());
        } else {
            mPropertyAddressTextView.setText(String.format("%s, %s", mListing.localityName, mListing.cityName).toLowerCase());
        }

        // set property tagline or detailed info
        /*if(mListing.description != null) {
            String text = Html.fromHtml(mListing.description.toLowerCase()).toString();
            text = text.replace("\t", "").replace("\n", "");

            if("null".equalsIgnoreCase(text)) {
                mPropertyDescriptionTextView.setText("not available");
            } else {
                mPropertyDescriptionTextView.setText(text);
            }
        } else {
            mPropertyDescriptionTextView.setText("not available");
        }*/

        if(callback.needSellerInfoInSerp()) {
            mSellerInfoRelativeLayout.setVisibility(View.VISIBLE);

            // TODO check seller name
            if(mListing.lisitingPostedBy != null) {
                mPropertySellerNameTextView.setText(String.format("%s (%s)",mListing.lisitingPostedBy.name, mListing.lisitingPostedBy.type).toLowerCase());
            } else {
                mPropertySellerNameTextView.setText(mListing.project.builderName.toLowerCase());
            }

            int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
            int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);

            if(!TextUtils.isEmpty(mListing.lisitingPostedBy.logo)) {
                mSellerLogoTextView.setVisibility(View.GONE);
                mSellerImageView.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(mListing.lisitingPostedBy.logo, width, height, false), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showTextAsImage();
                    }
                });
            } else if(!TextUtils.isEmpty(mListing.lisitingPostedBy.profilePictureURL)) {
                mSellerLogoTextView.setVisibility(View.GONE);
                mSellerImageView.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(mListing.lisitingPostedBy.profilePictureURL, width, height, false), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showTextAsImage();
                    }
                });
            } else {
                showTextAsImage();
            }

            if(mListing.lisitingPostedBy == null || !mListing.lisitingPostedBy.assist) {
                mAssistButton.setVisibility(View.GONE);
            } else {
                mAssistButton.setVisibility(View.VISIBLE);
            }
            //mAssistButton.setVisibility(View.VISIBLE);
        } else {
            mSellerInfoRelativeLayout.setVisibility(View.GONE);
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListing != null) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(KeyUtil.LISTING_ID, mListing.id);
                    bundle.putDouble(KeyUtil.LISTING_LAT, mListing.latitude);
                    bundle.putDouble(KeyUtil.LISTING_LON, mListing.longitude);
                    bundle.putString(KeyUtil.LISTING_Image, mListing.mainImageUrl);

                    mCallback.requestDetailPage(SerpActivity.REQUEST_PROPERTY_PAGE, bundle);
                }
            }
        });

    }

    private void showTextAsImage() {
        if(mListing.lisitingPostedBy.name == null) {
            mSellerLogoTextView.setVisibility(View.INVISIBLE);
        }
        mSellerLogoTextView.setText(String.valueOf(mListing.lisitingPostedBy.name.charAt(0)));
        mSellerLogoTextView.setVisibility(View.VISIBLE);
        mSellerImageView.setVisibility(View.GONE);
        // show seller first character as logo

        int[] bgColorArray = getResources().getIntArray(R.array.bg_colors);

        Random random = new Random();
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//        int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSellerLogoTextView.setBackground(drawable);
        } else {
            mSellerLogoTextView.setBackgroundDrawable(drawable);
        }
    }

    private void mapPropertyInfo(boolean isBuy) {
        // check possession for under construction properties
        // or age of property for reseller properties
        // or furnished/unfurnished/semi-furnished for rental properties
        if(isBuy) {
            if(!TextUtils.isEmpty(mListing.propertyType) && "Residential Plot".equals(mListing.propertyType)) {
                ArrayList<ListingInfoMap.InfoMap> map = MasterDataCache.getInstance().getListingMapInfo(ListingInfoMap.MAP_BUY_PLOT_PROPERTIES);
                mapPropertyInfo(map);
            } else {
                ArrayList<ListingInfoMap.InfoMap> map = MasterDataCache.getInstance().getListingMapInfo(ListingInfoMap.MAP_BUY_PROPERTIES);
                mapPropertyInfo(map);
                /*if (mListing.isReadyToMove && mListing.propertyAge != null && !TextUtils.isEmpty(mListing.propertyAge)) {
                    mPossesionTextView.setText("Age of Property");
                    mPropertyPossessionDateTextView.setText(mListing.propertyAge);
                } else if (mListing.possessionDate != null && !TextUtils.isEmpty(mListing.possessionDate)) {
                    mPossesionTextView.setText("Possession");
                    mPropertyPossessionDateTextView.setText(mListing.possessionDate);
                } else {
                    mPossesionTextView.setVisibility(View.GONE);
                    mPropertyPossessionDateTextView.setVisibility(View.GONE);
                    mPossessionImageView.setVisibility(View.GONE);
                }*/
            }
        } else {
            ArrayList<ListingInfoMap.InfoMap> map = MasterDataCache.getInstance().getListingMapInfo(ListingInfoMap.MAP_RENT_PROPERTIES);
            mapPropertyInfo(map);
            /*if(mListing.furnished != null && !TextUtils.isEmpty(mListing.furnished)) {
                mPossesionTextView.setText("Furnished");
                mPropertyPossessionDateTextView.setText(mListing.furnished);
            } else {
                mPossesionTextView.setVisibility(View.GONE);
                mPropertyPossessionDateTextView.setVisibility(View.GONE);
                mPossessionImageView.setVisibility(View.GONE);
            }*/
        }

        /*// set value of floor info of property out of total floors in building
        if(mListing.floor == 1) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>st</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else if(mListing.floor == 2) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>nd</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else if(mListing.floor == 3) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>rd</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>th</sup> of %d", mListing.floor, mListing.totalFloors)));
        }

        // set bathroom info of property
        // TODO check if need to be replaced by any other value if bathroom value is 0 or negative
        mPropertyBathroomNumberTextView.setText(String.valueOf(mListing.bathrooms));*/
    }

    private void mapPropertyInfo(ArrayList<ListingInfoMap.InfoMap> map) {
        int j = 0;
        for(int i = 0; i < map.size() && j < mPropertyInfoImageViews.size(); i++) {
            if(mapPropertyInfo(map.get(i), j)) {
                mPropertyInfoImageViews.get(j).setVisibility(View.VISIBLE);
                mPropertyInfoTextViews.get(j).setVisibility(View.VISIBLE);
                mPropertyInfoNameTextViews.get(j).setVisibility(View.VISIBLE);
                j++;
            }
        }
        while(j < mPropertyInfoImageViews.size()) {
            mPropertyInfoImageViews.get(j).setVisibility(View.GONE);
            mPropertyInfoTextViews.get(j).setVisibility(View.GONE);
            mPropertyInfoNameTextViews.get(j).setVisibility(View.GONE);
            j++;
        }
    }

    // TODO use lru cache for image mapping
    private boolean mapPropertyInfo(ListingInfoMap.InfoMap infoMap, int j) {
        switch (infoMap.fieldName) {
            case "propertyStatus":
                if(!TextUtils.isEmpty(mListing.propertyStatus)) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(mListing.propertyStatus.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "propertyAge":
                if(mListing.isReadyToMove && mListing.age >= 0) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    if(mListing.age <= 1) {
                        mPropertyInfoTextViews.get(j).setText(String.format("%d yr", mListing.age));
                    } else {
                        mPropertyInfoTextViews.get(j).setText(String.format("%d yrs", mListing.age));
                    }
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "possessionDate":
                if(!mListing.isReadyToMove && !TextUtils.isEmpty(mListing.possessionDate)) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(mListing.possessionDate.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "bathrooms":
                if(mListing.bathrooms != null && mListing.bathrooms != 0) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(String.valueOf(mListing.bathrooms).toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "floor,totalFloors":
                if(mListing.floor != null && mListing.totalFloors != null && mListing.floor != 0 && mListing.totalFloors != 0) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    if(mListing.floor == 1) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup>st</sup> of %d", mListing.floor, mListing.totalFloors).toLowerCase()));
                    } else if(mListing.floor == 2) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup>nd</sup> of %d", mListing.floor, mListing.totalFloors).toLowerCase()));
                    } else if(mListing.floor == 3) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup>rd</sup> of %d", mListing.floor, mListing.totalFloors).toLowerCase()));
                    } else {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup>th</sup> of %d", mListing.floor, mListing.totalFloors).toLowerCase()));
                    }
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "balcony":
                if(mListing.balcony != null && mListing.balcony != 0) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(String.valueOf(mListing.balcony).toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "listingCategory":
                if(!TextUtils.isEmpty(mListing.listingCategory)) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(mListing.listingCategory.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "facing":
                if(!TextUtils.isEmpty(mListing.facing)) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(mListing.facing.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "ownershipType":
                if(!TextUtils.isEmpty(mListing.ownershipType)) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(mListing.ownershipType.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "furnished":
                if(!TextUtils.isEmpty(mListing.furnished)) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(mListing.furnished.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "isReadyToMove":
                if(!mListing.isReadyToMove && !TextUtils.isEmpty(mListing.possessionDate)) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(mListing.possessionDate.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                } else if(mListing.isReadyToMove) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText("ready to move in");
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "noOfOpenSides":
                if(mListing.noOfOpenSides != null && mListing.noOfOpenSides != 0) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(String.valueOf(mListing.noOfOpenSides).toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
            case "securityDeposit":
                if(mListing.securityDeposit != null && mListing.securityDeposit != 0) {
                    mPropertyInfoImageViews.get(j).setImageResource(this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan"));
                    mPropertyInfoTextViews.get(j).setText(String.valueOf(mListing.securityDeposit).toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    return true;
                }
                break;
        }
        return false;
    }

    @OnClick({R.id.serp_default_listing_seller_image_frame_layout, R.id.serp_default_listing_seller_name_text_view, R.id.serp_default_listing_seller_rating})
    public void onSellerPressed(View view) {
        // TODO discuss what should be done if listing posted by is not present
        SerpRequest request = new SerpRequest(SerpActivity.TYPE_SELLER);
        request.setSellerId(mListing.lisitingPostedBy.id);
        request.launchSerp(getContext());

//        MakaanBuyerApplication.mSerpSelector.term("sellerId", String.valueOf(mListing.sellerId));
    }

    @OnClick(R.id.serp_default_listing_property_address_frame_layout)
    public void onProjectClicked(View view) {
        if(mListing.projectId != null && mListing.projectId != 0) {
            Bundle bundle = new Bundle();
            bundle.putLong(ProjectActivity.PROJECT_ID, mListing.projectId);
            mCallback.requestDetailPage(SerpActivity.REQUEST_PROJECT_PAGE, bundle);
        }
    }

    @OnClick(R.id.serp_default_listing_call_button)
    public void onCallClicked(View view) {

        Bundle bundle = new Bundle();

        bundle.putString("name", mListing.lisitingPostedBy.name);
        bundle.putString("score", String.valueOf(mListing.lisitingPostedBy.rating));
        bundle.putString("phone", "9090909090");//todo: not available in pojo
        bundle.putString("id", String.valueOf(mListing.lisitingPostedBy.id));

        mCallback.requestDetailPage(SerpActivity.REQUEST_LEAD_FORM, bundle);
    }

    @OnClick(R.id.serp_default_listing_assist_button)
    public void onAssistClicked(View view) {
        if(mCallback != null) {
            mCallback.requestDetailPage(SerpActivity.REQUEST_MPLUS_POPUP, null);
        }
    }

}

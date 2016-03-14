package com.makaan.fragment.buyerJourney;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.event.buyerjourney.NewMatchesGetEvent;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.MakaanMessageDialogFragment;
import com.makaan.fragment.listing.SetAlertsDialogFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.pojo.SelectorParser;
import com.makaan.pojo.SerpRequest;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.city.City;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Builder;
import com.makaan.response.saveSearch.SaveSearch;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.makaan.ui.CustomNetworkImageView;
import com.makaan.util.ErrorUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * Created by aishwarya on 29/01/16.
 */
public class SaveSearchFragment extends MakaanBaseFragment {

    @Bind(R.id.save_search_recycler_view)
    RecyclerView mRecyclerView;

    private SaveSearchAdapter mAdapter;
    private Context context;
    private BuyerDashboardCallbacks mCallback;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_save_search;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();

        if (MasterDataCache.getInstance().getSavedSearch() != null) {

            ArrayList<SaveSearch> saveSearches = MasterDataCache.getInstance().getSavedSearch();
            mAdapter = new SaveSearchAdapter(saveSearches);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
            ArrayList<Long> ids = new ArrayList<>();
            for (SaveSearch search : saveSearches) {
                ids.add(search.id);
            }
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearchesNewMatchesByIds(ids);
        }
    }

    private void initView() {
        LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(BuyerDashboardCallbacks callbacks) {
        this.mCallback = callbacks;
    }


    private class SaveSearchAdapter extends RecyclerView.Adapter<SaveSearchAdapter.ViewHolder> {
        private List<SaveSearch> savedSearches;
        private List<ImageObject> imageObjects;
        private HashMap<String, Integer> newMatchesCount;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ImageView deleteImageView;
            private final CustomNetworkImageView backgroundImageView;
            private final TextView newMatchesCountTextView;
            // each data item is just a string in this case
            public TextView saveSearchName;
            public TextView saveSearchFilter;
            public TextView saveSearchPlace;
            public Long searchId;
            private int position;

            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                backgroundImageView = (CustomNetworkImageView) v.findViewById(R.id.iv_search);
                saveSearchName = (TextView) v.findViewById(R.id.search_name);
                saveSearchFilter = (TextView) v.findViewById(R.id.filter_name);
                saveSearchPlace = (TextView) v.findViewById(R.id.place_name);
                deleteImageView = (ImageView) v.findViewById(R.id.iv_cancel);
                newMatchesCountTextView = (TextView) v.findViewById(R.id.new_matches_count);
                deleteImageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v instanceof ImageView) {
                    if (searchId != null) {
                        MakaanMessageDialogFragment.showMessage(getActivity().getFragmentManager(),
                                "delete saved search " + saveSearchName.getText().toString() + "?", "delete", "cancel",
                                new MakaanMessageDialogFragment.MessageDialogCallbacks() {
                                    @Override
                                    public void onPositiveClicked() {
                                        ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).removeSavedSearch(searchId);
                                    }

                                    @Override
                                    public void onNegativeClicked() {

                                    }
                                });
                    }
                } else if (savedSearches != null && savedSearches.size() > position) {
                    createSaveSearchEvent(imageObjects,position);
                    SerpRequest request = new SerpRequest(SerpActivity.TYPE_SUGGESTION);
                    request.launchSerp(getActivity(), savedSearches.get(position).searchQuery);
                }
            }
        }

        public SaveSearchAdapter(List<SaveSearch> savedSearches) {
            if (this.savedSearches == null) {
                this.savedSearches = new ArrayList<>();
            } else {
                this.savedSearches.clear();
            }
            this.savedSearches.addAll(savedSearches);

            if (this.imageObjects == null) {
                this.imageObjects = new ArrayList<>();
            } else {
                this.imageObjects.clear();
            }
            for (int i = 0; i < savedSearches.size(); i++) {
                this.imageObjects.add(new ImageObject());
            }
        }

        public void updateNewCount(HashMap<String, Integer> data) {
            if (this.newMatchesCount == null) {
                this.newMatchesCount = new HashMap<>();
            } else {
                this.newMatchesCount.clear();
            }
            this.newMatchesCount.putAll(data);
            notifyDataSetChanged();
        }

        @Override
        public SaveSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_search, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.position = position;
            String name = savedSearches.get(position).name;
            holder.searchId = savedSearches.get(position).id;
            if (!TextUtils.isEmpty(name)) {
                String[] text = name.split(";");
                holder.saveSearchName.setVisibility(View.VISIBLE);
                holder.saveSearchName.setText(text[0].toLowerCase());
                if (text.length > 1) {
                    holder.saveSearchPlace.setVisibility(View.VISIBLE);
                    holder.saveSearchFilter.setVisibility(View.VISIBLE);

                    String[] filterArray = text[1].split(SetAlertsDialogFragment.SEPARATOR_FILTER);
                    StringBuilder builder = new StringBuilder();
                    String separator = "";
                    for (String filter : filterArray) {
                        builder.append(separator);
                        builder.append(filter);
                        separator = " | ";
                    }
                    holder.saveSearchFilter.setText(builder.toString().toLowerCase());
                    if (text.length > 2) {
                        holder.saveSearchPlace.setText(text[2].toLowerCase());
                    } else {
                        holder.saveSearchPlace.setVisibility(View.INVISIBLE);
                    }
                } else {
                    holder.saveSearchPlace.setVisibility(View.INVISIBLE);
                    holder.saveSearchFilter.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.saveSearchName.setVisibility(View.INVISIBLE);
                holder.saveSearchPlace.setVisibility(View.INVISIBLE);
                holder.saveSearchFilter.setVisibility(View.INVISIBLE);
            }

            if (newMatchesCount != null && newMatchesCount.containsKey(String.valueOf(savedSearches.get(position).id))) {
                holder.newMatchesCountTextView.setText(newMatchesCount.get(String.valueOf(savedSearches.get(position).id)) + " new");
            }

            if (imageObjects.get(position).request == null) {
                imageObjects.get(position).request = new SerpRequest(SerpActivity.TYPE_SUGGESTION);
                SelectorParser.parse(savedSearches.get(position).searchQuery, imageObjects.get(position).request);
            }

            if (imageObjects.get(position).imageUrl == null) {
                HashMap<String, ArrayList<String>> map = imageObjects.get(position).request.getTermMap();
                if (map != null) {
                    for (String key : map.keySet()) {
                        if (KeyUtil.LOCALITY_ID.equalsIgnoreCase(key)) {
                            if (map.get(KeyUtil.LOCALITY_ID) != null && map.get(KeyUtil.LOCALITY_ID).size() > 0) {
                                imageObjects.get(position).requestLocalityImage(map.get(KeyUtil.LOCALITY_ID).get(0));
                                Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap("locality_placeholder");
                                if (bitmap == null) {
                                    int id = R.drawable.locality_placeholder;
                                    bitmap = BitmapFactory.decodeResource(getResources(), id);
                                    MakaanBuyerApplication.bitmapCache.putBitmap("locality_placeholder", bitmap);
                                }
                                holder.backgroundImageView.setLocalImageBitmap(bitmap);
                                break;
                            }
                        } else if (KeyUtil.CITY_ID.equalsIgnoreCase(key)) {
                            if (map.get(KeyUtil.CITY_ID) != null && map.get(KeyUtil.CITY_ID).size() > 0) {
                                imageObjects.get(position).requestCityImage(map.get(KeyUtil.CITY_ID).get(0));
                                Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap("city_placeholder");
                                if (bitmap == null) {
                                    int id = R.drawable.city_placeholder;
                                    bitmap = BitmapFactory.decodeResource(getResources(), id);
                                    MakaanBuyerApplication.bitmapCache.putBitmap("city_placeholder", bitmap);
                                }
                                holder.backgroundImageView.setLocalImageBitmap(bitmap);
                                break;
                            }
                        } else if (KeyUtil.BUILDER_ID.equalsIgnoreCase(key)) {
                            if (map.get(KeyUtil.BUILDER_ID) != null && map.get(KeyUtil.BUILDER_ID).size() > 0) {
                                imageObjects.get(position).requestBuilderImage(map.get(KeyUtil.BUILDER_ID).get(0));
                                Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap("builder_placeholder");
                                if (bitmap == null) {
                                    int id = R.drawable.builder_placeholder;
                                    bitmap = BitmapFactory.decodeResource(getResources(), id);
                                    MakaanBuyerApplication.bitmapCache.putBitmap("builder_placeholder", bitmap);
                                }

                                holder.backgroundImageView.setLocalImageBitmap(bitmap);
                                break;
                            }
                        }
                    }
                }
            } else {
                Configuration configuration = getResources().getConfiguration();
                int width = (int) (configuration.screenHeightDp * Resources.getSystem().getDisplayMetrics().density);
                int height = getResources().getDimensionPixelSize(R.dimen.buyer_content_image_height);
//                Log.d("debug", "position : " + position + ", img = " + ImageUtils.getImageRequestUrl(imageObjects.get(position).imageUrl, width, height, false));
                holder.backgroundImageView.setImageUrl(ImageUtils.getImageRequestUrl(imageObjects.get(position).imageUrl, width, height, false), MakaanNetworkClient.getInstance().getImageLoader());
            }
        }

        @Override
        public int getItemCount() {
            return savedSearches.size();
        }

    }

    @Subscribe
    public void onResults(SaveSearchGetEvent saveSearchGetEvent) {
        if (null == saveSearchGetEvent || null != saveSearchGetEvent.error) {
            if (saveSearchGetEvent != null && saveSearchGetEvent.error != null
                    && saveSearchGetEvent.error.error != null && saveSearchGetEvent.error.error.networkResponse != null
                    && saveSearchGetEvent.error.error.networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                if (mCallback != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(BlogContentFragment.TYPE, BlogContentFragment.SEARCH);
                    mCallback.loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT, false, bundle, null, null);
                }
            } else if (saveSearchGetEvent != null && !TextUtils.isEmpty(saveSearchGetEvent.error.msg)) {
                showNoResults(saveSearchGetEvent.error.msg);
            } else {
                showNoResults();
            }
            return;
        }
        if (saveSearchGetEvent.saveSearchArrayList == null || saveSearchGetEvent.saveSearchArrayList.size() == 0) {
//            showNoResults(ErrorUtil.getErrorMessageId(ErrorUtil.STATUS_CODE_NO_CONTENT));
            if (mCallback != null) {
                Bundle bundle = new Bundle();
                bundle.putString(BlogContentFragment.TYPE, BlogContentFragment.SEARCH);
                mCallback.loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT, false, bundle, null, null);
            }
        } else {
            mAdapter = new SaveSearchAdapter(saveSearchGetEvent.saveSearchArrayList);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
            ArrayList<Long> ids = new ArrayList<>();
            for (SaveSearch search : saveSearchGetEvent.saveSearchArrayList) {
                ids.add(search.id);
            }
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearchesNewMatchesByIds(ids);
        }
    }


    @Subscribe
    public void onResults(NewMatchesGetEvent event) {
        if (null == event || null != event.error) {
            return;
        }
        if (event.data != null && event.data.size() > 0) {
            mAdapter.updateNewCount(event.data);
        }
    }

    class ImageObject {
        String imageUrl;
        SerpRequest request;

        public void requestLocalityImage(String localityId) {
            String localityUrl = ApiConstants.LOCALITY.concat(localityId);

            Selector localitySelector = new Selector();

            localitySelector.fields(new String[]{"localityHeroshotImageUrl"});

            localityUrl = localityUrl.concat("?").concat(localitySelector.build());
            Type localityType = new TypeToken<Locality>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(localityUrl, localityType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {

                }

                @Override
                public void onSuccess(Object responseObject) {
                    Locality locality = (Locality) responseObject;
                    if (locality != null && locality.localityHeroshotImageUrl != null) {
                        ImageObject.this.imageUrl = locality.localityHeroshotImageUrl;
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        public void requestCityImage(String cityId) {

            if (null != cityId) {
                Selector citySelector = new Selector();

                //citySelector.fields(new String[]{ID, ENTITY_DESCRIPTIONS, CITY_TAG_LINE, CENTER_LAT, CENTER_LONG, DESCRIPTION, CITY_HEROSHOT_IMAGE_URL, ANNUAL_GROWTH, RENTAL_YIELD, DEMAND_RATE, SUPPLY_RATE, LABEL});

                citySelector.fields(new String[]{"cityHeroshotImageUrl"});


                String cityUrl = ApiConstants.CITY.concat(cityId.toString()).concat("?").concat(citySelector.build());

                Type cityType = new TypeToken<City>() {
                }.getType();

                MakaanNetworkClient.getInstance().get(cityUrl, cityType, new ObjectGetCallback() {
                    @Override
                    public void onError(ResponseError error) {
                    }

                    @Override
                    public void onSuccess(Object responseObject) {
                        City city = (City) responseObject;
                        if (city != null && city.cityHeroshotImageUrl != null) {
                            ImageObject.this.imageUrl = city.cityHeroshotImageUrl;
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }

        public void requestBuilderImage(String builderId) {
            if (null != builderId) {
                Selector builderSelector = new Selector();
                builderSelector.fields(new String[]{"imageURL"});

                String builderUrl = ApiConstants.BUILDER_DETAIL.concat("/").concat(builderId.toString()).concat("?").concat(builderSelector.build());

                Type builderType = new TypeToken<Builder>() {
                }.getType();

                MakaanNetworkClient.getInstance().get(builderUrl, builderType, new ObjectGetCallback() {
                    @Override
                    public void onError(ResponseError error) {
                    }

                    @Override
                    public void onSuccess(Object responseObject) {
                        Builder builder = (Builder) responseObject;

                        if (builder != null && builder.imageURL != null) {
                            ImageObject.this.imageUrl = builder.imageURL;
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }


    private void createSaveSearchEvent(List<ImageObject> imageObjects, int position) {
            HashMap<String, ArrayList<String>> map = imageObjects.get(position).request.getTermMap();
            if (map != null) {
                for (String key : map.keySet()) {
                    if (KeyUtil.LOCALITY_ID.equalsIgnoreCase(key)) {
                        Properties properties= MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.locality,
                                   map.get(KeyUtil.LOCALITY_ID).get(0)));
                        MakaanEventPayload.endBatch(getContext(),MakaanTrackerConstants.Action.clickSavedSearches);
                        break;

                    } else if (KeyUtil.CITY_ID.equalsIgnoreCase(key)) {
                        Properties properties= MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.city,
                                map.get(KeyUtil.CITY_ID).get(0)));
                        MakaanEventPayload.endBatch(getContext(),MakaanTrackerConstants.Action.clickSavedSearches);
                        break;

                    } else if (KeyUtil.BUILDER_ID.equalsIgnoreCase(key)) {
                        Properties properties= MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.builder,
                                map.get(KeyUtil.BUILDER_ID).get(0)));
                        MakaanEventPayload.endBatch(getContext(),MakaanTrackerConstants.Action.clickSavedSearches);
                        break;

                    }
                }
            }
    }


}

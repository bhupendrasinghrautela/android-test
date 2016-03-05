package com.makaan.fragment.buyerJourney;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import com.android.volley.toolbox.ImageRequest;
import com.google.gson.reflect.TypeToken;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.event.builder.BuilderByIdEvent;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
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
import com.makaan.response.serp.TermFilter;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.makaan.util.AppBus;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.squareup.otto.Subscribe;

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

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_save_search;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();

        if(MasterDataCache.getInstance().getSavedSearch() != null) {

            mAdapter = new SaveSearchAdapter(MasterDataCache.getInstance().getSavedSearch());
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    private void initView() {
        LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    private class SaveSearchAdapter extends RecyclerView.Adapter<SaveSearchAdapter.ViewHolder> {
        private List<SaveSearch> savedSearches;
        private List<ImageObject> imageObjects;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ImageView deleteImageView;
            private final FadeInNetworkImageView backgroundImageView;
            // each data item is just a string in this case
            public TextView saveSearchName;
            public TextView saveSearchFilter;
            public TextView saveSearchPlace;
            public Long searchId;
            private int position;

            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                backgroundImageView = (FadeInNetworkImageView) v.findViewById(R.id.iv_search);
                saveSearchName = (TextView) v.findViewById(R.id.search_name);
                saveSearchFilter = (TextView) v.findViewById(R.id.filter_name);
                saveSearchPlace = (TextView) v.findViewById(R.id.place_name);
                deleteImageView = (ImageView) v.findViewById(R.id.iv_cancel);
                deleteImageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(v instanceof ImageView) {
                    if(searchId != null) {
                        ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).removeSavedSearch(searchId);
                    }
                } else if(savedSearches != null && savedSearches.size() > position) {
                    SerpRequest request = new SerpRequest(SerpActivity.TYPE_SUGGESTION);
                    request.launchSerp(getActivity(), savedSearches.get(position).searchQuery);
                }
            }
        }

        public SaveSearchAdapter(List<SaveSearch> savedSearches) {
            if(this.savedSearches == null) {
                this.savedSearches = new ArrayList<>();
            } else {
                this.savedSearches.clear();
            }
            this.savedSearches.addAll(savedSearches);

            if(this.imageObjects == null) {
                this.imageObjects = new ArrayList<>();
            } else {
                this.imageObjects.clear();
            }
            for(int i = 0; i < savedSearches.size(); i++) {
                this.imageObjects.add(new ImageObject());
            }
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
            if(!TextUtils.isEmpty(name)) {
                String[] text = name.split(";");
                holder.saveSearchName.setVisibility(View.VISIBLE);
                holder.saveSearchName.setText(text[0]);
                if(text.length > 1) {
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
                    holder.saveSearchFilter.setText(builder.toString());
                    if(text.length > 2) {
                        holder.saveSearchPlace.setText(text[2]);
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

            if(imageObjects.get(position).request == null) {
                imageObjects.get(position).request = new SerpRequest(SerpActivity.TYPE_SUGGESTION);
                SelectorParser.parse(savedSearches.get(position).searchQuery, imageObjects.get(position).request);
            }

            if(imageObjects.get(position).imageUrl == null) {
                HashMap<String, ArrayList<String>> map = imageObjects.get(position).request.getTermMap();
                if(map != null) {
                    for(String key : map.keySet()) {
                        if(KeyUtil.LOCALITY_ID.equalsIgnoreCase(key)) {
                            if(map.get(KeyUtil.LOCALITY_ID) != null && map.get(KeyUtil.LOCALITY_ID).size() > 0) {
                                imageObjects.get(position).requestLocalityImage(map.get(KeyUtil.LOCALITY_ID).get(0));
                                break;
                            }
                        } else if(KeyUtil.CITY_ID.equalsIgnoreCase(key)) {
                            if(map.get(KeyUtil.CITY_ID) != null && map.get(KeyUtil.CITY_ID).size() > 0) {
                                imageObjects.get(position).requestCityImage(map.get(KeyUtil.CITY_ID).get(0));
                                break;
                            }
                        } else if(KeyUtil.BUILDER_ID.equalsIgnoreCase(key)) {
                            if(map.get(KeyUtil.BUILDER_ID) != null && map.get(KeyUtil.BUILDER_ID).size() > 0) {
                                imageObjects.get(position).requestBuilderImage(map.get(KeyUtil.BUILDER_ID).get(0));
                                break;
                            }
                        }
                    }
                }
                holder.backgroundImageView.setDefaultImageResId(R.drawable.locality_hero);
            } else {
                Configuration configuration = getResources().getConfiguration();
                int width = (int) (configuration.screenHeightDp * Resources.getSystem().getDisplayMetrics().density);
                int height = getResources().getDimensionPixelSize(R.dimen.buyer_content_image_height);
                Log.d("debug", "position : " + position + ", img = " + ImageUtils.getImageRequestUrl(imageObjects.get(position).imageUrl, width, height, false));
                holder.backgroundImageView.setImageUrl(ImageUtils.getImageRequestUrl(imageObjects.get(position).imageUrl, width, height, false), MakaanNetworkClient.getInstance().getImageLoader());
            }
        }

        @Override
        public int getItemCount() {
            return savedSearches.size();
        }

    }


    @Subscribe
    public void onResults(SaveSearchGetEvent saveSearchGetEvent){
        if(null== saveSearchGetEvent || null!=saveSearchGetEvent.error){
            //TODO handle error
            return;
        }
        SaveSearchGetEvent saveSearchGetEvent1 = saveSearchGetEvent;
        mAdapter = new SaveSearchAdapter(saveSearchGetEvent.saveSearchArrayList);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
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
                    ImageObject.this.imageUrl = locality.localityHeroshotImageUrl;
                    mAdapter.notifyDataSetChanged();
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
                        ImageObject.this.imageUrl = city.cityHeroshotImageUrl;
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        public void requestBuilderImage(String builderId) {
            if (null != builderId) {
                Selector builderSelector = new Selector();
                builderSelector.fields(new String[]{"imageURL"});

                String builderUrl = ApiConstants.BUILDER_DETAIL.concat("/").concat(builderId.toString()).concat("?").concat(builderSelector.build());

                Type builderType = new TypeToken<Builder>() {}.getType();

                MakaanNetworkClient.getInstance().get(builderUrl, builderType, new ObjectGetCallback() {
                    @Override
                    public void onError(ResponseError error) {
                    }

                    @Override
                    public void onSuccess(Object responseObject) {
                        Builder builder = (Builder) responseObject;

                        ImageObject.this.imageUrl = builder.imageURL;
                    }
                });
            }
        }
    }
}

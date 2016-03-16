package com.makaan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.project.Project;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by rohitgarg on 2/18/16.
 */
public class RecentPropertyProjectManager {
    private static final String SHARED_PREF_KEY = "recent_project_properties";
    private static final String PREF_KEY = "key";
    private static final int MAX_ENTRIES = 20;
    public static final String TYPE_PROPERTY = "property";
    public static final String TYPE_PROJECT = "project";

    ArrayList<DataObject> recentProjectPropertyList = new ArrayList<DataObject>(MAX_ENTRIES);

    private static RecentPropertyProjectManager manager;

    public static RecentPropertyProjectManager getInstance(Context context) {
        if(manager == null) {
            synchronized (RecentSearchManager.class) {
                if(manager == null) {
                    manager = new RecentPropertyProjectManager(context);
                }
            }
        }
        return manager;
    }

    public RecentPropertyProjectManager(Context context) {
        loadPropertyProjectEntries(context);
    }

    private void loadPropertyProjectEntries(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String searchJsonString = Preference.getString(preferences, PREF_KEY);

        if(searchJsonString != null) {
            Type apiPriceTrendType = new TypeToken<ArrayList<DataObject>>() {}.getType();
            recentProjectPropertyList = MakaanBuyerApplication.gson.fromJson(searchJsonString, apiPriceTrendType);
        }
    }

    public void addEntryToRecent(DataObject propertyProject, Context context) {
        for (Iterator<DataObject> iterator = recentProjectPropertyList.iterator(); iterator.hasNext();) {
            DataObject s = iterator.next();
            if (s.id == propertyProject.id) {
                iterator.remove();
            }
        }
        recentProjectPropertyList.add(0, propertyProject);
        if(recentProjectPropertyList.size() > MAX_ENTRIES) {
            recentProjectPropertyList.remove(MAX_ENTRIES);
        }

        String propertyProjectJsonString = MakaanBuyerApplication.gson.toJson(recentProjectPropertyList);

        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        Preference.putString(edit, PREF_KEY, propertyProjectJsonString);
        edit.apply();
    }

    public boolean contain(long id, String type) {
        if(recentProjectPropertyList != null) {
            for (DataObject obj : recentProjectPropertyList) {
                if(obj.type.equalsIgnoreCase(type)) {
                    if(obj.id == id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean containsProperty(long id) {
        return this.contain(id, TYPE_PROPERTY);
    }

    public boolean containsProject(long id) {
        return this.contain(id, TYPE_PROJECT);
    }

    public ArrayList<DataObject> getRecentEntries(Context context) {
        return recentProjectPropertyList;
    }

    public class DataObject {
        long id;
        public String addressLine1;
        public String addressLine2;
        public double price, minPrice, maxPrice;
        public String type;
        public String imageUrl;
        public String sellerName;
        public String phoneNo;
        public double rating;
        public long sellerId;
        public long cityId;
        public long localityId;

        public DataObject() {}

        public DataObject(long id, String addressLine1, String addressLine2, double price, String type) {
            this.id = id;
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
            this.price = price;
            this.type = type;
        }

        public DataObject(long id, String addressLine1, String addressLine2, double minPrice, double maxPrice, String type) {
            this.id = id;
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.type = type;
        }

        public DataObject(ListingDetail listingDetail) {
            if(listingDetail != null) {
                this.id = listingDetail.id;
                this.type = TYPE_PROPERTY;
                if(listingDetail.currentListingPrice != null) {
                    this.price = listingDetail.currentListingPrice.price;
                }

                // todo check for phone no
                if(listingDetail.mainImageURL != null && !TextUtils.isEmpty(listingDetail.mainImageURL)) {
                    this.imageUrl = listingDetail.mainImageURL;
                }

                if(listingDetail.companySeller != null) {
                    if(listingDetail.companySeller.company != null) {
                        if(listingDetail.companySeller.company.id != null) {
                            this.sellerId = listingDetail.companySeller.company.id;
                        }

                        if(listingDetail.companySeller.company.name != null
                                && !TextUtils.isEmpty(listingDetail.companySeller.company.name)) {
                            this.sellerName = listingDetail.companySeller.company.name;
                        } else if(listingDetail.companySeller.user != null && listingDetail.companySeller.user.fullName != null
                                && !TextUtils.isEmpty(listingDetail.companySeller.user.fullName)){
                            this.sellerName = listingDetail.companySeller.user.fullName;
                        }
                        if(listingDetail.companySeller.company.score != null) {
                            this.rating = listingDetail.companySeller.company.score;
                        }
                    }
                }

                if(listingDetail.property != null) {
                    if(listingDetail.property.project != null) {
                        if(!TextUtils.isEmpty(listingDetail.property.project.name)) {
                            this.addressLine1 = listingDetail.property.project.name;
                        }
                        if(listingDetail.property.project.locality != null) {
                            if(!TextUtils.isEmpty(listingDetail.property.project.locality.label)) {
                                this.addressLine2 = listingDetail.property.project.locality.label;
                                if(listingDetail.property.project.locality.localityId != null) {
                                    this.localityId = listingDetail.property.project.locality.localityId;
                                }
                                if(listingDetail.property.project.locality.cityId != null) {
                                    this.localityId = listingDetail.property.project.locality.cityId;
                                }
                            }
                            if(listingDetail.property.project.locality.suburb != null) {
                                if(listingDetail.property.project.locality.suburb.city != null) {
                                    if(!TextUtils.isEmpty(listingDetail.property.project.locality.suburb.city.label)) {
                                        if(this.addressLine2 == null) {
                                            this.addressLine2 = listingDetail.property.project.locality.suburb.city.label;
                                        } else {
                                            this.addressLine2 = this.addressLine2.concat(", ").concat(listingDetail.property.project.locality.suburb.city.label);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        public DataObject(Project project) {
            if(project != null) {
                this.id = project.projectId;
                this.type = TYPE_PROJECT;
                if(project.minPrice != null && !Double.isNaN(project.minPrice)) {
                    this.minPrice = project.minPrice;
                }
                if(project.maxPrice != null && !Double.isNaN(project.maxPrice)) {
                    this.maxPrice = project.maxPrice;
                }
                if(!TextUtils.isEmpty(project.name)) {
                    this.addressLine1 = project.name;
                }

                if(project.imageURL != null && !TextUtils.isEmpty(project.imageURL)) {
                    this.imageUrl = project.imageURL;
                }

                if(project.locality != null) {
                    if(!TextUtils.isEmpty(project.locality.label)) {
                        this.addressLine2 = project.locality.label;
                    }
                    if(project.locality.suburb != null) {
                        if(project.locality.suburb.city != null) {
                            if(!TextUtils.isEmpty(project.locality.suburb.city.label)) {
                                if(this.addressLine2 == null) {
                                    this.addressLine2 = project.locality.suburb.city.label;
                                } else {
                                    this.addressLine2 = this.addressLine2.concat(", ").concat(project.locality.suburb.city.label);
                                }
                            }
                        }
                    }
                }
            }
        }

        public DataObject(ProjectConfigEvent projectConfigEvent) {
            if(projectConfigEvent != null) {
                this.type = TYPE_PROJECT;
            }
        }

        public void updateProjectData(Project project) {
            if(project != null) {
                this.id = project.projectId;
                if(project.minPrice != null && !Double.isNaN(project.minPrice)) {
                    this.minPrice = project.minPrice;
                }
                if(project.maxPrice != null && !Double.isNaN(project.maxPrice)) {
                    this.maxPrice = project.maxPrice;
                }
                if(!TextUtils.isEmpty(project.name)) {
                    this.addressLine1 = project.name;
                }

                if(project.imageURL != null && !TextUtils.isEmpty(project.imageURL)) {
                    this.imageUrl = project.imageURL;
                }

                if(project.locality != null) {
                    if(!TextUtils.isEmpty(project.locality.label)) {
                        this.addressLine2 = project.locality.label;
                    }
                    if(project.locality.suburb != null) {
                        if(project.locality.suburb.city != null) {
                            if(!TextUtils.isEmpty(project.locality.suburb.city.label)) {
                                if(this.addressLine2 == null) {
                                    this.addressLine2 = project.locality.suburb.city.label;
                                } else {
                                    this.addressLine2 = this.addressLine2.concat(", ").concat(project.locality.suburb.city.label);
                                }
                            }
                        }
                    }
                }
            }
        }

        public void updateProjectData(ProjectConfigEvent projectConfigEvent) {

        }
    }
}

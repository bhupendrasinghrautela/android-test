package com.makaan.pojo;

import android.util.Range;

import com.crashlytics.android.Crashlytics;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.constants.RequestConstants;
import com.makaan.util.KeyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by rohitgarg on 2/14/16.
 */
public class SelectorParser {
    Filter filters;

    public static SelectorParser parse(String json) {
        if (json.indexOf(RequestConstants.SELECTOR) >= 0 && RequestConstants.SELECTOR.length() + 1 < json.length()) {
            json = json.substring(RequestConstants.SELECTOR.length() + 1);
        }
        if (json.indexOf("&facets=") >= 0) {
            json = json.substring(0, json.indexOf("&facets="));
        }
        Type type = new TypeToken<SelectorParser>() {
        }.getType();
        SelectorParser parser = MakaanBuyerApplication.gson.fromJson(json, type);
        return parser;
    }

    public static void parse(String json, SerpRequest request) {
        if (json.indexOf(RequestConstants.SELECTOR) >= 0 && RequestConstants.SELECTOR.length() + 1 < json.length()) {
            json = json.substring(RequestConstants.SELECTOR.length() + 1);
        }
        if (json.indexOf("&facets=") >= 0) {
            json = json.substring(0, json.indexOf("&facets="));
        }
        try {
            JSONObject object = new JSONObject(json);
            JSONObject filters = object.getJSONObject("filters");
            JSONArray and = filters.getJSONArray("and");
            for (int i = 0; i < and.length(); i++) {
                try {
                    JSONObject obj = and.getJSONObject(i);
                    if (obj.has("equal")) {
                        JSONObject equal = obj.getJSONObject("equal");
                        Iterator<String> keyIterator = equal.keys();
                        while (keyIterator.hasNext()) {
                            String key = keyIterator.next();
                            if (key == KeyUtil.CITY_ID) {
                                String cityId = equal.getString(key);
                                request.addTerm(key, cityId);
                            }
                            JSONArray array = equal.getJSONArray(key);
                            for (int j = 0; j < array.length(); j++) {
                                if (request != null) {
                                    request.addTerm(key, array.getString(j));
                                }
                            }
                        }
                    } else if (obj.has("range")) {
                        JSONObject range = obj.getJSONObject("range");
                        Iterator<String> keyIterator = range.keys();
                        while (keyIterator.hasNext()) {
                            String key = keyIterator.next();
                            JSONObject keyObj = range.getJSONObject(key);
                            Long from = null, to = null;
                            if (keyObj.has("from")) {
                                from = keyObj.getLong("from");
                            }
                            if (keyObj.has("to")) {
                                to = keyObj.getLong("to");
                            }
                            if (request != null) {
                                request.addRange(key, from, to);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public void applySelections(SerpRequest request) {
        if (filters != null && filters.and != null && filters.and.size() > 0) {
            for (And and : filters.and) {
                if (and != null) {
                    and.applySelection(request);
                }
            }
        }
    }

    class Filter {
        ArrayList<And> and;
    }

    class And {
        Equal equal;
        Range range;

        public void applySelection(SerpRequest request) {
            if (equal != null) {
                equal.applyEqual(request);
            } else if (range != null) {
                range.applyRange(request);
            }
        }
    }

    class Equal {
        @SerializedName(KeyUtil.BEDROOM)
        ArrayList<String> bedrooms;
        @SerializedName(KeyUtil.PROPERTY_TYPES)
        ArrayList<String> unitTypeId;
        @SerializedName(KeyUtil.BEDROOM)
        ArrayList<String> bathrooms;
        @SerializedName(KeyUtil.LISTING_CATEGORY)
        ArrayList<String> listingCategory;
        @SerializedName(KeyUtil.LISTING_SELLER_COMPANY_TYPE)
        ArrayList<String> listingSellerCompanyType;
        @SerializedName(KeyUtil.LOCALITY_ID)
        ArrayList<String> localityId;
        @SerializedName(KeyUtil.CITY_ID)
        ArrayList<String> cityId;
        @SerializedName(KeyUtil.LISTING_SELLER_COMPANY_ASSIST)
        ArrayList<String> listingSellerCompanyAssist;

        private void applyEqual(SerpRequest request) {
            if (bedrooms != null) {
                for (String bedroom : bedrooms) {
                    try {
                        request.setBedrooms(Integer.valueOf(bedroom));
                    } catch (NumberFormatException ex) {
                        String[] val = bedroom.split(FiltersViewAdapter.MIN_MAX_SEPARATOR);
                        int min = Integer.valueOf(val[0]);
                        int max = Integer.valueOf(val[1]);
                        for (int i = min; i <= max; i++) {
                            request.setBedrooms(i);
                        }
                    }
                }
            } else if (unitTypeId != null) {
                for (String unitType : unitTypeId) {
                    // TODO
                }
            } else if (bathrooms != null) {
                for (String bathroom : bathrooms) {
                    try {
                        request.setBedrooms(Integer.valueOf(bathroom));
                    } catch (NumberFormatException ex) {
                        String[] val = bathroom.split(FiltersViewAdapter.MIN_MAX_SEPARATOR);
                        int min = Integer.valueOf(val[0]);
                        int max = Integer.valueOf(val[1]);
                        for (int i = min; i <= max; i++) {
                            request.setBedrooms(i);
                        }
                    }
                }
            } else if (listingCategory != null) {
                for (String category : listingCategory) {
                    if ("Primary".equalsIgnoreCase(category)) {
                        request.addSerpContext(SerpRequest.CONTEXT_PRIMARY);
                    } else if ("Resale".equalsIgnoreCase(category)) {
                        request.addSerpContext(SerpRequest.CONTEXT_RESALE);
                    } else if ("Rental".equalsIgnoreCase(category)) {
                        request.addSerpContext(SerpRequest.CONTEXT_RENT);
                    }
                }
            } else if (listingSellerCompanyType != null) {
                for (String companyType : listingSellerCompanyType) {
                    // TODO
                }
            } else if (localityId != null) {
                for (String locality : localityId) {
                    request.setLocalityId(Long.valueOf(locality));
                }
            } else if (cityId != null) {
                for (String city : cityId) {
                    request.setCityId(Long.valueOf(city));
                }
            } else if (listingSellerCompanyAssist != null) {
                //TODO
            }
        }
    }

    class Range {
        RangeElement price;
        RangeElement listingMinConstructionCompletionDate;
        RangeElement listingMaxConstructionCompletionDate;
        RangeElement size;

        private void applyRange(SerpRequest request) {

        }
    }

    class RangeElement {
        Double from;
        Double to;
    }
}

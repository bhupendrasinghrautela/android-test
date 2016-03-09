package com.makaan.fragment.pyr;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.pyr.PyrOtpVerification;

import com.makaan.cache.MasterDataCache;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.ui.pyr.FilterableMultichoiceDialogFragment;
import com.makaan.ui.pyr.PyrBudgetCardView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by proptiger on 7/1/16.
 */
public class PyrPagePresenter {
    private static final String STRING_LACS = " Lacs";
    private static final String STRING_CRORE = " Cr";
    private static final String STRING_THOUSANDS=" K";
    public static String MAKAAN_ASSIST_VALUE = "makaan_assist";
    public static String NO_SELLERS_FRAGMENT = "no_seller";
    public static final long MAXIMUM_BUY_BUDGET = 500000000l;
    public static final long MAXIMUM_RENT_BUDGET = 1000000l;
    private boolean RENT_SELECTED=false;
    private boolean BUY_SELECTED=true;
    private static PyrPagePresenter pyrPagePresenter;
    private static PyrRequest mPyrRequest=new PyrRequest();
    private PyrReplaceFragment mReplaceFragment;
    private PyrPageFragment mPyrFragment;
    private TopSellersFragment mTopSellersFragment;
    private PropertyTypeFragment mPropertyTypeFragment;
    private ThankYouScreenFragment mThankYouFragment;
    private PyrOtpVerification mPyrOtpVerification;
    private FilterableMultichoiceDialogFragment mPropertySearchFragment;
    private NoSellersFragment mNoSellersFragment;
    private ArrayList<String> list = new ArrayList<String>();
    private boolean[] mSelectedLocalitiesFlag;
    private ArrayList<SearchResponseItem> locaityIds=new ArrayList<SearchResponseItem>();
    private HashMap<Long ,Boolean> mSellerIdMap=new HashMap<Long, Boolean>();
    private PyrBudgetCardView pyrBudgetCardView;
    private List<Integer>bedroomList=new ArrayList<Integer>();
    private String userName=null, userEmail=null, phoneNumber=null, countryName="India";
    private int countryId=1;
    private String mCityContext;
    public double alreadySelectedMinBudget=0l,alreadySelectedMaxBudget=0l;
     private boolean pyrFromProjectBuySelected;
    private boolean fromProject;
    private Integer mCityId=null;
    ArrayList<TopAgent> mTopAgentsDatas;

    public ArrayList<TopAgent> getmTopAgentsDatas() {
        return mTopAgentsDatas;
    }

    public void setmTopAgentsDatas(ArrayList<TopAgent> mTopAgentsDatas) {
        this.mTopAgentsDatas = mTopAgentsDatas;
    }

    public PyrPagePresenter() {

    }

    public static PyrPagePresenter getPyrPagePresenter() {
        if (pyrPagePresenter == null) {
            pyrPagePresenter = new PyrPagePresenter();
        }
        return pyrPagePresenter;
    }

    public boolean isFromProject() {
        return fromProject;
    }

    public boolean isPyrFromProjectBuySelected() {
        return pyrFromProjectBuySelected;
    }

    public double getAlreadySelectedMinBudget() {
        return alreadySelectedMinBudget;
    }

    public double getAlreadySelectedMaxBudget() {
        return alreadySelectedMaxBudget;
    }

    public void setReplaceFragment(PyrReplaceFragment replaceFragment) {
        mReplaceFragment = replaceFragment;
    }

    public void showPyrMainPageFragment() {
        mPyrFragment = new PyrPageFragment();
        mReplaceFragment.replaceFragment(mPyrFragment, true);
    }

    public void showPropertySearchFragment() {
        mSelectedLocalitiesFlag = new boolean[list.size()];
        mPropertySearchFragment = new FilterableMultichoiceDialogFragment();
        mReplaceFragment.showPropertySearchFragment(mPropertySearchFragment);
    }

    public void showTopSellersFragment() {
        mTopSellersFragment = new TopSellersFragment();
        mReplaceFragment.replaceFragment(mTopSellersFragment, true);
    }

    public void showPropertyTypeFragment(ArrayList<TermFilter> termFilter) {
        mPropertyTypeFragment = new PropertyTypeFragment();
        mPropertyTypeFragment.setValues(termFilter);
        mReplaceFragment.replaceFragment(mPropertyTypeFragment, true);
    }

    public PyrOtpVerification showPyrOtpFragment() {
        mPyrOtpVerification = new PyrOtpVerification();
        mReplaceFragment.replaceFragment(mPyrOtpVerification, true);
        return mPyrOtpVerification;
    }

    public void showThankYouScreenFragment(boolean makaanAssist, boolean throughOtpScreen, boolean throughNoSellers) {
        mThankYouFragment = new ThankYouScreenFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(MAKAAN_ASSIST_VALUE, makaanAssist);
        bundle.putBoolean(NO_SELLERS_FRAGMENT, throughNoSellers);
        mThankYouFragment.setArguments(bundle);
        if(throughOtpScreen) {
            mReplaceFragment.popFromBackstack(2);
        }
        else{
            mReplaceFragment.popFromBackstack(1);
        }
        mReplaceFragment.replaceFragment(mThankYouFragment, false);
    }

    public void showNoSellersFragment() {
        mNoSellersFragment= new NoSellersFragment();
        mReplaceFragment.replaceFragment(mNoSellersFragment, true);
    }

    public void updateSelectedItemsList(String clickedItem, ArrayList<SearchResponseItem> list, SearchResponseItem searchResponseItem) {
        boolean flag = true;
        for (int i = 0; i < list.size(); i++) {
            if (clickedItem.equalsIgnoreCase(list.get(i).entityId)) {
                flag = false;
                break;
            } else {
                flag = true;
            }
        }
        if (flag) {
            list.add(searchResponseItem);
        }
    }

    public ArrayList<SearchResponseItem> getSelectedItemList(String clickedItem, ArrayList<SearchResponseItem> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (clickedItem.equalsIgnoreCase(list.get(i).entityName)) {
                //list.remove(i);
                index = i;
                break;
            }
        }
        if (index != -1) {
            list.remove(index);
        }
        return list;
    }

    public void setLocalityIds(SearchResponseItem searchResponseItem, Context context) {
        if (locaityIds.size() < 6) {

            boolean flag = false;
            for (SearchResponseItem searchResponseItemObj : locaityIds) {
                if (searchResponseItemObj.entityId.equals(searchResponseItem.entityId)) {
                    flag = true;
                }
            }

            if (!flag) {
                if (locaityIds.size() >= 0) {
                    locaityIds.add(searchResponseItem);
                }
            }
        } else if (locaityIds.size() == 6) {
            Toast.makeText(context, "Only a maximum of 6 listings can be added", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeLocalityId(SearchResponseItem searchResponseItem) {
        boolean flag = false;
        for (SearchResponseItem searchResponseItemObj : locaityIds) {
            if (searchResponseItemObj.entityId.equals(searchResponseItem.entityId)) {
                flag = true;
            }
        }
        if (flag) {
            locaityIds.remove(searchResponseItem);
        }

    }

    public ArrayList<SearchResponseItem> getAlreadySelectedProjects() {
        return locaityIds;
    }

    public String getPrice(long price_value_lacs) {
        if(BUY_SELECTED) {
            String priceString = null;

            if (price_value_lacs < 10000000) {
                priceString = String.valueOf(price_value_lacs / 100000).concat(
                        STRING_LACS);
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                double converInCr = (double) price_value_lacs / 10000000;
                df.format(converInCr);
                if (converInCr > 5) {
                    return "5+Cr";
                }
                String price = String.format("%.2f", converInCr);
                priceString = price.concat(STRING_CRORE);
            }

            return priceString;
        } else {
            return getRentPrice(price_value_lacs);
        }
    }

    public String getRentPrice(long price_value_lacs) {
        String priceString = null;

        if (price_value_lacs < 100000) {
            DecimalFormat df = new DecimalFormat("#.##");
            double converInCr = (double) price_value_lacs / 1000;
            df.format(converInCr);
            String price = String.format("%.2f", converInCr);
            priceString = price.concat(STRING_THOUSANDS);

        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            double converInCr = (double) price_value_lacs / 100000;
            df.format(converInCr);
            if(converInCr>2.5){
                return "2.5L+";
            }
            String price = String.format("%.2f", converInCr);
            priceString = price.concat(STRING_LACS);
        }

        return priceString;
    }


    public boolean isValidEmail(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isValidPhoneNumber(CharSequence target) {

        if(countryName.equalsIgnoreCase("India")){
           if(target.length()==10 && Integer.parseInt(String.valueOf(String.valueOf(target).charAt(0)))>=7 ){
               return true;
           }
           else {
               return false;
           }
        }
        else if(!countryName.equalsIgnoreCase("India") && target.length()>=6 && target.length()<=12){
            return true;
        }
        else {
            return false;
        }

    }

    public void setSellerIds(Long sellerId, boolean flag) {
        mSellerIdMap.put(sellerId, flag);
    }

    public HashMap<Long ,Boolean> getSellerIdMap(){
        return mSellerIdMap;
    }

    public boolean isMakkanAssist(){
        boolean assist=false;
        if(mSellerIdMap!=null && mTopAgentsDatas!=null && mSellerIdMap.size()>0 && mTopAgentsDatas.size()>0){
            for(TopAgent topAgent:mTopAgentsDatas){
                if(mSellerIdMap.containsKey(topAgent.agent.company.id) && mSellerIdMap.get(topAgent.agent.company.id)){
                    if(topAgent.agent.company.assist){
                        assist=true;
                        break;
                    }
                }
            }
        }
        return assist;
    }

    public boolean getSellerIdStatus(Long sellerId) {
        if(mSellerIdMap.containsKey(sellerId)) {
            return mSellerIdMap.get(sellerId);
        }
        else {
            return false;
        }
    }

    public boolean isRentSelected(){
        return RENT_SELECTED;
    }

    public boolean isBuySelected(){
        return BUY_SELECTED;
    }

    public void setBuySelected(boolean selected){
        RENT_SELECTED=!selected;
        BUY_SELECTED=selected;
        mPyrFragment.setBuySelected(selected);
    }

    public void setBudgetCardViewInstance(PyrBudgetCardView pyrBudgetCardView){
        this.pyrBudgetCardView=pyrBudgetCardView;
    }

    public void setLocalitiesOnPyrMainPage(){
        mPyrFragment.setLocaityInfo();
    }

    public void setPropertyTypeCount(){
        mPyrFragment.setPropertyCount();
    }

    public void setPyrPageFragmentInstance(PyrPageFragment pyrPageFragment){
        this.mPyrFragment=pyrPageFragment;
    }

    public void setName(String name){
        userName=name;
    }

    public String getName(){
        return userName;
    }

    public void setUserEmail(String email){
        userEmail=email;
    }

    public String getEmail(){
        return userEmail;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }

    public String getPhonNumber(){
        return phoneNumber;
    }

    public void setCountryName(String name){
        countryName=name;
    }

    public void setCountryId(int id) {
        countryId=id;
    }

    public PyrRequest getPyrRequestObject(){
        return mPyrRequest;
    }

    public boolean makePartialPyrRequest(Context context, PyrRequest pyrRequest, ArrayList<FilterGroup> groups){

        if(null!=userName && !userName.isEmpty()){
            pyrRequest.setName(userName);
        }
        else{
            Toast.makeText(context, context.getResources().getString(R.string.add_user_name_toast),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if(null!=userEmail && !userEmail.isEmpty() && isValidEmail(userEmail)){
            pyrRequest.setEmail(userEmail);
        }
        else{
            Toast.makeText(context,context.getResources().getString(R.string.invalid_email),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if(null!=phoneNumber && !phoneNumber.isEmpty() && isValidPhoneNumber(phoneNumber)){
            pyrRequest.setPhone(phoneNumber);
            pyrRequest.setCountry(countryName);
            pyrRequest.setCountryId(countryId);
            pyrRequest.setCityId(mCityId);
        }
        else {
            Toast.makeText(context,context.getResources().getString(R.string.invalid_phone_no_toast),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        for(FilterGroup group : groups) {
            String name = group.internalName;
            if("i_beds".equals(name)) {
                ArrayList<Integer> bhks = new ArrayList<>();
                for(TermFilter filter : group.termFilterValues) {
                    if(filter.selected) {
                        if(filter.displayName.contains("+")) {
                            String[] val = filter.value.split("-");
                            int min = Integer.valueOf(val[0]);
                            int max = Integer.valueOf(val[1]);
                            for(int i = min; i <= max; i++) {
                                bhks.add(i);
                            }
                        } else {
                            bhks.add(Integer.valueOf(filter.value));
                        }
                    }
                }
                if(bhks.size() > 0) {
                    pyrRequest.setBhk(bhks);
                }
            } else if("i_budget".equals(name)) {
                RangeFilter filter = group.rangeFilterValues.get(0);

                if(BUY_SELECTED){
                    pyrRequest.setSalesType("buy");
                    pyrRequest.setMinBudget(filter.selectedMinValue);
                    if(filter.selectedMaxValue == PyrBudgetCardView.MAX_BUY_BUDGET){
                        pyrRequest.setMaxBudget(MAXIMUM_BUY_BUDGET);
                    } else{
                        pyrRequest.setMaxBudget(filter.selectedMaxValue);
                    }
                }
                else{
                    pyrRequest.setSalesType("rent");
                    pyrRequest.setMinBudget(filter.selectedMinValue);
                    if(filter.selectedMaxValue == PyrBudgetCardView.MAX_RENT_BUDGET){
                        pyrRequest.setMaxBudget(MAXIMUM_BUY_BUDGET);
                    } else{
                        pyrRequest.setMaxBudget(filter.selectedMaxValue);
                    }
                }

            } else if("i_property_type".equals(name)) {
                ArrayList<String> selectedValues = new ArrayList<>();
                for(TermFilter filter : group.termFilterValues) {
                    if(filter.selected) {
                        selectedValues.add(filter.value);
                    }
                }
                if(selectedValues.size() > 0) {
                    pyrRequest.setPropertyTypes(selectedValues);
                }
            }
        }

        if(locaityIds.size()>0){
           int localId[]=new int[locaityIds.size()];
           int i=0;
           for(SearchResponseItem search:locaityIds){
               localId[i]=Integer.valueOf(search.entityId);
               i++;
           }
            pyrRequest.setLocalityIds(localId);
        }
        pyrRequest.setApplicationType("MobileAndroidApp");
        mPyrRequest=pyrRequest;
        return true;
    }

    public void setSellerIdToPyrObject(PyrRequest pyrRequest){
        int size=0;
        for (Map.Entry<Long, Boolean> entry : mSellerIdMap.entrySet()) {
            if(entry.getValue()){
                size++;
            }
        }

        Long sellerIds[]=new Long[size];
        int i=0;
        for (Map.Entry<Long, Boolean> entry : mSellerIdMap.entrySet()) {
            if(entry.getValue()){
                sellerIds[i]=entry.getKey();
                i++;
            }
        }
        pyrRequest.setMultipleCompanyIds(sellerIds);
        pyrRequest.setDomainId(1);
        pyrRequest.setSendOtp(true);
    }

    public int getContactAdvisorsCount(){
        int count=0;
        if(mSellerIdMap!=null && mSellerIdMap.size()>0) {
            for (Map.Entry<Long, Boolean> entry : mSellerIdMap.entrySet()) {
                if(entry.getValue()){
                    count++;
                }
            }
        }
        return count;
    }

    public int getNonMakaanAssistCount(){
        boolean assist=false;
        int count=0;
        if(mSellerIdMap!=null && mTopAgentsDatas!=null && mSellerIdMap.size()>0 && mTopAgentsDatas.size()>0){
            for(TopAgent topAgent:mTopAgentsDatas){
                if(mSellerIdMap.containsKey(topAgent.agent.company.id) && mSellerIdMap.get(topAgent.agent.company.id)){
                    if(topAgent.agent.company.assist){
                        count=0;
                        assist=true;
                        break;
                    }
                    else{
                        count++;
                    }
                }
            }
        }
        if(!assist){
            return count;
        }
        else {
            return 0;
        }
    }

    public String getSellerName(){
        String sellerName=null;
        if(mSellerIdMap!=null && mTopAgentsDatas!=null && mSellerIdMap.size()>0 && mTopAgentsDatas.size()>0){
            for(TopAgent topAgent:mTopAgentsDatas){
                if(mSellerIdMap.containsKey(topAgent.agent.company.id) && mSellerIdMap.get(topAgent.agent.company.id)){
                    if(!topAgent.agent.company.assist){
                        sellerName=topAgent.agent.user.fullName;
                        break;
                    }

                }
            }
        }
        return sellerName;
    }

    public String getCityContext(){
        return mCityContext;
    }

    public void setCityId(Integer cityId){
        if(mCityId==null) {
            mCityId = cityId;
        }
    }

    public void prefillLocality(String localityName, long localityId, String cityName,
                                ProjectConfigItem projectConfigItem, boolean isBuySelected){
        if(localityId>0) {
            SearchResponseItem searchResponseItem = new SearchResponseItem();
            searchResponseItem.entityId = String.valueOf(localityId);
            searchResponseItem.entityName = localityName;

            locaityIds.add(searchResponseItem);
        }

        if(projectConfigItem!=null){
            fromProject=true;
            if(projectConfigItem.minPrice!=0) {
                alreadySelectedMinBudget = projectConfigItem.minPrice;
            }
            if(projectConfigItem.maxPrice!=0) {
                alreadySelectedMaxBudget = projectConfigItem.maxPrice;
            }
            pyrFromProjectBuySelected=isBuySelected;
        }

        if(TextUtils.isEmpty(mCityContext)) {
            mCityContext = cityName;
        }
    }



    public void clear(){
        list.clear();
        locaityIds.clear();
        mSellerIdMap.clear();
        mCityId=null;
        mCityContext = "";
        alreadySelectedMinBudget=0;
        alreadySelectedMaxBudget=0;
        fromProject=false;
        pyrFromProjectBuySelected=false;
    }

    public SerpRequest getserpRequestObject(){
        PyrRequest pyrRequest =getPyrRequestObject();
        SerpRequest serpRequest;
        if(BUY_SELECTED) {
            serpRequest = new SerpRequest(SerpRequest.CONTEXT_BUY);
        }
        else {
            serpRequest = new SerpRequest(SerpRequest.CONTEXT_RENT);
        }
        serpRequest.setCityId(pyrRequest.getCityId());

        int localityIds[]=pyrRequest.getLocalityIds();

        for(int i=0;i<locaityIds.size();i++) {
            serpRequest.setLocalityId(localityIds[i]);
        }

        if(pyrRequest.getProjectId()!=null){
            serpRequest.setProjectId(pyrRequest.getProjectId());
        }

        return serpRequest;
    }

    public ArrayList<FilterGroup> getSerpFilterGroups(){
        try {
            PyrRequest pyrRequest=getPyrRequestObject();
            if(pyrRequest.getSalesType().equalsIgnoreCase("buy")) {
                ArrayList<FilterGroup> filterGroups = MasterDataCache.getInstance().getAllBuyFilterGroups();
                populateFilters(getClonedFilterGroups(filterGroups));
                return filterGroups;
            }
            else if(pyrRequest.getSalesType().equalsIgnoreCase("rent")) {
                ArrayList<FilterGroup> filterGroups = MasterDataCache.getInstance().getAllRentFilterGroups();
                populateFilters(getClonedFilterGroups(filterGroups));
                return filterGroups;
            }
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private ArrayList<FilterGroup> getClonedFilterGroups(ArrayList<FilterGroup> filterGroups) throws CloneNotSupportedException {
        ArrayList<FilterGroup> group = new ArrayList<>(filterGroups.size());
        for (FilterGroup filter : filterGroups) {
            group.add(filter.clone());
        }
        return group;
    }

    public void populateFilters(ArrayList<FilterGroup> filterGroups){
        PyrRequest pyrRequest =getPyrRequestObject();
        for(FilterGroup grp : filterGroups) {
            if("i_beds".equalsIgnoreCase(grp.internalName)) {
                if(pyrRequest.getBhk() != null && pyrRequest.getBhk().size() >= 0) {
                    ArrayList<Integer>bhkArr=pyrRequest.getBhk();
                    if(bhkArr!=null && bhkArr.size()>0) {
                        for (TermFilter filter : grp.termFilterValues) {
                            if (bhkArr.contains(4) && filter.displayName.equalsIgnoreCase("3+")) {
                                filter.selected = true;
                                grp.isSelected = true;
                            } else if (bhkArr.contains(Integer.valueOf(filter.displayName))) {
                                filter.selected = true;
                                grp.isSelected = true;
                            }

                        }
                    }
                }
            } else if("i_budget".equalsIgnoreCase(grp.internalName)) {
                for(RangeFilter filter : grp.rangeFilterValues) {
                    filter.selectedMinValue=pyrRequest.getMinBudget();
                    filter.selectedMaxValue=pyrRequest.getMaxBudget();
                    grp.isSelected=true;
                }
            } else if("i_property_type".equalsIgnoreCase(grp.internalName)) {
                ArrayList<String>propertyTypes= pyrRequest.getPropertyTypes();
                if(propertyTypes!=null && propertyTypes.size()>0) {
                    for (TermFilter filter : grp.termFilterValues) {
                        if (filter.value.equalsIgnoreCase("1") && propertyTypes.contains("Apartment")) {
                            filter.selected = true;
                            grp.isSelected = true;
                        } else {
                            for (String type : propertyTypes) {
                                if (type.equalsIgnoreCase(filter.displayName.replace(" ", ""))) {
                                    filter.selected = true;
                                    grp.isSelected = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

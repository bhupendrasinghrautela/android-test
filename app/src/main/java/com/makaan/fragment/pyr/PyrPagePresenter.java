package com.makaan.fragment.pyr;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.makaan.activity.pyr.PropertyTypeFragment;
import com.makaan.activity.pyr.PyrOtpVerification;
import com.makaan.activity.pyr.TopSellersFragment;
import com.makaan.response.pyr.TopAgentsData;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.ui.pyr.FilterableMultichoiceDialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by proptiger on 7/1/16.
 */
public class PyrPagePresenter {
    private static final String STRING_LACS = " Lacs";
    private static final String STRING_CRORE = " Cr";
    private static PyrPagePresenter pyrPagePresenter;
    private PyrReplaceFragment mReplaceFragment;
    private PyrPageFragment mPyrFragment;
    private TopSellersFragment mTopSellersFragment;
    private PropertyTypeFragment mPropertyTypeFragment;
    private PyrOtpVerification mPyrOtpVerification;
    private FilterableMultichoiceDialogFragment mPropertySearchFragment;
    private ArrayList<String> list=new ArrayList<String>();
    private boolean[] mSelectedLocalitiesFlag;
    private ArrayList<SearchResponseItem> locaityIds=new ArrayList<SearchResponseItem>();
    private HashMap<Integer ,Boolean> mSellerIdMap=new HashMap<Integer, Boolean>();
    TopAgentsData[] mTopAgentsDatas;

    public TopAgentsData[] getmTopAgentsDatas() {
        return mTopAgentsDatas;
    }

    public void setmTopAgentsDatas(TopAgentsData[] mTopAgentsDatas) {
        this.mTopAgentsDatas = mTopAgentsDatas;
    }

    public PyrPagePresenter(){

    }


    public static PyrPagePresenter getPyrPagePresenter() {
        if(pyrPagePresenter==null){
            pyrPagePresenter=new PyrPagePresenter();
        }
        return pyrPagePresenter;
    }

    public void setReplaceFragment(PyrReplaceFragment replaceFragment){
        mReplaceFragment=replaceFragment;
    }

    public void showPropertySearchFragment(){
        mSelectedLocalitiesFlag=new boolean[list.size()];
        mPropertySearchFragment=new FilterableMultichoiceDialogFragment();
        mReplaceFragment.showPropertySearchFragment(mPropertySearchFragment);
    }

    public void showTopSellersFragment(){
       // mSelectedLocalitiesFlag=new boolean[list.size()];
        mTopSellersFragment = new TopSellersFragment();
        mReplaceFragment.replaceFragment(mTopSellersFragment, true);
    }

    public void showPropertyTypeFragment(){
        // mSelectedLocalitiesFlag=new boolean[list.size()];
        mPropertyTypeFragment = new PropertyTypeFragment();
        mReplaceFragment.replaceFragment(mPropertyTypeFragment, true);
    }

    public void showPyrOtpFragment(){
        // mSelectedLocalitiesFlag=new boolean[list.size()];
        mPyrOtpVerification = new PyrOtpVerification();
        mReplaceFragment.replaceFragment(mPyrOtpVerification, true);
    }


    public void showPyrMainPageFragment(){
        mPyrFragment=new PyrPageFragment();
        mReplaceFragment.replaceFragment(mPyrFragment, true);
    }

    public void updateSelectedItemsList(String clickedItem, ArrayList<SearchResponseItem> list, SearchResponseItem searchResponseItem){
        boolean flag=true;
        for (int i = 0; i < list.size(); i++) {
            if(clickedItem.equalsIgnoreCase(list.get(i).entityName)){
                flag=false;
                break;
            }
            else {
                flag=true;
            }
        }
        if(flag){
            list.add(searchResponseItem);
        }
    }

    public ArrayList<SearchResponseItem>getSelectedItemList(String clickedItem, ArrayList<SearchResponseItem>list){
        int index=-1;
        for (int i = 0; i < list.size(); i++) {
            if (clickedItem.equalsIgnoreCase(list.get(i).entityName)) {
                //list.remove(i);
                index=i;
                break;
            }
        }
        if(index!=-1){
            list.remove(index);
        }
        return list;
    }

    public void setLocalityIds(SearchResponseItem searchResponseItem, Context context){
      if(locaityIds.size()<6) {

          boolean flag=false;
          for(SearchResponseItem searchResponseItemObj :locaityIds){
            if(searchResponseItemObj.entityId== searchResponseItem.entityId){
              flag=true;
            }
          }

          if(!flag) {
              if (locaityIds.size() >=0) {
                  locaityIds.add(searchResponseItem);
              }
          }
      }
      else if(locaityIds.size()==6){
          Toast.makeText(context, "Only a maximum of 6 listings can be added", Toast.LENGTH_SHORT).show();
      }
    }

    public void removeLocalityId(SearchResponseItem searchResponseItem){
        boolean flag=false;
        for(SearchResponseItem searchResponseItemObj :locaityIds){
            if(searchResponseItemObj.entityId== searchResponseItem.entityName){
                //locaityIds.remove(searchResponseItemObj);
                flag=true;
            }
        }
        if(flag){
            locaityIds.remove(searchResponseItem);
        }

    }

    public ArrayList<SearchResponseItem> getAlreadySelectedProjects(){
        return locaityIds;
    }

    public String getPrice(long price_value_lacs) {
        String priceString = null;

        if (price_value_lacs < 10000000) {
            priceString = String.valueOf(price_value_lacs / 100000).concat(
                    STRING_LACS);
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            double converInCr = (double) price_value_lacs / 10000000;
            df.format(converInCr);
            String price = String.format("%.2f", converInCr);
            priceString = price.concat(STRING_CRORE);
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
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public void setSellerIds(int sellerId, boolean flag){
        Log.e("called ","for seller id "+sellerId+" "+flag);
        mSellerIdMap.put(sellerId,flag);
    }

    public boolean getSellerIdStatus(int sellerId){
               if(mSellerIdMap.get(sellerId)==null){
                   return false;
               }
               else {
                   return mSellerIdMap.get(sellerId);
               }
    }
}

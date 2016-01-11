package com.makaan.fragment.pyr;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.makaan.response.search.Search;
import com.makaan.ui.pyr.FilterableMultichoiceDialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by proptiger on 7/1/16.
 */
public class PyrPagePresenter {
    private static final String STRING_LACS = " Lacs";
    private static final String STRING_CRORE = " Cr";
    private static PyrPagePresenter pyrPagePresenter;
    private PyrReplaceFragment mReplaceFragment;
    private PyrPageFragment mPyrFragment;
    private FilterableMultichoiceDialogFragment mPropertySearchFragment;
    private ArrayList<String> list=new ArrayList<String>();
    private boolean[] mSelectedLocalitiesFlag;
    private ArrayList<Search> locaityIds=new ArrayList<Search>();
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

    public void showPyrMainPageFragment(){
        mPyrFragment=new PyrPageFragment();
        mReplaceFragment.replaceFragment(mPyrFragment, true);
    }

    public void updateSelectedItemsList(String clickedItem, ArrayList<Search> list, Search search){
        boolean flag=true;
        for (int i = 0; i < list.size(); i++) {
            if(clickedItem.equalsIgnoreCase(list.get(i).getLocality())){
                flag=false;
                break;
            }
            else {
                flag=true;
            }
        }
        if(flag){
            list.add(search);
        }
    }

    public ArrayList<Search>getSelectedItemList(String clickedItem, ArrayList<Search>list){
        int index=-1;
        for (int i = 0; i < list.size(); i++) {
            if (clickedItem.equalsIgnoreCase(list.get(i).getLocality())) {
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

    public void setLocalityIds(Search search, Context context){
      if(locaityIds.size()<6) {

          boolean flag=false;
          for(Search searchObj:locaityIds){
            if(searchObj.getLocalityId()==search.getLocalityId()){
              flag=true;
            }
          }

          if(!flag) {
              if (locaityIds.size() >=0) {
                  locaityIds.add(search);
              }
          }
      }
      else if(locaityIds.size()==6){
          Toast.makeText(context, "Only a maximum of 6 listings can be added", Toast.LENGTH_SHORT).show();
      }
    }

    public void removeLocalityId(Search search){
        boolean flag=false;
        for(Search searchObj:locaityIds){
            if(searchObj.getLocalityId()==search.getLocalityId()){
                //locaityIds.remove(searchObj);
                flag=true;
            }
        }
        if(flag){
            locaityIds.remove(search);
        }

    }

    public ArrayList<Search> getAlreadySelectedProjects(){
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
}

package com.makaan.activity.pyr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.fragment.pyr.NoSellersFragment;
import com.makaan.fragment.pyr.TopSellersFragment;
import com.makaan.fragment.userLogin.LoginSocialFragment;
import com.makaan.fragment.userLogin.SignUpFragment;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.ui.pyr.FilterableMultichoiceDialogFragment;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.fragment.pyr.PyrReplaceFragment;
import com.segment.analytics.Properties;

/**
 * Created by proptiger on 6/1/16.
 */
public class PyrPageActivity extends MakaanFragmentActivity implements PyrReplaceFragment{

    public static final String KEY_IS_BUY = "isBuy";
    public static final String KEY_CITY_NAME = "cityName";
    public static final String KEY_CITY_Id = "cityId";
    public static final String KEY_LOCALITY_ID = "localityId";
    public static final String KEY_LOCALITY_NAME = "localityName";
    public static final String BEDROOM_AND_BUDGET="bedroomAndBudget";
    public static final String BUY_SELECTED="buySelected";
    public static final String SOURCE_SCREEN_NAME="sourceScreenName";

    private FragmentTransaction mFragmentTransaction;
    private PyrPagePresenter mPagePresenter;

    @Override
    protected int getContentViewId() {
        return R.layout.pyr_activity_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPagePresenter=PyrPagePresenter.getPyrPagePresenter();
        mPagePresenter.setReplaceFragment(this);

        if(null!=getIntent()) {
            long localityId = getIntent().getLongExtra(KEY_LOCALITY_ID, 0);
            String localityName = getIntent().getStringExtra(KEY_LOCALITY_NAME);
            String cityName = getIntent().getStringExtra(KEY_CITY_NAME);
            String screenName=getIntent().getStringExtra(SOURCE_SCREEN_NAME);
            Long cityId = null;
            ProjectConfigItem projectConfigItem=null;
            boolean isBuySelected = false;
            if(null!=getIntent().getExtras()) {
                cityId = this.getIntent().getExtras().getLong(KEY_CITY_Id);
                projectConfigItem = this.getIntent().getExtras().getParcelable(BEDROOM_AND_BUDGET);
                isBuySelected = this.getIntent().getExtras().getBoolean(BUY_SELECTED);
            }
            mPagePresenter.setCityId(cityId!=null?cityId.intValue():0);
            mPagePresenter.prefillLocality(localityName, localityId, cityName ,
                    projectConfigItem, isBuySelected,screenName);
        }

        mPagePresenter.showPyrMainPageFragment();
        //mPagePresenter.setBuySelected(getIntent().getBooleanExtra(IS_BUY, false));
    }

    @Override
    protected void onDestroy() {
        mPagePresenter.clear();
        super.onDestroy();
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean shouldAddToBackStack) {
        if(fragment.getClass().getName().equalsIgnoreCase(NoSellersFragment.class.getName())) {
            Bundle bundle = new Bundle();
            if (null != getIntent()) {
                String localityName = getIntent().getStringExtra(KEY_LOCALITY_NAME);
                String cityName = getIntent().getStringExtra(KEY_CITY_NAME);
                if(localityName!=null){
                    bundle.putString(KEY_LOCALITY_NAME,localityName);
                }
                if(cityName!=null){
                    bundle.putString(KEY_CITY_NAME, cityName);
                }
            }
            fragment.setArguments(bundle);
        }
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.pyr_fragment_holder, fragment, fragment.getClass().getName());
        if(shouldAddToBackStack) {
            mFragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        mFragmentTransaction.commit();
    }

    @Override
    public void showPropertySearchFragment(FilterableMultichoiceDialogFragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragment.show(fragmentManager, "PropertyList");
    }

    @Override
    public void popFromBackstack(int popCount) {
        if (getSupportFragmentManager().getBackStackEntryCount() >=1 ){
            for(int i=0;i<popCount;i++) {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onBackPressed() {
        TopSellersFragment myFragment = (TopSellersFragment)getSupportFragmentManager().findFragmentByTag(TopSellersFragment.class.getName());

        if(myFragment!=null && myFragment.isVisible()){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerPyr);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.close);
            MakaanEventPayload.endBatch(this, mPagePresenter.getSelectSellersAction(mPagePresenter.getSourceScreenName()));
            myFragment=null;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 1 ){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            super.onBackPressed();
        }
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    @Override
    public String getScreenName() {
        return "Pyr";
    }
}
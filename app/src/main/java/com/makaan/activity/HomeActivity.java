package com.makaan.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;
//import com.makaan.fragment.listing.SearchBarFragment;

public class HomeActivity extends AppCompatActivity {
    boolean isBottom = true;
    private Toolbar mToolbar;

    RelativeLayout rlSearch;
    RadioGroup rgType;
    TextView tvSearch;
    FrameLayout topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        topBar=(FrameLayout) findViewById(R.id.top_bar);
        // new fragment to show search bar top of listing
        //SearchBarFragment searchBarFragment = SearchBarFragment.init(SearchBarFragment.TYPE_SEARCH);
        //initFragment(R.id.top_bar, searchBarFragment, false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tvSearch =(TextView) findViewById(R.id.et_search);
        tvSearch.setFocusable(false);
        rgType=(RadioGroup) findViewById(R.id.rg_property_type);

        rlSearch = (RelativeLayout) findViewById(R.id.rl_footer);
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBottom) {
                    slideToTop();
                    isBottom = false;
                } else {
                    slideToBottom();
                    isBottom = true;
                }


            }
        });

        /*setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        /*final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        */
    }

    public void slideToTop() {
        Animation slide;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -3.2f);

        slide.setDuration(500);
        rlSearch.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                rlSearch.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rlSearch.setLayoutParams(lp);
                mToolbar.setVisibility(View.GONE);
                rgType.setVisibility(View.GONE);
                rlSearch.setVisibility(View.GONE);
                topBar.setVisibility(View.VISIBLE);
                //tvSearch.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //tvSearch.setFocusableInTouchMode(true);

            }
        });
    }

    public void slideToBottom() {
        Animation slide;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 3.2f);

        slide.setDuration(500);
        rlSearch.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                rlSearch.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(25, 25, 0, 74);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rlSearch.setLayoutParams(lp);
                mToolbar.setVisibility(View.VISIBLE);
                rgType.setVisibility(View.VISIBLE);

            }
        });
    }

    private void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if(shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commit();
    }
}
package com.makaan.activity.buyerJourney;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.cookie.CookiePreferences;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.user.UserResponse;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/23/16.
 */
public class BuyerAccountSettingActivity extends MakaanFragmentActivity {
    @Bind(R.id.et_header_name)
    EditText mHeaderNameEditText;
    @Bind(R.id.et_name)
    EditText mNameEditText;
    @Bind(R.id.et_email)
    EditText mEmailEditText;
    @Bind(R.id.et_contact_number)
    EditText mComtactNumberEditText;
    @Bind(R.id.et_current_password)
    EditText mCurrentPasswordEditText;
    @Bind(R.id.et_new_password)
    EditText mNewPasswordEditText;
    @Bind(R.id.et_confirm_password)
    EditText mConfirmPasswordEditText;

    @Bind(R.id.iv_profile_image)
    ImageView mProfileImageView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.ll_change_password)
    LinearLayout mChangePasswordLinearLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_buyer_edit_profile;
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    @Override
    public String getScreenName() {
        return "account_settings";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CookiePreferences.isUserLoggedIn(BuyerAccountSettingActivity.this)) {
            finish();
        }
//        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
//        setContentView(R.layout.fragment_buyer_profile);
        setupAppBar();
        setUserData();
    }



    private void setUserData() {
        if (CookiePreferences.isUserLoggedIn(BuyerAccountSettingActivity.this)) {
            UserResponse.UserData userData = CookiePreferences.getUserInfo(this).getData();
            mHeaderNameEditText.setText(userData.getFirstName());
            mNameEditText.setText(userData.getFirstName());
            if(!TextUtils.isEmpty(userData.getProfileImageUrl())) {
                MakaanNetworkClient.getInstance().getImageLoader().get(userData.profileImageUrl, new ImageLoader.ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                                mProfileImageView.setImageBitmap(imageContainer.getBitmap());
                            }
                        }
                );
            }
            if(!TextUtils.isEmpty(userData.contactNumber)) {
                mComtactNumberEditText.setText(userData.contactNumber);
            }
        }
    }

    private void setupAppBar() {
        setSupportActionBar(mToolbar);

        //set up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.back_black);
        actionBar.setTitle("");

    }

    @OnClick(R.id.iv_edit_header_name)
    void onHeaderNameEditClicked(View view) {
        mHeaderNameEditText.setEnabled(true);
    }

    @OnClick(R.id.iv_edit_name)
    void onNameEditClicked(View view) {
        mNameEditText.setEnabled(true);
    }

    @OnClick(R.id.tv_change_password)
    void onChangePasswordClicked(View view) {
        mChangePasswordLinearLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_save)
    void onSaveClicked(View view) {
        // TODO
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}

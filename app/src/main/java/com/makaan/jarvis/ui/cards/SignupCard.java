package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.CookiePreferences;
import com.makaan.jarvis.message.Message;
import com.makaan.response.login.UserLoginPresenter;
import com.makaan.ui.view.BaseView;

import butterknife.Bind;

/**
 * Created by sunil on 19/01/16.
 */
public class SignupCard extends BaseView<Message> {

    @Bind(R.id.makaan_login)
    View makaanLogin;

    @Bind(R.id.fb_login)
    View facebookLogin;

    @Bind(R.id.gmail_login)
    View gmailLogin;

    private Context mContext;

    public SignupCard(Context context) {
        super(context);
        mContext = context;
    }

    public SignupCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SignupCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void bindView(final Context context, Message item) {
        makaanLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLoginActivity( UserLoginPresenter.LOGIN_MAKAAN);
            }
        });

        facebookLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLoginActivity( UserLoginPresenter.LOGIN_FB);
            }
        });

        gmailLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLoginActivity(UserLoginPresenter.LOGIN_GMAIL);
            }
        });
    }

    private void launchLoginActivity(int loginType){
        if(null!=MasterDataCache.getInstance().getUserData()){
            Toast.makeText(mContext, R.string.user_already_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent loginIntent = new Intent(getContext(), UserLoginActivity.class);
        loginIntent.putExtra(UserLoginPresenter.LOGIN_TYPE, loginType);
        getContext().startActivity(loginIntent);
    }
}

package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.makaan.R;
import com.makaan.activity.userLogin.UserLoginActivity;
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

    public SignupCard(Context context) {
        super(context);
    }

    public SignupCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SignupCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        Intent loginIntent = new Intent(getContext(), UserLoginActivity.class);
        loginIntent.putExtra(UserLoginPresenter.LOGIN_TYPE, loginType);
        getContext().startActivity(loginIntent);
    }
}

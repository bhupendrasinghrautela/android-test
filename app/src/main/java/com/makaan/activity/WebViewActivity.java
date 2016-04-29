package com.makaan.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.makaan.R;
import com.makaan.fragment.WebViewFragment;

import butterknife.Bind;

/**
 * Created by sunil on 29/04/16.
 */
public class WebViewActivity extends MakaanFragmentActivity {

    private static final String SCREEN = WebViewActivity.class.getSimpleName();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_webview_layout;
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    @Override
    public String getScreenName() {
        return SCREEN;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebViewFragment fragment = new WebViewFragment();
        Bundle data = getIntent().getExtras();
        if (data != null) {
            fragment.setArguments(data);
            initFragment(R.id.activity_webview_frame, fragment, false);
        }else{
            Toast.makeText(this,R.string.generic_error, Toast.LENGTH_SHORT).show();
            finish();
        }


    }
}

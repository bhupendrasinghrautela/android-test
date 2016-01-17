package com.makaan.activity;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.ui.MakaanProgressDialog;
import com.makaan.util.AnimationUtils;
import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;

import butterknife.ButterKnife;

/**
 * Created by vaibhav on 23/12/15.
 */
public abstract class MakaanFragmentActivity extends BaseJarvisActivity {


    private MakaanProgressDialog progressDialog;


    protected abstract int getContentViewId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        AppBus.getInstance().register(this);

        if (!AppUtils.haveNetworkConnection(this)) {
            showNoNetworkFound();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationUtils.backActivityAnimation(this);
    }



    private void showLoadingDialog() {
        progressDialog = new MakaanProgressDialog(this);
        if (!isFinishing()) {
            progressDialog.show();
        }
    }

    private void cancelLoadingDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Not able to launch activity and dismiss progressbar", e);
        }
    }

    private void showNoNetworkFound() {
        //TODO: implement
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        AppBus.getInstance().unregister(this);

    }


}

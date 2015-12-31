package com.makaan.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.makaan.activity.MakaanFragmentActivity;

/**
 * Created by vaibhav on 23/12/15.
 */
public class MakaanProgressDialog extends ProgressDialog {

    Context context;

    public MakaanProgressDialog(Context context) {
        super(context);
        this.context = context;
        this.setCancelable(false);
        this.setMessage("Loading ..");
        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        /** dismiss the progress bar and clean up here **/
        this.dismiss();
        ((MakaanFragmentActivity) context).onBackPressed();
    }

}

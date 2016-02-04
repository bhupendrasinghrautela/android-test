package com.makaan.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/**
 * A convenient class for displaying  progress dialog 
 * */
public class CommonProgressDialog {
	private Context mContext;
    private ProgressDialog mProgressDialog;
    
    public void showDialog(Context context, int msgId) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        String msg = context.getString(msgId);
        showDialog(context, msg);
    }

    public void showDialog(Context context, String msg) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        mContext = context;
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener(){

            @Override
            public void onCancel(DialogInterface arg0) {
            	((Activity) mContext).finish();
            }
            
        });
        mProgressDialog.show();
    }

    public void dismissDialog() {
    	if (mContext == null || ((Activity) mContext).isFinishing()) {
            return;
        }
        if (mProgressDialog != null) {
        	try{
        		mProgressDialog.dismiss();
        	}catch(Exception e){}
            mProgressDialog = null;
        }
    }

}
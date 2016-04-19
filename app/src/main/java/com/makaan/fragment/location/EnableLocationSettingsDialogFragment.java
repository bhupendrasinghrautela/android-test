package com.makaan.fragment.location;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.crashlytics.android.Crashlytics;
import com.makaan.R;
/**
 *  Enable location prompt dialog
 * */
public class EnableLocationSettingsDialogFragment extends DialogFragment {
    private AlertDialog.Builder mAlertDialogBuilder;

    private static Context mContext;

    public static EnableLocationSettingsDialogFragment newInstance(Context context) {
        EnableLocationSettingsDialogFragment enable_location_settings_dialog = new EnableLocationSettingsDialogFragment();
        mContext = context;
        return enable_location_settings_dialog;
    }

    public EnableLocationSettingsDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mAlertDialogBuilder = new AlertDialog.Builder(getActivity()).setTitle(R.string.gps_network_not_enabled)
                .setMessage(R.string.gps_network_enable_prompt)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    	
                    	if(getActivity() == null || getActivity().isFinishing()) {
    						return;
    					}
                    	
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        try {
                            if (mContext != null) {
                                mContext.startActivity(myIntent);
                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                    }

                }).setNegativeButton(android.R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    	
                    	if(getActivity() == null || getActivity().isFinishing()) {
    						return;
    					}

                    }

                });

        return mAlertDialogBuilder.create();
    }

}
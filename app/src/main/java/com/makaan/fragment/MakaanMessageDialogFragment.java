package com.makaan.fragment;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;

/**
 * Created by rohitgarg on 3/1/16.
 */
public class MakaanMessageDialogFragment extends MakaanBaseDialogFragment implements View.OnClickListener {
    @Bind(R.id.fragment_dialog_makaan_message_message_text_view)
    TextView mMessageTextView;
    @Bind(R.id.fragment_dialog_makaan_message_positive_button)
    TextView mPositiveButton;
    @Bind(R.id.fragment_dialog_makaan_message_negative_button)
    TextView mNegativeButton;
    private MessageDialogCallbacks mCallbacks;
    private String mMessage;
    private String mPositiveText;
    private String mNegativeText;

    public static void showMessage(FragmentManager manager, String message, String positiveText) {
        showMessage(manager, message, positiveText, null, null);
    }

    public static void showMessage(FragmentManager manager, String message, String positiveText,
                                   MessageDialogCallbacks callbacks) {
        showMessage(manager, message, positiveText, null, callbacks);
    }

    public static void showMessage(FragmentManager manager, String message, String positiveText,
                                   String negativeText, MessageDialogCallbacks callbacks) {
        if(manager == null) {
            return;
        }
        FragmentTransaction ft = manager.beginTransaction();
        MakaanMessageDialogFragment fragment = new MakaanMessageDialogFragment();
        fragment.setData(message, positiveText, negativeText, callbacks);
        fragment.show(ft, "");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_dialog_makaan_message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mMessageTextView.setText(mMessage);

        if (!TextUtils.isEmpty(mPositiveText)) {
            mPositiveButton.setVisibility(View.VISIBLE);
            mPositiveButton.setOnClickListener(this);
            mPositiveButton.setText(mPositiveText);
        } else {
            mPositiveButton.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(mNegativeText)) {
            mNegativeButton.setVisibility(View.VISIBLE);
            mNegativeButton.setOnClickListener(this);
            mNegativeButton.setText(mNegativeText);
        } else {
            mNegativeButton.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        if(dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setData(String message, String positiveText, String negativeText, MessageDialogCallbacks callbacks) {
        mMessage = message;
        mPositiveText = positiveText;
        mNegativeText = negativeText;
        mCallbacks = callbacks;
    }

    @Override
    public void onClick(View v) {
        if (mCallbacks != null) {
            switch (v.getId()) {
                case R.id.fragment_dialog_makaan_message_positive_button:
                    mCallbacks.onPositiveClicked();
                    break;
                case R.id.fragment_dialog_makaan_message_negative_button:
                    mCallbacks.onNegativeClicked();
                    break;
            }
        }
        dismissAllowingStateLoss();
    }

    public interface MessageDialogCallbacks {
        void onPositiveClicked();

        void onNegativeClicked();
    }
}

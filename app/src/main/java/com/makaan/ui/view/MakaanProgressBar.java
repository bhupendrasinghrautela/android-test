package com.makaan.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makaan.R;

/**
 * Created by aishwarya on 07/04/16.
 */
public class MakaanProgressBar extends ProgressBar implements OnClickListener{
    private Context mContext;
    private Boolean mIsLocality = false;
    public MakaanProgressBar(Context context) {
        this(context,null);
    }

    public MakaanProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MakaanProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        final TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.MakaanProgressBar, defStyleAttr, 0);
        try {
            mIsLocality = typedArray.getBoolean(R.styleable.MakaanProgressBar_isLocality, false);
        }
        finally {
            typedArray.recycle();
        }
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final Dialog dialog = new Dialog(mContext);
        if(dialog.getWindow()!=null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setContentView(R.layout.dialog_layout);

        TextView title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        if(mIsLocality) {
            title.setText(mContext.getString(R.string.locality_score));
        }
        else{
            title.setText(mContext.getString(R.string.project_score));
        }
        TextView message = (TextView) dialog.findViewById(R.id.message);
        if(mIsLocality) {
            message.setText(mContext.getString(R.string.locality_score_message));
        }
        else{
            message.setText(mContext.getString(R.string.project_score_message));
        }
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

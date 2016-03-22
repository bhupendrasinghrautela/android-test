package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.pyr.PyrPageActivity;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.message.Message;
import com.makaan.ui.view.BaseView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 19/02/16.
 */
public class PyrPopupCard extends BaseCtaView<ExposeMessage> {

    private Context mContext;

    @Bind(R.id.title)
    TextView mTitle;

    @Bind(R.id.message)
    TextView mMessage;

    @Bind(R.id.btn_yes)
    Button mApplyButton;

    @OnClick(R.id.btn_no)
    public void OnCancelClick(){
        if(null!=mOnCancelClickListener){
            mOnCancelClickListener.onCancelClick();
        }

    }


    public PyrPopupCard(Context context) {
        super(context);
    }

    public PyrPopupCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PyrPopupCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(final Context context, ExposeMessage item) {
        if(item.city != null) {
            mTitle.setText("interested in " + item.city.toLowerCase());
        }

        mApplyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pyrIntent = new Intent(context, PyrPageActivity.class);
                pyrIntent.putExtra(PyrPageActivity.SOURCE_SCREEN_NAME, "Jarvis");
                context.startActivity(pyrIntent);

                if(null!=mOnApplyClickListener){
                    mOnApplyClickListener.onApplyClick();
                }
            }
        });
    }


}

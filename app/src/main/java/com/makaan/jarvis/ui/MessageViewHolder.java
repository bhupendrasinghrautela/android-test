package com.makaan.jarvis.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.TextView;

import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.ui.cards.InTextCard;
import com.makaan.jarvis.ui.cards.OutTextCard;
import com.makaan.ui.view.BaseView;
import com.makaan.util.DateUtil;

/**
 * Created by sunil on 14/01/16.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder implements HolderBinder<Message>{
    BaseView view;
    TextView timestampText;

    public MessageViewHolder(BaseView itemView) {
        super(itemView);
        view = itemView;
    }

    @Override
    public void bind(Context context, Message message) {
        view.bindView(context, message);

        if(message.timestamp>0) {
            addTimeStamp(context, message.timestamp);
        }

    }

    private void addTimeStamp(Context context, long timeStamp){

        if(null==timestampText) {
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

            lparams.setMargins(16, 16, 16, 22);


            timestampText = new TextView(context);
            if (view instanceof OutTextCard) {
                timestampText.setGravity(Gravity.RIGHT);
            } else {
                timestampText.setGravity(Gravity.LEFT);
            }

            timestampText.setTextSize(12);
            timestampText.setTypeface(null, Typeface.ITALIC);

            timestampText.setTextColor(Color.LTGRAY);

            timestampText.setLayoutParams(lparams);


            view.addView(timestampText);
        }

        timestampText.setText(DateUtil.getDateTime(timeStamp));

    }
}

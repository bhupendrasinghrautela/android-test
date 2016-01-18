package com.makaan.jarvis.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.makaan.jarvis.message.Message;
import com.makaan.ui.view.BaseView;

/**
 * Created by sunil on 14/01/16.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder implements HolderBinder<Message>{
    BaseView view;
    public MessageViewHolder(BaseView itemView) {
        super(itemView);
        view = itemView;
    }

    @Override
    public void bind(Context context, Message message) {
        view.bindView(context, message);
    }
}

package com.makaan.jarvis.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.ui.cards.ChatCardFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 14/01/16.
 */
public class ConversationAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private Context mContext;

    List<Message> messages = new ArrayList<Message>();

    public ConversationAdapter(Context context){
        mContext = context;
    }

    /**
     * This will add the message to the conversation
     * @param message {@link Message}
     * */
    public void addMessage(Message message){
        messages.add(message);
        notifyItemChanged(messages.size());
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(ChatCardFactory.createCard(mContext, parent, viewType));
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.bind(mContext, messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).messageType.value;
    }
}

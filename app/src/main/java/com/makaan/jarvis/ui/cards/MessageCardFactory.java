package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.jarvis.message.MessageType;
import com.makaan.ui.view.BaseView;

/**
 * Created by sunil on 14/01/16.
 */
public class MessageCardFactory {
    public static BaseView createCard(Context context, ViewGroup parent, int messageType){

        if(messageType == MessageType.outText.value){
            return (OutTextCard) LayoutInflater.from(context)
                    .inflate(R.layout.text_message_right, parent, false);

        } else if(messageType == MessageType.inText.value){
            return (InTextCard) LayoutInflater.from(context)
                    .inflate(R.layout.text_message_left, parent, false);

        } else if(messageType == MessageType.sellerOverView.value){
            return (SellerOverviewCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_seller, parent, false);

        }else if(messageType == MessageType.serpFilter.value){
            return (SerpFilterCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_serp_filter, parent, false);

        }else if(messageType == MessageType.signUp.value){
            return (SignupCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_signup, parent, false);
        }else{
            return null;
        }
    }
}

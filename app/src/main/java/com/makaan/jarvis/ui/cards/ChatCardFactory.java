package com.makaan.jarvis.ui.cards;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.makaan.R;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.jarvis.message.MessageType;
import com.makaan.ui.view.BaseView;

/**
 * Created by sunil on 14/01/16.
 */
public class ChatCardFactory {
    public static BaseView createCard(Context context, ViewGroup parent, int messageType){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        int margin=(width/10)*2;
        int defaultTopBottomMargin= JarvisConstants.CHAT_CARD_DEFAULT_TOP_BOTTOM_MARGIN;
        int defaultLeftMargin=JarvisConstants.CHAT_CARD_DEFAULT_LEFT_MARGIN;

        if(messageType == MessageType.outText.value){
            param.leftMargin=margin;
            param.rightMargin=defaultLeftMargin;
            OutTextCard outTextCard=(OutTextCard) LayoutInflater.from(context)
                    .inflate(R.layout.text_message_right, parent, false);
            outTextCard.setLayoutParams(param);
            return outTextCard;

        } else if(messageType == MessageType.inText.value){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            InTextCard inTextCard=(InTextCard) LayoutInflater.from(context)
                    .inflate(R.layout.text_message_left, parent, false);
            inTextCard.setLayoutParams(param);
            return inTextCard;

        } else if(messageType == MessageType.sellerOverView.value){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            param.topMargin=defaultTopBottomMargin;
            param.bottomMargin=defaultTopBottomMargin;
            SellerOverviewCard sellerOverviewCard=(SellerOverviewCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_seller, parent, false);
            sellerOverviewCard.setLayoutParams(param);
            return sellerOverviewCard;

        }else if(messageType == MessageType.askReq.value){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            param.topMargin=defaultTopBottomMargin;
            param.bottomMargin=defaultTopBottomMargin;
            AskReqCard askReqCard =(AskReqCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_requirement_filter, parent, false);
            askReqCard.setLayoutParams(param);
            return askReqCard;

        }else if(messageType == MessageType.signUp.value){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            param.topMargin=defaultTopBottomMargin;
            param.bottomMargin=defaultTopBottomMargin;
            SignupCard signupCard=(SignupCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_signup, parent, false);
            signupCard.setLayoutParams(param);
            return signupCard;

        }else if(messageType == MessageType.propertyOverview.value){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            param.topMargin=defaultTopBottomMargin;
            param.bottomMargin=defaultTopBottomMargin;
            PropertyCard propertyCard=(PropertyCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_property, parent, false);
            propertyCard.setLayoutParams(param);
            return  propertyCard;

        }else if(messageType == MessageType.localityOverview.value ||
                messageType == MessageType.localityBuy.value ||
                messageType == MessageType.localityRent.value){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            param.topMargin=defaultTopBottomMargin;
            param.bottomMargin=defaultTopBottomMargin;
            LocalityCard localityCard=(LocalityCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_locality, parent, false);
            localityCard.setLayoutParams(param);
            return localityCard;

        }else if(messageType == MessageType.plainLink.value){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            PlainLinkCard plainLinkCard=(PlainLinkCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_plainlink, parent, false);
            plainLinkCard.setLayoutParams(param);
            return plainLinkCard;

        }else if(messageType == MessageType.sellerSerp.value ||
                messageType == MessageType.sellerSerpMap.value){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            param.topMargin=defaultTopBottomMargin;
            param.bottomMargin=defaultTopBottomMargin;
            SellerOverviewCard sellerOverviewCard=(SellerOverviewCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_seller, parent, false);
            sellerOverviewCard.setLayoutParams(param);
            return sellerOverviewCard;

        }else if(messageType == MessageType.agentRating.value ){
            param.rightMargin=margin;
            param.leftMargin=defaultLeftMargin;
            param.topMargin=defaultTopBottomMargin;
            param.bottomMargin=defaultTopBottomMargin;
            AgentRatingCard agentRatingCard=(AgentRatingCard) LayoutInflater.from(context)
                    .inflate(R.layout.card_agent, parent, false);
            agentRatingCard.setLayoutParams(param);
            return agentRatingCard;

        }else{
            return null;
        }
    }
}

package com.makaan.jarvis.ui.cards;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.makaan.R;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.jarvis.message.CtaType;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.message.MessageType;
import com.makaan.ui.view.BaseView;

/**
 * Created by sunil on 07/01/16.
 */
public class CtaCardFactory {

    public static BaseCtaView createCard(Context context, ExposeMessage message){

        if(CtaType.serpScroll == message.properties.ctaType){
            SerpFilterCard serpFilterCard =
                    (SerpFilterCard) LayoutInflater.from(context).inflate(R.layout.card_serp_filter, null);
            serpFilterCard.bindView(context, message);
            return serpFilterCard;

        } else if (CtaType.enquiryDropped == message.properties.ctaType) {
            MultiPropertyCard multiPropertyCard =
                    (MultiPropertyCard) LayoutInflater.from(context).inflate(
                            R.layout.card_multi_property, null);
            multiPropertyCard.bindView(context, message);
            return multiPropertyCard;

        }else{
            return null;
        }
    }
}

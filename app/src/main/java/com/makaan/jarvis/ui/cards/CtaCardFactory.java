package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.view.LayoutInflater;

import com.makaan.R;
import com.makaan.jarvis.message.CtaType;
import com.makaan.jarvis.message.ExposeMessage;

/**
 * Created by sunil on 07/01/16.
 */
public class CtaCardFactory {

    public static BaseCtaView createCard(Context context, ExposeMessage message){

        if(CtaType.serpScroll == message.properties.ctaType){
            SerpFilterCard serpFilterCard =
                    (SerpFilterCard) LayoutInflater.from(context).inflate(
                            R.layout.jarvis_card_serp_filter, null);
            serpFilterCard.bindView(context, message);
            return serpFilterCard;

        } else if (CtaType.enquiryDropped == message.properties.ctaType) {
            MultiPropertyCard multiPropertyCard =
                    (MultiPropertyCard) LayoutInflater.from(context).inflate(
                            R.layout.jarvis_card_multi_property, null);
            multiPropertyCard.bindView(context, message);
            return multiPropertyCard;

        }else if (CtaType.childSerp == message.properties.ctaType) {
            PyrPopupCard pyrPopupCard =
                    (PyrPopupCard) LayoutInflater.from(context).inflate(
                            R.layout.jarvis_card_pyr, null);
            pyrPopupCard.bindView(context, message);
            return pyrPopupCard;

        }else if (CtaType.contentPyr == message.properties.ctaType) {
            if(message.properties.content==null) {
                PyrPopupCard pyrPopupCard =
                        (PyrPopupCard) LayoutInflater.from(context).inflate(
                                R.layout.jarvis_card_pyr, null);
                pyrPopupCard.bindView(context, message);
                return pyrPopupCard;

            }else{
                RichContentCard richContentCard =
                        (RichContentCard) LayoutInflater.from(context).inflate(
                                R.layout.jarvis_card_content_cancel, null);
                richContentCard.bindView(context, message);
                return richContentCard;

            }

        }else if (CtaType.pageVisits == message.properties.ctaType) {
            SimpleCtaCard simpleCtaCard =
                    (SimpleCtaCard) LayoutInflater.from(context).inflate(
                            R.layout.jarvis_card_cta, null);
            simpleCtaCard.bindView(context, message);
            return simpleCtaCard;

        }else{
            return null;
        }
    }
}

package com.makaan.activity.walkthrough;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.makaan.R;

/**
 * Created by aishwarya on 31/03/16.
 */
public class WalkThroughTransformer implements PageTransformer {
    Float villaXEnd,villaX;
    @Override
    public void transformPage(View view, float position) {
        if(view.getTag() != null) {
            int i = Integer.parseInt(String.valueOf(view.getTag()));
            if (position >= -1.1F && position < 1.1F) {
                if (i == 0) {
                    animateOnFirstScreen(position,view);
                }
                else if(i==1){
                    animateOnSecondScreen(position,view);
                }
                else if(i==2){
                    animateOnThirdScreen(position,view);
                }
                else{
                    animateOnFourthScreen(position,view);
                }
            }
        }
    }

    private void animateOnFirstScreen(float position, View view) {
        view.setTranslationX(-view.getWidth()*position);
        view.setAlpha(1+1.5f*position);
        view.setScaleX(1+position);
        view.setScaleY(1+position);
        view.setTranslationY(-view.getHeight()/2*position);
    }

    private void animateOnSecondScreen(float position, View view) {
        view.setTranslationX(-view.getWidth()*position);
        ImageView hiMBadge = (ImageView) view.findViewById(R.id.hi_m_badge);
        ImageView mBadge = (ImageView) view.findViewById(R.id.m_badge);
        ImageView handImage = (ImageView) view.findViewById(R.id.hand_bottom);
        ImageView face = (ImageView) view.findViewById(R.id.face);
        ImageView yellowText = (ImageView) view.findViewById(R.id.yellow_text);
        ImageView greenText = (ImageView) view.findViewById(R.id.green_text);
        ImageView butterfly = (ImageView) view.findViewById(R.id.butterfly);
        hiMBadge.setVisibility(View.VISIBLE);
        if(position>0) {
            hiMBadge.setAlpha(1 - 1.2f*position);
            mBadge.setVisibility(View.INVISIBLE);
            mBadge.setAlpha(1 - 1.2f*position);
            handImage.setAlpha(1 - position);
            face.setAlpha(1 - position);
            yellowText.setAlpha(1 - 2 * position);
            butterfly.setAlpha(1 - position);
            greenText.setAlpha(1 - 2 * position);
            yellowText.setScaleX(1 - 1.5f * position);
            yellowText.setScaleY(1 - 1.5f * position);
            greenText.setScaleY(1 - 1.5f * position);
            greenText.setScaleX(1 - 1.5f * position);
            butterfly.setScaleY(1 - 1.5f * position);
            butterfly.setScaleX(1 - 1.5f * position);
            yellowText.setTranslationX(-view.getWidth() / 4 * position);
            greenText.setTranslationX(-view.getWidth() / 4 * position);
            butterfly.setTranslationX(-view.getWidth() / 8 * position);
            handImage.setTranslationY(100 * position);
            face.setTranslationY(100 * position);
            butterfly.setTranslationY(300 * position);
            hiMBadge.setTranslationY((view.getHeight() - (1.5f * handImage.getHeight())) * position);
            mBadge.setTranslationY((view.getHeight() - (1.5f * handImage.getHeight())) * position);
            hiMBadge.setTranslationX(view.getWidth() / 4 * position);
            mBadge.setTranslationX(view.getWidth() / 4 * position);
            hiMBadge.setScaleX(1 - position);
            mBadge.setScaleX(1 - position);
            hiMBadge.setScaleY(1 - position);
            mBadge.setScaleY(1 - position);
        }
        else if(position>=-1.05f){
            mBadge.setVisibility(View.VISIBLE);
            mBadge.setAlpha(1f);
            face.setAlpha(1f);
            butterfly.setAlpha(1f);
            yellowText.setTranslationX(-view.getWidth() / 4 * position);
            yellowText.setAlpha(1+position);
            greenText.setAlpha(1+position);
            handImage.setAlpha(1+position);
            hiMBadge.setAlpha(1+position);
            greenText.setTranslationY(view.getHeight()/4*position);
            hiMBadge.setTranslationY(400 * position);
            mBadge.setTranslationY(400 * position);
            hiMBadge.setTranslationX(100 * position);
            mBadge.setTranslationX(100 * position);
            hiMBadge.setScaleX(1+0.5f*position);
            mBadge.setScaleX(1+0.5f*position);
            face.setTranslationX(-350*position);
            butterfly.setTranslationX(350*position);
            face.setTranslationY(50*position);
            face.setScaleX(1-1.4f*position);
            face.setScaleY(1-1.4f*position);
            butterfly.setTranslationY(-50*position);
            handImage.setTranslationY(-100 * position);
            hiMBadge.setScaleY(1+0.5f*position);
            mBadge.setScaleY(1+0.5f*position);
            if(hiMBadge.getY()<-80) {
                hiMBadge.setY(-80);
                mBadge.setY(-80);
            }
            if(villaXEnd!=null &&face.getX()>villaXEnd){
                face.setX(villaXEnd);
            }
            if(villaX!=null && butterfly.getX()<villaX&& villaX<(view.getWidth()/2)){
                butterfly.setX(villaX);
            }
        }
        else{
            mBadge.setAlpha(0f);
            face.setAlpha(0f);
            butterfly.setAlpha(0f);
            hiMBadge.setAlpha(0f);
            mBadge.setAlpha(0f);
            butterfly.setAlpha(0f);
            handImage.setAlpha(0f);
            yellowText.setAlpha(0f);
            greenText.setAlpha(0f);
            if(hiMBadge.getY()<-80) {
                hiMBadge.setY(-80);
                mBadge.setY(-80);
            }
            if(villaXEnd!=null &&face.getX()>villaXEnd){
                face.setX(villaXEnd);
            }

            if(villaX!=null && butterfly.getX()<villaX && villaX<(view.getWidth()/2)){
                butterfly.setX(villaX);
            }
        }
    }

    private void animateOnThirdScreen(float position, View view) {
        view.setTranslationX(-view.getWidth()*position);
        view.setAlpha(1-position);
        ImageView villa = (ImageView) view.findViewById(R.id.villa);
        ImageView hi = (ImageView) view.findViewById(R.id.hi);
        ImageView help = (ImageView) view.findViewById(R.id.help);
        FrameLayout home = (FrameLayout) view.findViewById(R.id.house);
        ImageView homeFrame  = (ImageView) view.findViewById(R.id.house_frame);
        ImageView bottomLine  = (ImageView) view.findViewById(R.id.bottom_line);
        ImageView mPlus = (ImageView) view.findViewById(R.id.m_plus);
        if(position<0.1) {
            villaXEnd = villa.getX() + villa.getWidth();
            villaX = villa.getX();
        }
        if(position>0){
            bottomLine.setVisibility(View.GONE);
            villa.setScaleX(1-position);
            villa.setScaleY(1-position);
            villa.setTranslationX(500*position);
            villa.setTranslationY(300*position);
            hi.setScaleX(1-position);
            hi.setScaleY(1-position);
            hi.setTranslationX(-100*position);
            help.setScaleX(1-position);
            help.setScaleY(1-position);
            help.setTranslationX(-300*position);
            home.setScaleX(1-position);
            home.setScaleY(1-position);
            home.setTranslationX(-500*position);
            mPlus.setTranslationX(500*position);
        }
        else{
            if(position<-0.2) {
                bottomLine.setVisibility(View.VISIBLE);
                bottomLine.setScaleX(-position);
                bottomLine.setScaleY(-position);
            }
            else{
                bottomLine.setVisibility(View.GONE);
            }
            bottomLine.setTranslationY(58*position);
            villa.setAlpha(1+3*position);
            homeFrame.setAlpha(1+3*position);
            hi.setAlpha(1+3*position);
            help.setAlpha(1+3*position);
            mPlus.setAlpha(1+3*position);
            home.setTranslationY(100*position);
            home.setScaleX(1-0.3f*position);
            home.setScaleY(1-0.3f*position);
        }
        if(bottomLine.getY()<(home.getY()+home.getHeight())){
            bottomLine.setY(home.getY());
        }
    }

    private void animateOnFourthScreen(float position, View view) {
        view.setTranslationX(-view.getWidth()*position);
        view.setAlpha(1-position);
        ImageView family = (ImageView) view.findViewById(R.id.family);
        ImageView smallCloud = (ImageView) view.findViewById(R.id.small_cloud);
        ImageView bigCloud = (ImageView) view.findViewById(R.id.big_cloud);
        ImageView withYou = (ImageView) view.findViewById(R.id.with_you);
        ImageView throughout = (ImageView) view.findViewById(R.id.throughout);
        ImageView path = (ImageView) view.findViewById(R.id.path);
        if(position>0){
            family.setTranslationX(500*position);
            path.setTranslationX(200*position);
            smallCloud.setTranslationX(-100*position);
            bigCloud.setTranslationX(100*position);
            withYou.setScaleX(1-position);
            withYou.setScaleY(1-position);
            throughout.setScaleY(1-position);
            throughout.setScaleX(1-position);
        }
    }
}
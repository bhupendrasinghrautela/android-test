package com.makaan.ui.anim;

import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.view.View;

/**
 * Created by aishwarya on 14/01/16.
 */
public class StackLikePagerTransform implements PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

/*        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else */
        if(position <-1 && position>-2){
            view.setRotation(-90);
            view.setAlpha(0);
        }
        else if(position<=-2){
            view.setRotation(0);
            view.setAlpha(1);
        }
        else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(0.99f);
            view.setScaleY(1);
            view.setRotation(90 * (position));

        } else if (position < 1) { // (0,1]
            // Fade the page out.
            view.setAlpha(1);
            view.setRotation(0);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

            view.setScaleX(0.99f);
            view.setScaleY(1);

        } else if (position==1) {
            view.setAlpha(1);
            view.setRotation(0);
//      view.setPadding(0,15,0,0);
        }else if(position>1){
            view.setAlpha(0);
            view.setRotation(0);
        }
/*        else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }*/
        Log.d("alpha",position+"  ");
    }
}

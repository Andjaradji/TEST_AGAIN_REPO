package com.vexanium.vexgift.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.vexanium.vexgift.R;

/**
 * Created by Amang on 17/07/2018.
 */


/*To add fading animation when changing page inside viewpager*/
public class FadePageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setTranslationX(page.getWidth()*-position);

        if(position <= -1 || position >= 1){
            page.setAlpha(0);
        }else if(position == 0){
            page.setAlpha(1);
        }else{
            page.setAlpha(1 - Math.abs(position));
        }

    }
}

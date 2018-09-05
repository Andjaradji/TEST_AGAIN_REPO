package com.vexanium.vexgift.widget.guideview.handguide;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.util.MeasureUtil;

/**
 * Created by mac on 5/31/17.
 */

public class HandGuide {
    public int mGravity;
    public View target;
    public Animation mEnterAnimation;
    public int mWidth;
    public int marginLeft;
    public int marginRight;
    public int marginTop;
    public int marginBottom;
    public View view;
    private Activity activity;
    LayoutInflater layoutInflater;

    public HandGuide target(View target){
        this.target = target;
        return this;
    }

    public HandGuide(Activity activity){
        mEnterAnimation = new AlphaAnimation(0.3f, 1f);
        mEnterAnimation.setDuration(500);
        mEnterAnimation.setRepeatCount(10);
        mEnterAnimation.setRepeatMode(Animation.REVERSE);

        mWidth = -1;
        this.activity = activity;

        layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.item_hand_guide_tooltip, null);
    }

    public HandGuide setMargin(int marginLeft, int marginTop, int marginRight, int marginBottom){

        this.marginBottom = MeasureUtil.dip2px(activity,  marginBottom);
        this.marginLeft = MeasureUtil.dip2px(activity,  marginLeft);
        this.marginRight = MeasureUtil.dip2px(activity,  marginRight);
        this.marginTop = MeasureUtil.dip2px(activity,  marginTop);
        return this;
    }

    public HandGuide setGravity(int gravity) {
        mGravity = gravity;
        return this;
    }
}

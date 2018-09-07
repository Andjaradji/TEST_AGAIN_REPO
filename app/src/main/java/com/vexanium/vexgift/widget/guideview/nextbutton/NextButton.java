package com.vexanium.vexgift.widget.guideview.nextbutton;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.vexanium.vexgift.R;


/**
 * Created by mac on 5/31/17.
 */

public class NextButton {
    public int mGravity;
    public Animation mEnterAnimation;
    public int mWidth;
    public View view;
    public TextView txNext;
    public ImageView imNext;
    LayoutInflater layoutInflater;


    public NextButton(Activity activity) {
        mEnterAnimation = new AlphaAnimation(0.3f, 1f);
        mEnterAnimation.setDuration(500);
        mEnterAnimation.setRepeatCount(2);
        mEnterAnimation.setRepeatMode(Animation.REVERSE);

        mWidth = -1;

        layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.item_next_button_view, null);
        txNext = (TextView) view.findViewById(R.id.txt_next);
        imNext = (ImageView) view.findViewById(R.id.im_next);
    }

    public int getWidth() {
        return mWidth;
    }

    public NextButton setGravity(int gravity) {
        mGravity = gravity;
        return this;
    }
}

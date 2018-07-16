package com.vexanium.vexgift.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Created by mac on 11/23/17.
 */

public class AnimUtil {

    public static void animateButtonRelease(final View container) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(container, "scaleX", 1.0f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(container, "scaleY", 1.0f);
        scaleUpX.setDuration(300);
        scaleUpY.setDuration(300);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleUpX).with(scaleUpY);
        scaleUp.setInterpolator(new OvershootInterpolator());

        scaleUpX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                View p = (View) container.getParent();
                p.invalidate();
            }
        });

        scaleUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scaleUp.start();
    }

    public static void animateButtonPress(final View container) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(container, "scaleX", 0.8f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(container, "scaleY", 0.8f);
        scaleDownX.setDuration(200);
        scaleDownY.setDuration(200);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                View p = (View) container.getParent();
                p.invalidate();
            }
        });

        scaleDown.start();
    }

    public static void animateButtonPress(final View container, float scaleDownValue) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(container, "scaleX", scaleDownValue);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(container, "scaleY", scaleDownValue);
        scaleDownX.setDuration(200);
        scaleDownY.setDuration(200);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                View p = (View) container.getParent();
                p.invalidate();
            }
        });

        scaleDown.start();
    }
}

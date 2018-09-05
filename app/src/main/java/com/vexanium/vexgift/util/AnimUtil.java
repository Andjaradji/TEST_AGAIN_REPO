package com.vexanium.vexgift.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by mac on 11/23/17.
 */

public class AnimUtil {
    public static final int DURATION = 300;

    public static boolean isGone = false;

    static Animation.AnimationListener mAnimListener;

    public static void stopAnimOnAllViews(View... views) {
        for (View view : views) {
            if (view != null) view.clearAnimation();
        }
    }

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

    public static void transTopIn(View view, boolean fade, long duration) {
        view.setVisibility(View.VISIBLE);
        translate(view, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, duration, fade);
    }

    public static void transTopIn(View view, boolean fade) {
        view.setVisibility(View.VISIBLE);
        translate(view, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, DURATION, fade);
    }

    public static Animation getFadeIn(View view, int duration) {
        return AnimUtil.getFadeAnim(view, 0.0f, 1.0f, duration, null);
    }

    public static Animation getFadeOut(View view, int duration ) {
        return AnimUtil.getFadeAnim(view, 1.0f, 0.0f, duration, null);
    }

    public static void transBottomIn(View view, boolean fade) {
        view.setVisibility(View.VISIBLE);
        translate(view, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 200, fade);
    }

    public static Animation getFadeAnim(View view, float fromAlpha, float toAlpha, long duration, Animation.AnimationListener listener) {
        final Animation anim = new AlphaAnimation(fromAlpha, toAlpha);
        anim.setInterpolator(AnimationUtils.loadInterpolator(view.getContext(),
                android.R.anim.decelerate_interpolator));
        anim.setDuration(duration);


        if (listener != null)
            anim.setAnimationListener(listener);
        return anim;
    }

    public static void translate(View view, float fromX, float toX, float fromY, float toY,
                                 float fromAlpha, float toAlpha, long duration, boolean fade) {
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(AnimationUtils.loadInterpolator(view.getContext(),
                android.R.anim.decelerate_interpolator));

        Animation anim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fromX,
                Animation.RELATIVE_TO_SELF, toX,
                Animation.RELATIVE_TO_SELF, fromY,
                Animation.RELATIVE_TO_SELF, toY);
        anim.setDuration(duration);

        final View mV = view;
        anim.setAnimationListener(mAnimListener);

        animSet.addAnimation(anim);

        if (fade) {
            anim = new AlphaAnimation(fromAlpha, toAlpha);
            anim.setDuration(duration);
            animSet.addAnimation(anim);
        }

        view.startAnimation(animSet);

        final View myView = view;
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {

//				if (isGone) {
//					myView.setVisibility(View.GONE);
//					isGone = false;
//				} else {
//					myView.setVisibility(View.VISIBLE);
//				}

            }
        });
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

    public static void fadeIn(View view) {
        view.setVisibility(View.VISIBLE);
        AnimUtil.fade(view, 0.0f, 1.0f, DURATION, null);
    }

    public static void fadeIn(View view, int duration) {
        if (view.getVisibility() == View.GONE)
            view.setVisibility(View.VISIBLE);
        AnimUtil.fade(view, 0.0f, 1.0f, duration, null);
    }

    public static void fade(View view, float fromAlpha, float toAlpha, long duration, Animation.AnimationListener listener) {
        final Animation anim = new AlphaAnimation(fromAlpha, toAlpha);
        anim.setInterpolator(AnimationUtils.loadInterpolator(view.getContext(),
                android.R.anim.decelerate_interpolator));
        anim.setDuration(duration);

        view.startAnimation(anim);

        if (listener != null)
            anim.setAnimationListener(listener);
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

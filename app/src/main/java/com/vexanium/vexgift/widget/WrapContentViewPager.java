package com.vexanium.vexgift.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * Created by mac on 11/29/17.
 */

public class WrapContentViewPager extends ViewPager implements Animation.AnimationListener {

    private boolean enabled;
    private View mCurrentView;
    private PagerAnimation mAnimation = new PagerAnimation();
    private boolean mAnimStarted = false;
    private long mAnimDuration = 100;
    private int widthMeasuredSpec;

    public WrapContentViewPager(Context context) {
        super(context);
        init();
    }

    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mAnimation.setAnimationListener(this);
    }


    /**
     * Allows to redraw the view size to wrap the content of the bigger child.
     *
     * @param widthMeasureSpec  with measured
     * @param heightMeasureSpec height measured
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthMeasuredSpec = widthMeasureSpec;
        int newHeight = 0;

        if (!mAnimStarted && mCurrentView != null) {
            int height;
            mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height = mCurrentView.getMeasuredHeight();

            if (height < getMinimumHeight()) {
                height = getMinimumHeight();
            }

            // make sure that we have an height (not sure if this is necessary because it seems that onPageScrolled is called right after
            int position = getCurrentItem();
            View child = getViewAtPosition(position);
            if (child != null) {
                height = measureViewHeight(child);
            }

            int totalHeight = height + getPaddingBottom() + getPaddingTop();
            newHeight = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY);
            if (getLayoutParams().height != 0 && heightMeasureSpec != newHeight) {
                mAnimation.setDimensions(newHeight, getLayoutParams().height);
                mAnimation.setDuration(mAnimDuration);
                startAnimation(mAnimation);
                mAnimStarted = true;
            } else {
                heightMeasureSpec = newHeight;
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    protected View getViewAtPosition(int position) {
        if (getAdapter() != null) {
            Object objectAtPosition = ((WrapFragmentStatePagerAdapter) getAdapter()).getItem(position);
            if (objectAtPosition != null) {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child != null && getAdapter().isViewFromObject(child, objectAtPosition)) {
                        return child;
                    }
                }
            }
        }
        return null;
    }

    private int measureViewHeight(View view) {
        view.measure(getChildMeasureSpec(widthMeasuredSpec, getPaddingLeft() + getPaddingRight(), view.getLayoutParams().width), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }


    public void onPageChanged(View currentView) {
        mCurrentView = currentView;
        requestLayout();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    /* even user will touch viewPager scope then this method will invoke and it will return false value ...
    *  so using this method we can avoid touch event of ViewPager.
    */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Custom animation to animate the change of height in the {@link WrapContentViewPager}.
     */
    private class PagerAnimation extends Animation {
        private int targetHeight;
        private int currentHeight;
        private int heightChange;

        /**
         * Set the dimensions for the animation.
         *
         * @param targetHeight  View's target height
         * @param currentHeight View's current height
         */
        void setDimensions(int targetHeight, int currentHeight) {
            this.targetHeight = targetHeight;
            this.currentHeight = currentHeight;
            this.heightChange = targetHeight - currentHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime >= 1) {
                getLayoutParams().height = targetHeight;
            } else {
                int stepHeight = (int) (heightChange * interpolatedTime);
                getLayoutParams().height = currentHeight + stepHeight;
            }
            requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    /**
     * Sets the duration of the animation.
     *
     * @param duration Duration in ms
     */
    public void setAnimationDuration(long duration) {
        mAnimDuration = duration;
    }

    /**
     * Sets the interpolator used by the animation.
     *
     * @param interpolator {@link Interpolator}
     */
    public void setAnimationInterpolator(Interpolator interpolator) {
        mAnimation.setInterpolator(interpolator);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mAnimStarted = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimStarted = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}


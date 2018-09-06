package com.vexanium.vexgift.widget.guideview;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

/**
 * Created by mac on 5/30/17.
 */

public class Overlay {
    public final static int NOT_SET = -1;
    public int mBackgroundColor;
    public boolean mDisableClick;
    public boolean mDisableClickThroughHole;
    public HoleStyle mHoleStyle;
    public Animation mEnterAnimation, mExitAnimation;
    public int mHoleOffsetLeft = 0;
    public int mHoleOffsetTop = 0;
    public View.OnClickListener mOnClickListener;
    public int mHoleRadius = NOT_SET;
    public int mPaddingDp = 10;
    public int mRoundedCornerRadiusDp = 0;


    public Overlay() {
        this(true, Color.parseColor("#55000000"), HoleStyle.CIRCLE);
    }

    public Overlay(boolean disableClick, int backgroundColor, HoleStyle holeStyle) {
        mDisableClick = disableClick;
        mBackgroundColor = backgroundColor;
        mHoleStyle = holeStyle;
    }

    /**
     * Set background color
     *
     * @param backgroundColor
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        return this;
    }

    /**
     * Set to true if you want to block all user input to pass through this overlay, set to false if you want to allow user input under the overlay
     *
     * @param yesNo
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay disableClick(boolean yesNo) {
        mDisableClick = yesNo;
        return this;
    }

    /**
     * Set to true if you want to disallow the highlighted view to be clicked through the hole,
     * set to false if you want to allow the highlighted view to be clicked through the hole
     *
     * @param yesNo
     * @return return Overlay instance for chaining purpose
     */
    public Overlay disableClickThroughHole(boolean yesNo) {
        mDisableClickThroughHole = yesNo;
        return this;
    }

    public Overlay setStyle(HoleStyle holeStyle) {
        mHoleStyle = holeStyle;
        return this;
    }

    /**
     * Set enter animation
     *
     * @param enterAnimation
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setEnterAnimation(Animation enterAnimation) {
        mEnterAnimation = enterAnimation;
        return this;
    }

    public Overlay setEnterAnimationSet(AnimationSet enterAnimation) {
        mEnterAnimation = enterAnimation;
        return this;
    }

    /**
     * Set exit animation
     *
     * @param exitAnimation
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setExitAnimation(Animation exitAnimation) {
        mExitAnimation = exitAnimation;
        return this;
    }

    /**
     * Set {@link Overlay#mOnClickListener} for the {@link Overlay}
     *
     * @param onClickListener
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        return this;
    }

    /**
     * This method sets the hole's radius.
     * If this is not set, the size of view hole fill follow the max(view.width, view.height)
     * If this is set, it will take precedence
     * It only has effect when {@link Overlay.Style#CIRCLE} is chosen
     *
     * @param holeRadius the radius of the view hole, setting 0 will make the hole disappear, in pixels
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setHoleRadius(int holeRadius) {
        mHoleRadius = holeRadius;
        return this;
    }


    /**
     * This method sets offsets to the hole's position relative the position of the targeted view.
     *
     * @param offsetLeft left offset, in pixels
     * @param offsetTop  top offset, in pixels
     * @return {@link Overlay} instance for chaining purpose
     */
    public Overlay setHoleOffsets(int offsetLeft, int offsetTop) {
        mHoleOffsetLeft = offsetLeft;
        mHoleOffsetTop = offsetTop;
        return this;
    }

    /**
     * This method sets the padding to be applied to the hole cutout from the overlay
     *
     * @param paddingDp padding, in dp
     * @return {@link Overlay} intance for chaining purpose
     */
    public Overlay setHolePadding(int paddingDp) {
        mPaddingDp = paddingDp;
        return this;
    }

    /**
     * This method sets the radius for the rounded corner
     * It only has effect when {@link Overlay.Style#ROUNDED_RECTANGLE} is chosen
     *
     * @param roundedCornerRadiusDp padding, in pixels
     * @return {@link Overlay} intance for chaining purpose
     */
    public Overlay setRoundedCornerRadius(int roundedCornerRadiusDp) {
        mRoundedCornerRadiusDp = roundedCornerRadiusDp;
        return this;
    }
}

package com.vexanium.vexgift.widget.guideview.bubbletooltip;

import android.app.Activity;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.util.MeasureUtil;


/**
 * Created by mac on 5/30/17.
 */

public class BubbleToolTip {
    public String mDescription;
    public Animation mEnterAnimation, mExitAnimation;
    public int mGravity;
    public View.OnClickListener mOnClickListener;
    public ArrowDirection arrowDirection;
    public View target;
    public View view;
    public int mWidth;
    public int mWidthPercent;
    public int marginLeft;
    public int marginRight;
    public int marginTop;
    public int marginBottom;
    LayoutInflater layoutInflater;
    private Activity activity;

    public BubbleToolTip(Activity activity) {
        mDescription = "";
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(1000);
        mEnterAnimation.setFillAfter(true);
        mEnterAnimation.setInterpolator(new BounceInterpolator());
        mWidth = -1;
        mWidthPercent = -1;
        arrowDirection = ArrowDirection.TOP_CENTER;
        mGravity = Gravity.CENTER;
        this.activity = activity;
        layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(this.getView(), null);
    }

    public BubbleToolTip setMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {

        this.marginBottom = MeasureUtil.dip2px(activity, marginBottom);
        this.marginLeft = MeasureUtil.dip2px(activity, marginLeft);
        this.marginRight = MeasureUtil.dip2px(activity, marginRight);
        this.marginTop = MeasureUtil.dip2px(activity, marginTop);
        return this;
    }

    public @LayoutRes
    int getView(ArrowDirection arrowDirection){
        switch (arrowDirection){
            case TOP_LEFT: return R.layout.item_bubble_tooltip_top_left;
            case TOP_RIGHT: return R.layout.item_bubble_tooltip_top_right;
            case TOP_CENTER: return R.layout.item_bubble_tooltip_top;
            case LEFT: return R.layout.item_bubble_tooltip_left;
            case RIGHT: return R.layout.item_bubble_tooltip_right;
            case BOTTOM_LEFT: return R.layout.item_bubble_tooltip_bottom_left;
            case BOTTOM_CENTER: return R.layout.item_bubble_tooltip_bottom;
            case BOTTOM_RIGHT: return R.layout.item_bubble_tooltip_bottom_right;
            default:return R.layout.item_bubble_tooltip_top;
        }
    }

    public
    @LayoutRes
    int getView() {
        return getView(this.arrowDirection);
    }

    public BubbleToolTip target(View view) {
        this.target = view;
        return this;
    }

    public BubbleToolTip setWidthPercent(@IntRange(from = 0, to = 100) int percent) {
        this.mWidthPercent = percent;
        return this;
    }

    public BubbleToolTip arrowDirection(ArrowDirection arrowDirection) {
        this.arrowDirection = arrowDirection;
        view = layoutInflater.inflate(this.getView(arrowDirection), null);
        return this;
    }

    public BubbleToolTip setDescription(String description) {
        mDescription = description;
        return this;
    }

    public BubbleToolTip setGravity(int gravity) {
        mGravity = gravity;
        return this;
    }

    public BubbleToolTip setWidth(int px) {
        if (px >= 0) mWidth = px;
        return this;
    }

    public BubbleToolTip setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        return this;
    }
}

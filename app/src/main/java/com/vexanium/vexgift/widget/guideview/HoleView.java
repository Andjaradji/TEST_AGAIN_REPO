package com.vexanium.vexgift.widget.guideview;

import android.app.Activity;
import android.graphics.RectF;
import android.support.annotation.Size;
import android.view.View;
import android.view.ViewParent;

import com.vexanium.vexgift.util.MeasureUtil;


/**
 * Created by mac on 5/31/17.
 */

public class HoleView {

    public int loc[];
    public View view;
    public int padding = 4;
    public int radius = 0;
    public RectF mRectF;
    public int paddingLeft = 0;
    public int paddingTop = 0;
    public int paddingRight = 0;
    public int paddingBottom = 0;
    public boolean isViewPager;
    public boolean isAlwaysAllowClick;
    public HoleStyle holeStyle;

    public HoleView(View view) {
        this.view = view;
        holeStyle = HoleStyle.UN_SET;
        isViewPager = false;
        isAlwaysAllowClick = false;
    }

    public HoleView holeStyle(HoleStyle holeStyle) {
        this.holeStyle = holeStyle;
        return this;
    }

    public HoleView isViewPager(boolean isViewPager) {
        this.isViewPager = isViewPager;
        return this;
    }

    public HoleView isAlwaysAllowClick(boolean isAlwaysAllowClick) {
        this.isAlwaysAllowClick = isAlwaysAllowClick;
        return this;
    }

    public int getHeight() {
        return view.getHeight();
    }

    public int getWidth() {
        return view.getWidth();
    }

    public ViewParent getParent() {
        return view.getParent();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        view.setOnClickListener(onClickListener);
    }

    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        view.setOnTouchListener(onTouchListener);
    }

    public void setClickable(boolean isClickAble) {
        view.setClickable(isClickAble);
    }


    public void getLocationOnScreen(@Size(2) int[] pos) {
        view.getLocationOnScreen(pos);
        this.loc = pos;
    }

    public HoleView setPadding(Activity activity, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.paddingLeft = MeasureUtil.dip2px(activity, paddingLeft);
        this.paddingTop = MeasureUtil.dip2px(activity, paddingTop);
        this.paddingBottom = MeasureUtil.dip2px(activity, paddingBottom);
        this.paddingRight = MeasureUtil.dip2px(activity, paddingRight);
        return this;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }
}

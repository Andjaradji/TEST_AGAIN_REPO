package com.vexanium.vexgift.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.util.ClickUtil;

import java.util.ArrayList;


/**
 * Created by mac on 11/17/17.
 */

public class IconTextTabBarView extends RelativeLayout {
    public static int mSelectedTab = 0;
    public ViewPager.OnPageChangeListener delegatePageListener;
    private ArrayList<IconTextTabView> tabViews;
    private View view;
    private FlexboxLayout flexboxLayout;
    private float mOffset = 0f;
    private Context context;
    private ViewPager viewPager;

    public IconTextTabBarView(Context context) {
        super(context);
        init(context);
    }

    public IconTextTabBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconTextTabBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        setWillNotDraw(false);
    }

//    public CustomTabBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context);
//    }

    public void init(Context context) {
        this.context = context;
        view = View.inflate(context, R.layout.item_custom_tab_bar_view, this);
        flexboxLayout = view.findViewById(R.id.flexbox_tab_bar);
        tabViews = new ArrayList<>();
    }

    public void addTabView(final int idx, int icon, String title) {
        if (tabViews != null) {
            final IconTextTabView tabView = new IconTextTabView(context);
            tabView.setIdx(idx);
            tabView.setIcon(icon);
            tabView.setText(title);
            //tabView.setBackgroundResource(R.drawable.shape_ripple_transparent);
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    viewPager.setCurrentItem(idx, false);
                }
            });
            tabViews.add(idx, tabView);
        }
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;

        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        notifyDataSetChanged();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mSelectedTab = position;
        mOffset = positionOffset;

        invalidate();

        if (delegatePageListener != null) {
            delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        //based on the position setting tab alpha as 1 if it's selected or others set is as 0.5f
        for (IconTextTabView tabView : tabViews) {
            tabView.updateView(tabView.getIdx() == position);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
        }

        if (delegatePageListener != null) {
            delegatePageListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageSelected(int position) {
        if (delegatePageListener != null) {
            delegatePageListener.onPageSelected(position);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }


    public void notifyDataSetChanged() {
        if (viewPager == null) {
            throw new IllegalStateException("ViewPager is null.");
        }

        this.flexboxLayout.removeAllViews();

        int tabCount = viewPager.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {
            this.flexboxLayout.addView(tabViews.get(i));
        }

        //removing swiping animation effect from viewPager
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mSelectedTab = viewPager.getCurrentItem();
            }
        });

    }
}

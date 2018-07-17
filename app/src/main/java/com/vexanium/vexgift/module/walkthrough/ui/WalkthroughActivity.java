package com.vexanium.vexgift.module.walkthrough.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.widget.CustomViewPager;
import com.vexanium.vexgift.widget.FadePageTransformer;

/**
 * Created by Amang on 16/07/2018.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_walkthrough)
public class WalkthroughActivity extends BaseActivity {

    public static final int FRAGMENT_FIRST = 0;
    public static final int FRAGMENT_SECOND = 1;
    public static final int FRAGMENT_THIRD = 2;
    public static final int FRAGMENT_FOURTH = 3;
    public static final int PAGE_COUNT = 4;

    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    private FourthFragment fourthFragment;

    private CustomViewPager mCustomViewPager;
    private LinearLayout mNextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mNextButton = (LinearLayout) findViewById(R.id.ll_walkthrough_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currPos = mCustomViewPager.getCurrentItem();
                if(currPos != PAGE_COUNT-1){
                    mCustomViewPager.setCurrentItem(currPos+1,true);
                }else{
                    Intent intent = new Intent();
                    intent.setClass(WalkthroughActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        setWalkthroughPager();
        setPagerListener();
    }

    @Override
    protected void initView() {

    }

    private void setWalkthroughPager(){
        WalkthroughAdapter walkthroughAdapter = new WalkthroughAdapter(getSupportFragmentManager());
        mCustomViewPager = (CustomViewPager) findViewById(R.id.cvp_walkthrough);
        mCustomViewPager.setAdapter(walkthroughAdapter);
        mCustomViewPager.setPageTransformer(true,new FadePageTransformer());
        mCustomViewPager.setOffscreenPageLimit(PAGE_COUNT);
        mCustomViewPager.setPagingEnabled(true);
        mCustomViewPager.setCurrentItem(0, false);
    }

    private void setPagerListener() {
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class WalkthroughAdapter extends FragmentStatePagerAdapter {

        WalkthroughAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case FRAGMENT_FIRST:
                    if (firstFragment == null) {
                        firstFragment = FirstFragment.newInstance();
                    }
                    return firstFragment;
                case FRAGMENT_SECOND:
                    if (secondFragment == null) {
                        secondFragment = SecondFragment.newInstance();
                    }
                    return secondFragment;
                case FRAGMENT_THIRD:
                    if (thirdFragment == null) {
                        thirdFragment = ThirdFragment.newInstance();
                    }
                    return thirdFragment;
                case FRAGMENT_FOURTH:
                    if (fourthFragment == null) {
                        fourthFragment = FourthFragment.newInstance();
                    }
                    return fourthFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }


}

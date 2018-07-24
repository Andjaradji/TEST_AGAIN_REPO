package com.vexanium.vexgift.module.vexpoint.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.widget.IconTextTabBarView;
import com.vexanium.vexgift.widget.LockableScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@ActivityFragmentInject(contentViewId = R.layout.activity_vexpoint)
public class VexPointActivity extends BaseActivity {

    private static final int POINT_RECORD_FRAGMENT = 0;
    private static final int INVITE_POINT_FRAGMENT = 1;
    private static final int PAGE_COUNT = 2;

    private PointRecordFragment pointRecordFragment;
    private InvitePointFragment invitePointFragment;

    private TextView mTvVp;
    private IconTextTabBarView mTabVp;
    private ViewPager mPagerVp;

    private View mVpShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

        mTvVp = findViewById(R.id.tv_vexpoint);
        mTabVp = findViewById(R.id.tab_vexpoint);
        mPagerVp = findViewById(R.id.vp_vexpoint);

        mVpShadow = findViewById(R.id.v_vexpoint_shadow);

        String pointRecord = getResources().getString(R.string.vexpoint_point_record);
        String invitePoint = getResources().getString(R.string.vexpoint_invite_point);


        mTabVp.addTabView(0, -1, pointRecord);
        mTabVp.addTabView(1, -1,  invitePoint);

        VpPagerAdapter vpPagerAdapter = new VpPagerAdapter(getSupportFragmentManager());
        mPagerVp.setAdapter(vpPagerAdapter);
        mPagerVp.setOffscreenPageLimit(PAGE_COUNT);
        mPagerVp.setCurrentItem(0, false);

        mTabVp.setViewPager(mPagerVp);
        setPagerListener();

        findViewById(R.id.back_button).setOnClickListener(this);

        //AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f,1f);
        //alphaAnimation.setDuration(1500);
        //alphaAnimation.setFillAfter(true);
        mVpShadow.setVisibility(View.VISIBLE);
        //mVpShadow.animate().alpha(1).setDuration(1000);

    }

    private void setPagerListener() {
        mPagerVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTabVp.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                KLog.i("Page: " + position);
                mTabVp.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mTabVp.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class VpPagerAdapter extends FragmentStatePagerAdapter {

        VpPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POINT_RECORD_FRAGMENT:
                    if (pointRecordFragment == null) {
                        pointRecordFragment = PointRecordFragment.newInstance();
                    }
                    return pointRecordFragment;
                case INVITE_POINT_FRAGMENT:
                    if (invitePointFragment == null) {
                        invitePointFragment = InvitePointFragment.newInstance();
                    }
                    return invitePointFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }

    /*@Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        Toast.makeText(VexPointActivity.this, "ended", Toast.LENGTH_SHORT).show();
    }*/
}

package com.vexanium.vexgift.module.vexpoint.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.util.AnimUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.widget.IconTextTabBarView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_vexpoint)
public class VexPointActivity extends BaseActivity {

    private static final int POINT_RECORD_FRAGMENT = 0;
    private static final int INVITE_POINT_FRAGMENT = 1;
    private static final int PAGE_COUNT = 2;

    private PointRecordFragment pointRecordFragment;
    private InvitePointFragment invitePointFragment;

    private TextView mTvVp;
    private TextView mTvCountdownVp;
    private IconTextTabBarView mTabVp;
    private ViewPager mPagerVp;
    private Observable<Integer> mVpObservable;
    private View mVpShadow;

    private View notifView;

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
        mTabVp.addTabView(1, -1, invitePoint);

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

        notifView = findViewById(R.id.rl_notif_info);

        mTvCountdownVp = findViewById(R.id.tv_countdown);

        mVpObservable = RxBus.get().register(RxBus.KEY_VP_RECORD_ADDED, Integer.class);
        mVpObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer vp) {
                String message = String.format(Locale.getDefault(), getString(R.string.vp_get_vp_from_snapshoot), vp);
                ((TextView)notifView.findViewById(R.id.tv_notif_info)).setText(message);
                AnimUtil.transTopIn(notifView,true, 300);
            }
        });

        long remainTime ;

        Calendar now  = Calendar.getInstance();


        CountDownTimer countDownTimer = new CountDownTimer(150000, 1000) {
            @Override
            public void onTick(long l) {
                String time = String.format(Locale.getDefault(), "%02d HOUR, %02d MIN, %02d SEC",
                        TimeUnit.MILLISECONDS.toHours(l),
                        TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                        TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                mTvCountdownVp.setText(time);
            }

            @Override
            public void onFinish() {
                long l = 0;
                String time = String.format(Locale.getDefault(), "%02d HOUR, %02d MIN, %02d SEC",
                        TimeUnit.MILLISECONDS.toHours(l),
                        TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                        TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                mTvCountdownVp.setText(time);
                RxBus.get().post(RxBus.KEY_VP_RECORD_ADDED, 150);

            }
        };
        countDownTimer.start();
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
        switch (v.getId()) {
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

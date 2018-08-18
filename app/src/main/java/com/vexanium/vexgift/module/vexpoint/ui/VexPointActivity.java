package com.vexanium.vexgift.module.vexpoint.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.UserAddress;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.module.referral.ui.ReferralActivity;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenter;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenterImpl;
import com.vexanium.vexgift.module.vexpoint.view.IVexpointView;
import com.vexanium.vexgift.util.AnimUtil;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.NetworkUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.IconTextTabBarView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_vexpoint, withLoadingAnim = false)
public class VexPointActivity extends BaseActivity<IVexpointPresenter> implements IVexpointView {

    private static final int POINT_RECORD_FRAGMENT = 0;
    private static final int INVITE_POINT_FRAGMENT = 1;
    private static final int PAGE_COUNT = 2;

    private SwipeRefreshLayout mRefreshLayout;
    private PointRecordFragment pointRecordFragment;
    private InvitePointFragment invitePointFragment;

    private ImageView mReferralButton;
    private TextView mTvVp;
    private TextView mTvVpGen;
    private IconTextTabBarView mTabVp;
    private ViewPager mPagerVp;
    private Observable<Integer> mVpObservable;
    private Observable<Integer> mVexAddressObservable;
    private View mVpShadow;

    private View notifView;

    private Subscription timeSubsription;
    private User user;

    Calendar verifTimeLeft;

    private boolean isTimeUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        mPresenter = new IVexpointPresenterImpl(this);
        user = User.getCurrentUser(this);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        verifTimeLeft = Calendar.getInstance();
        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestGetActAddress(user.getId());
            }
        });
        mReferralButton = findViewById(R.id.referral_button);
        mReferralButton.setOnClickListener(this);
        updateView();

        if (User.getUserAddressStatus() != 1) {
            mPresenter.requestGetActAddress(user.getId());
        }

        findViewById(R.id.back_button).setOnClickListener(this);

        if (NetworkUtil.isOnline(this)) {
            // TODO: 02/08/18 doSomething on VexPoint 
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof UserAddressResponse) {
                UserAddressResponse userAddressResponse = (UserAddressResponse) data;

                if (userAddressResponse != null && userAddressResponse.getUserAddress() != null) {
                    UserAddress userAddress = userAddressResponse.getUserAddress();
                    TpUtil tpUtil = new TpUtil(this);
                    tpUtil.put(TpUtil.KEY_USER_ADDRESS, JsonUtil.toString(userAddress));

                    if (userAddress.getStatus() == 1) {
                        User.setIsVexAddressSet(this, true);
                    } else {
                        User.setIsVexAddressSet(this, false);
                    }

                    updateView();
                }
            }
        } else if (errorResponse != null) {
            if (errorResponse.getMeta().getStatus() == 404) {
                User.setIsVexAddressSet(this, false);
            }

            updateView();
        }
    }

    private void updateView() {
        if (!User.getIsVexAddressSet(this)) {
            findViewById(R.id.rl_vp).setVisibility(View.GONE);

            if (User.getUserAddressStatus() == 0) {
                //mReferralButton.setVisibility(View.GONE);
                findViewById(R.id.ll_info).setVisibility(View.GONE);
                findViewById(R.id.ll_wait_address).setVisibility(View.VISIBLE);

                final UserAddress userAddress = User.getUserAddress();
                if (userAddress != null) {
                    verifTimeLeft.setTimeInMillis((userAddress.getRemainingTime() * 1000) + Calendar.getInstance().getTimeInMillis());
                    ViewUtil.setText(this, R.id.tv_vex_address, userAddress.getActAddress());
                    ViewUtil.setText(this, R.id.tv_vex_count, userAddress.getAmount() + "");
                    ViewUtil.setText(this, R.id.tv_address_send_to, userAddress.getTransferTo());

                    findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            StaticGroup.copyToClipboard(VexPointActivity.this, userAddress.getTransferTo());
                        }
                    });
                }
            } else {
               // mReferralButton.setVisibility(View.GONE);
                findViewById(R.id.ll_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_wait_address).setVisibility(View.GONE);
                findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        Intent intent = new Intent(VexPointActivity.this, VexAddressActivity.class);
                        startActivity(intent);
                    }
                });

                if (User.getUserAddressStatus() == 2) {
                    ViewUtil.setText(this, R.id.tv_info_title, getString(R.string.vexpoint_need_address_subtitle_rejected));
                    ViewUtil.setText(this, R.id.tv_info_desc, getString(R.string.vexpoint_need_address_desc_rejected));
                }
            }

        } else {
            mReferralButton.setVisibility(View.VISIBLE);
            findViewById(R.id.rl_vp).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_info).setVisibility(View.GONE);
            findViewById(R.id.ll_wait_address).setVisibility(View.GONE);


            mTvVp = findViewById(R.id.tv_vexpoint);
            mTvVpGen = findViewById(R.id.tv_vexpoint_generated);
            mTabVp = findViewById(R.id.tab_vexpoint);
            mPagerVp = findViewById(R.id.vp_vexpoint);

            mTvVp.setText("" + user.getVexPoint());
            mTvVpGen.setText("" + 150);

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

            mVpObservable = RxBus.get().register(RxBus.KEY_VP_RECORD_ADDED, Integer.class);
            mVpObservable.subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer vp) {
                    String message = String.format(Locale.getDefault(), getString(R.string.vp_get_vp_from_snapshoot), vp);
                    ((TextView) notifView.findViewById(R.id.tv_notif_info)).setText(message);
                    AnimUtil.transTopIn(notifView, true, 300);
                }
            });
        }
        mRefreshLayout.setEnabled(true);
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
            case R.id.referral_button:
                Intent intent = new Intent(this, ReferralActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        KLog.v("VexPointActivity", "onPause: ");
        super.onPause();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
            timeSubsription = null;
        }
    }

    @Override
    protected void onStart() {
        KLog.v("VexPointActivity", "onStart: ");
        super.onStart();
        startDateTimer();
    }

    @Override
    protected void onDestroy() {
        KLog.v("VexPointActivity", "onDestroy: ");
        super.onDestroy();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
        }
        if (mVpObservable != null) {
            RxBus.get().unregister(RxBus.KEY_VP_RECORD_ADDED, mVpObservable);
        }
    }

    @Override
    protected void onResume() {
        KLog.v("VexPointActivity", "onResume: ");
        super.onResume();
        startDateTimer();
    }

    private void startDateTimer() {
        if (timeSubsription == null && StaticGroup.isScreenOn(this, true)) {
            timeSubsription = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            //KLog.v("Date Time called");
                            if (!StaticGroup.isScreenOn(VexPointActivity.this, true)) {
                                if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
                                    timeSubsription.unsubscribe();
                                }
                            } else {
                                setWatchText();
                                setVerifTimeText();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                        }
                    });
        }
    }

    private void setWatchText() {
        TextView mTvCountdownVp = findViewById(R.id.tv_countdown);
        Calendar now = Calendar.getInstance();
        Calendar nextSnapshoot = Calendar.getInstance();
        if (now.get(Calendar.HOUR_OF_DAY) >= 12) {
            nextSnapshoot.add(Calendar.DATE, 1);
            nextSnapshoot.set(Calendar.HOUR_OF_DAY, 0);
        } else {
            nextSnapshoot.set(Calendar.HOUR_OF_DAY, 12);
        }
        nextSnapshoot.set(Calendar.MINUTE, 0);
        nextSnapshoot.set(Calendar.SECOND, 0);
        nextSnapshoot.set(Calendar.MILLISECOND, 0);

        long remainTime = nextSnapshoot.getTimeInMillis() - now.getTimeInMillis();

        String time = String.format(Locale.getDefault(), "%02d HOUR, %02d MIN, %02d SEC",
                TimeUnit.MILLISECONDS.toHours(remainTime),
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));

        mTvCountdownVp.setText(time);
    }

    private void setVerifTimeText() {
        TextView tvHour = findViewById(R.id.tv_time_left);

        Calendar now = Calendar.getInstance();

        long remainTime = verifTimeLeft.getTimeInMillis() - now.getTimeInMillis();
        remainTime = remainTime - TimeUnit.DAYS.toMillis(TimeUnit.MILLISECONDS.toDays(remainTime));

        if (remainTime < 0 && !isTimeUp) {
            isTimeUp = true;
            mPresenter.requestGetActAddress(user.getId());
        } else if (remainTime > 0) {
            isTimeUp = false;
        }

        String time = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));

        tvHour.setText(time);
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

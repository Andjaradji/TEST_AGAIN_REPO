package com.vexanium.vexgift.module.referral.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.bean.model.Referral;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserReferralResponse;
import com.vexanium.vexgift.bean.response.WalletReferralResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenterImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.widget.IconTextTabBarView;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_referral_special_event_detail, withLoadingAnim = true, toolbarTitle = R.string.referral_detail_title)
public class ReferralSpecialEventDetailActivity extends BaseActivity<IWalletPresenter> implements IWalletView {
    private static final int COUNTED_REFERRAL_FRAGMENT = 0;
    private static final int UNCOUNTED_REFERAL_FRAGMENT = 1;
    private static final int PAGE_COUNT = 2;

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody, mTvInvitedCount, mTvCounted;

    UserReferralResponse userReferralResponse;
    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<Referral> mAdapter;
    RecyclerView mRecyclerview;
    private ArrayList<Referral> userReferrals;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;

    private IconTextTabBarView mTabWallet;
    private ViewPager mPagerWallet;
    private ReferralListFragment countedReferralListFragment;
    private ReferralListFragment uncountedReferralListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IWalletPresenterImpl(this);

        userReferralResponse = TableContentDaoUtil.getInstance().getReferrals();
        if (userReferralResponse != null) {
            userReferrals = userReferralResponse.getReferrals();
        }
        if (userReferrals == null) {
            userReferrals = new ArrayList<>();
        }

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestGetWalletReferral(user.getId());
            }
        });

        mPresenter.requestGetWalletReferral(user.getId());

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mTvInvitedCount = findViewById(R.id.tv_referral_invited_user_count);
        mTvCounted = findViewById(R.id.tv_counted_user);

        String countedReferralTitle = getResources().getString(R.string.wallet_referral_counted);
        String uncountedReferralTitle = getResources().getString(R.string.wallet_referral_uncounted);

        mTabWallet = findViewById(R.id.tab_wallet);
        mPagerWallet = findViewById(R.id.vp_wallet);

        mTabWallet.addTabView(0, -1, countedReferralTitle);
        mTabWallet.addTabView(1, -1, uncountedReferralTitle);

        TextPagerAdapter textPagerAdapter = new TextPagerAdapter(getSupportFragmentManager());
        mPagerWallet.setAdapter(textPagerAdapter);
        mPagerWallet.setOffscreenPageLimit(PAGE_COUNT);
        mPagerWallet.setCurrentItem(0, false);

        mTabWallet.setViewPager(mPagerWallet);
        setPagerListener();

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof WalletReferralResponse) {
                WalletReferralResponse walletReferralResponse = (WalletReferralResponse) data;
                TableContentDaoUtil.getInstance().saveWalletReferralsToDb(JsonUtil.toString(walletReferralResponse));

                int referralsCount = walletReferralResponse.getReferralsCount();
                int countedCount = walletReferralResponse.getCountedReferralsCount();
                mTvInvitedCount.setText(String.valueOf(referralsCount));
                String countedUser = getString(R.string.wallet_referral_counted_user) + " " + String.valueOf(countedCount);
                mTvCounted.setText(countedUser);
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }


    private void setPagerListener() {
        mPagerWallet.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTabWallet.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                KLog.i("Page: " + position);
                mTabWallet.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mTabWallet.onPageScrollStateChanged(state);
            }
        });
    }

    public class TextPagerAdapter extends FragmentStatePagerAdapter {

        TextPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case COUNTED_REFERRAL_FRAGMENT:
                    if (countedReferralListFragment == null) {
                        countedReferralListFragment = ReferralListFragment.newInstance(true);
                    }
                    return countedReferralListFragment;
                case UNCOUNTED_REFERAL_FRAGMENT:
                    if (uncountedReferralListFragment == null) {
                        uncountedReferralListFragment = ReferralListFragment.newInstance(false);
                    }
                    return uncountedReferralListFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            //TODO PAGE_COUNT
            return 2;
        }

    }

}

package com.vexanium.vexgift.module.wallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Wallet;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenterImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.LocaleUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.IconTextTabBarView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;


@ActivityFragmentInject(contentViewId = R.layout.fragment_wallet, withLoadingAnim = true)
public class WalletFragment extends BaseFragment<IWalletPresenter> implements IWalletView {

    private static final int TRANSACTION_RECORD_FRAGMENT = 0;
    private static final int BONUS_RECORD_FRAGMENT = 1;
    private static final int PAGE_COUNT = 2;

    LinearLayout mGenerateView;
    TextView mTvCountdownBonus;

    private IconTextTabBarView mTabWallet;
    private ViewPager mPagerWallet;

    private TransactionRecordFragment transactionRecordFragment;
    private BonusRecordFragment bonusRecordFragment;

    private Subscription timeSubsription;

    private SwipeRefreshLayout mRefreshLayout;
    private NestedScrollView mNscrollView;

    private User user;
    private View fragmentView;
    private WalletResponse walletResponse;

    private boolean isComingSoon = false;
    private boolean isBonusShown = false;
    private String guideUrl = "";

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        user = User.getCurrentUser(getActivity());
        mPresenter = new IWalletPresenterImpl(this);
        fragmentView = fragmentRootView;
        mPresenter.requestGetWallet(user.getId());

        ViewUtil.setText(fragmentRootView, R.id.tv_toolbar_title, getString(R.string.shortcut_my_wallet));

        mGenerateView = fragmentRootView.findViewById(R.id.ll_wallet_address_generate);

        isBonusShown = StaticGroup.isWalletBonusShown();
        if (isBonusShown) {
            mTvCountdownBonus = fragmentRootView.findViewById(R.id.tv_countdown_temp);
            fragmentRootView.findViewById(R.id.tv_countdown_temp).setVisibility(View.VISIBLE);

        } else {
            mTvCountdownBonus = fragmentRootView.findViewById(R.id.tv_countdown);
            fragmentRootView.findViewById(R.id.tv_countdown_temp).setVisibility(View.GONE);
        }

        mTabWallet = fragmentRootView.findViewById(R.id.tab_wallet);
        mPagerWallet = fragmentRootView.findViewById(R.id.vp_wallet);
        mNscrollView = fragmentRootView.findViewById(R.id.nsv_wallet);

        fragmentRootView.findViewById(R.id.ll_deposit_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.ll_withdraw_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.btn_generate_wallet).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.ll_personal_wallet).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.ll_expense_wallet).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.iv_ask).setOnClickListener(this);

        mRefreshLayout = fragmentRootView.findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestGetWallet(user.getId());
            }
        });

        String transactionRecord = getResources().getString(R.string.wallet_transaction_record);
        String bonusRecord = getResources().getString(R.string.wallet_bonus_record);

        mTabWallet.addTabView(0, -1, transactionRecord);
        mTabWallet.addTabView(1, -1, bonusRecord);

        TextPagerAdapter textPagerAdapter = new TextPagerAdapter(getChildFragmentManager());
        mPagerWallet.setAdapter(textPagerAdapter);
        mPagerWallet.setOffscreenPageLimit(PAGE_COUNT);
        mPagerWallet.setCurrentItem(0, false);

        mTabWallet.setViewPager(mPagerWallet);
        setPagerListener();

        walletResponse = TableContentDaoUtil.getInstance().getWallet();
        if (walletResponse != null) {
            updateViewWallet(walletResponse);
        }

        App.setTextViewStyle((ViewGroup) fragmentRootView);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Wallet Fragment View")
                .putContentType("Wallet")
                .putContentId("wallet"));

        String lang = LocaleUtil.getLanguage(getContext());
        switch (lang) {
            case "id":
                guideUrl = "ask_wallet_detail_link_id";
                break;

            case "zh":
                guideUrl = "ask_wallet_detail_link_zh";
                break;

            case "en":
            default:
                guideUrl = "ask_wallet_detail_link_en";
                break;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("WalletFragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof WalletResponse) {
                KLog.v("WalletFragment", "handleResult: HMtes 2");
                WalletResponse walletResponse = (WalletResponse) data;
                if (walletResponse != null) {
                    if (walletResponse.getWallet() != null) {
                        TableContentDaoUtil.getInstance().saveWalletsToDb(JsonUtil.toString(walletResponse));
                        updateViewWallet(walletResponse);
                    } else {
                        updateViewWallet(walletResponse);
                    }
                    RxBus.get().post(RxBus.KEY_WALLET_TRANSACTION_RECORD_ADDED, true);
                }
            }

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this.getContext(), errorResponse);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        String deepLinkUrl;
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_ask:
                deepLinkUrl = StaticGroup.getStringValFromSettingKey(guideUrl);
                ((MainActivity) getActivity()).openDeepLink(deepLinkUrl);
                break;
            case R.id.ll_personal_wallet:
                deepLinkUrl = StaticGroup.getStringValFromSettingKey(guideUrl);
                ((MainActivity) getActivity()).openDeepLink(deepLinkUrl);
                break;
            case R.id.ll_expense_wallet:
                deepLinkUrl = StaticGroup.getStringValFromSettingKey(guideUrl);
                ((MainActivity) getActivity()).openDeepLink(deepLinkUrl);
                break;
            case R.id.ll_deposit_button:
                intent = new Intent(this.getActivity(), WalletDepositActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_withdraw_button:
                intent = new Intent(this.getActivity(), WalletWithdrawActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_generate_wallet:
                mPresenter.requestCreateWallet(user.getId());
                break;

        }
    }

    private void updateViewWallet(WalletResponse walletResponse) {
        boolean isWalletExist = (walletResponse != null && walletResponse.getWallet() != null);
        if (fragmentView != null) {
            if (isComingSoon) {
                fragmentView.findViewById(R.id.toolbar_record).setVisibility(View.GONE);
                fragmentView.findViewById(R.id.vp_wallet).setVisibility(View.GONE);
                fragmentView.findViewById(R.id.ll_wallet_main).setVisibility(View.GONE);
                fragmentView.findViewById(R.id.ll_wallet_address_generate).setVisibility(View.GONE);
                fragmentView.findViewById(R.id.iv_coming_soon).setVisibility(View.VISIBLE);
            } else {
                fragmentView.findViewById(R.id.iv_coming_soon).setVisibility(View.GONE);
                fragmentView.findViewById(R.id.toolbar_record).setVisibility(isWalletExist ? View.VISIBLE : View.GONE);
                fragmentView.findViewById(R.id.vp_wallet).setVisibility(isWalletExist ? View.VISIBLE : View.GONE);
                fragmentView.findViewById(R.id.ll_wallet_main).setVisibility(isWalletExist ? View.VISIBLE : View.GONE);
                fragmentView.findViewById(R.id.ll_wallet_address_generate).setVisibility(!isWalletExist ? View.VISIBLE : View.GONE);
                if (isWalletExist) {
                    Wallet wallet = walletResponse.getWallet();
                    ViewUtil.setText(fragmentView, R.id.tv_total_wallet, "" + wallet.getBalance());
                    ViewUtil.setText(fragmentView, R.id.tv_personal_wallet, "" + wallet.getPersonalWalletBalance());
                    ViewUtil.setText(fragmentView, R.id.tv_expense_wallet, "" + wallet.getExpenseWalletBalance());
                    ViewUtil.setText(fragmentView, R.id.tv_deposit_bonus, walletResponse.getExpectedStakingBonus() + "");
                    ViewUtil.setText(fragmentView, R.id.tv_referral_bonus, walletResponse.getExpectedReferralBonus() + "");
                    if (isBonusShown) {
                        ViewUtil.setText(fragmentView, R.id.tv_countdown, walletResponse.getNextBonusPayoutAmount() + "");
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        KLog.v("WalletFragment", "onPause: ");
        super.onPause();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
            timeSubsription = null;
        }
    }

    @Override
    public void onStart() {
        KLog.v("WalletFragment", "onStart: ");
        super.onStart();
//        startDateTimer();
    }

    @Override
    public void onDestroy() {
        KLog.v("WalletFragment", "onDestroy: ");
        super.onDestroy();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        KLog.v("WalletFragment", "onResume: ");
        super.onResume();
        startDateTimer();
        mNscrollView.scrollTo(0, 0);
//        user = User.getCurrentUser(this);
//        if (User.getUserAddressStatus() != 1) {
//            mPresenter.requestGetActAddress(user.getId());
//        }
        mPresenter.requestGetWallet(user.getId());
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

    private void startDateTimer() {
        if (timeSubsription == null && StaticGroup.isScreenOn(this.getActivity(), true)) {
            timeSubsription = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            //KLog.v("Date Time called");
                            if (!StaticGroup.isScreenOn(WalletFragment.this.getActivity(), true)) {
                                if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
                                    timeSubsription.unsubscribe();
                                }
                            } else {
                                setWatchText();
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

        Calendar now = Calendar.getInstance();
        Calendar nextSnapshoot = Calendar.getInstance();
//        nextSnapshoot.add(Calendar.DATE, 1);
        nextSnapshoot.set(Calendar.HOUR_OF_DAY, 12);
        nextSnapshoot.set(Calendar.MINUTE, 0);
        nextSnapshoot.set(Calendar.SECOND, 0);
        nextSnapshoot.set(Calendar.MILLISECOND, 0);
        nextSnapshoot.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));

        long remainTime = nextSnapshoot.getTimeInMillis() - now.getTimeInMillis();

        String time = String.format(Locale.getDefault(), getString(R.string.time_hour_min_sec),
                TimeUnit.MILLISECONDS.toHours(remainTime),
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));

        mTvCountdownBonus.setText(time);
    }

    public class TextPagerAdapter extends FragmentStatePagerAdapter {

        TextPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TRANSACTION_RECORD_FRAGMENT:
                    if (transactionRecordFragment == null) {
                        transactionRecordFragment = TransactionRecordFragment.newInstance();
                    }
                    return transactionRecordFragment;
                case BONUS_RECORD_FRAGMENT:
                    if (bonusRecordFragment == null) {
                        bonusRecordFragment = BonusRecordFragment.newInstance();
                    }
                    return bonusRecordFragment;

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

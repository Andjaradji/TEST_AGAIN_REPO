package com.vexanium.vexgift.module.wallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenterImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.util.JsonUtil;
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


@ActivityFragmentInject(contentViewId = R.layout.fragment_wallet)
public class WalletFragment extends BaseFragment<IWalletPresenter> implements IWalletView {

    private static final int TRANSACTION_RECORD_FRAGMENT = 0;
    private static final int BONUS_RECORD_FRAGMENT = 1;
    private static final int PAGE_COUNT = 2;

    LinearLayout mErrorView, mGenerateView;
    TextView mTvCountdownVp;

    private TextView mTvWallet;
    private TextView mTvWalletBonusGen;
    private IconTextTabBarView mTabWallet;
    private ViewPager mPagerWallet;

    private TransactionRecordFragment transactionRecordFragment;
    private BonusRecordFragment bonusRecordFragment;

    private Subscription timeSubsription;

    private SwipeRefreshLayout mRefreshLayout;
    private NestedScrollView mNscrollView;

    private User user;
    private boolean isAlreadyHaveAddress;
    private View fragmentView;


    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        user = User.getCurrentUser(this.getContext());
        mPresenter = new IWalletPresenterImpl(this);
        fragmentView = fragmentRootView;

        ViewUtil.setText(fragmentRootView, R.id.tv_toolbar_title, getString(R.string.shortcut_my_wallet));

        mGenerateView = fragmentRootView.findViewById(R.id.ll_wallet_address_generate);
        mTvCountdownVp = fragmentRootView.findViewById(R.id.tv_countdown);
        mTabWallet = fragmentRootView.findViewById(R.id.tab_wallet);
        mPagerWallet = fragmentRootView.findViewById(R.id.vp_wallet);
        mNscrollView = fragmentRootView.findViewById(R.id.nsv_wallet);

        fragmentRootView.findViewById(R.id.ll_deposit_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.ll_withdraw_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.btn_generate_wallet).setOnClickListener(this);

//        ImageView mIvComingSoon = fragmentRootView.findViewById(R.id.iv_coming_soon);

        mRefreshLayout = fragmentRootView.findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                setRecordlist(FixtureData.tokenList);
            }
        });

        mRefreshLayout.setEnabled(false);

        //Coming soon view
//        mIvComingSoon.setVisibility(View.GONE);

//        if (mIvComingSoon.getVisibility() != View.VISIBLE) {
//            layoutListManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//            mRecycler.setLayoutManager(layoutListManager);
//        }

//        mTvWallet.setText(convertVpFormat(user.getVexPoint()));
//        mTvWalletBonusGen.setText("");

        String transactionRecord = getResources().getString(R.string.wallet_transaction_record);
        String bonusRecord = getResources().getString(R.string.wallet_bonus_record);

        mTabWallet.addTabView(0, -1, transactionRecord);
        mTabWallet.addTabView(1, -1, bonusRecord);

        VpPagerAdapter vpPagerAdapter = new VpPagerAdapter(getActivity().getSupportFragmentManager());
        mPagerWallet.setAdapter(vpPagerAdapter);
        mPagerWallet.setOffscreenPageLimit(PAGE_COUNT);
        mPagerWallet.setCurrentItem(0, false);

        mTabWallet.setViewPager(mPagerWallet);
        setPagerListener();

        mPresenter.requestGetWallet(user.getId());

        App.setTextViewStyle((ViewGroup) fragmentRootView);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Wallet Fragment View")
                .putContentType("Wallet")
                .putContentId("wallet"));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("WalletFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof WalletResponse) {
                WalletResponse walletResponse = (WalletResponse) data;
                if (walletResponse != null) {
                    if (walletResponse.getWallet() != null) {
                        TableContentDaoUtil.getInstance().saveBoxsToDb(JsonUtil.toString(walletResponse));
                        updateViewWallet(walletResponse);
                    } else {
                        updateViewWallet(walletResponse);
                    }
                }

            }

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this.getContext(), errorResponse);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
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
        boolean isWalletExist = (walletResponse != null);
        if (fragmentView != null) {
            fragmentView.findViewById(R.id.toolbar_record).setVisibility(isWalletExist ? View.VISIBLE : View.GONE);
            fragmentView.findViewById(R.id.vp_wallet).setVisibility(isWalletExist ? View.VISIBLE : View.GONE);
            fragmentView.findViewById(R.id.ll_wallet_main).setVisibility(isWalletExist ? View.VISIBLE : View.GONE);
            fragmentView.findViewById(R.id.ll_wallet_address_generate).setVisibility(!isWalletExist ? View.VISIBLE : View.GONE);
            if(isWalletExist){
                Wallet wallet = walletResponse.getWallet();
                ViewUtil.setText(fragmentView, R.id.tv_total_wallet, ""+ wallet.getBalance());
                ViewUtil.setText(fragmentView, R.id.tv_personal_wallet, ""+ wallet.getPersonalWalletBalance());
                ViewUtil.setText(fragmentView, R.id.tv_expense_wallet, ""+ wallet.getExpenseWalletBalance());
                ViewUtil.setText(fragmentView, R.id.tv_deposit_bonus, walletResponse.getExpectedStakingBonus()+"");
                ViewUtil.setText(fragmentView, R.id.tv_referral_bonus, walletResponse.getExpectedReferralBonus()+"");
            }
        }
    }

    //    private void setRecordlist(ArrayList<WalletToken> dataList) {
//        mRefreshLayout.setRefreshing(false);
//        float totalAsset = 0f;
//        for (WalletToken token : FixtureData.tokenList) {
//            totalAsset += token.getAmount() * token.getEstPriceInIDR();
//        }
//
//        DecimalFormat df = new DecimalFormat("#,###.##");
//
//        mTotalAsset.setText(df.format(totalAsset));
//
//        if (mAdapter == null) {
//            mAdapter = new BaseRecyclerAdapter<WalletToken>(getActivity(), dataList, layoutListManager) {
//
//                @Override
//                public int getItemLayoutId(int viewType) {
//                    return R.layout.item_wallet_coin_list;
//                }
//
//                @Override
//                public void bindData(final BaseRecyclerViewHolder holder, final int position, final WalletToken item) {
//                    holder.setText(R.id.tv_wallet_coin_title, item.getName());
//                    holder.setImageResource(R.id.iv_icon, item.getResIcon());
//                    DecimalFormat df = new DecimalFormat("#,###.##");
//                    holder.setText(R.id.tv_wallet_coin_subtitle, "~" + df.format(item.getEstPriceInIDR()) + " IDR");
//                    holder.setText(R.id.tv_wallet_coin_amount, df.format(item.getAmount()));
//                    holder.setOnClickListener(R.id.rl_wallet_coin_item, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (ClickUtil.isFastDoubleClick()) return;
//                            Intent intent = new Intent(getActivity(), WalletDetailActivity.class);
//                            intent.putExtra("wallet_token", item);
//                            startActivity(intent);
//                        }
//                    });
//
//                }
//            };
//            mAdapter.setHasStableIds(true);
//            mRecycler.setItemAnimator(new DefaultItemAnimator());
//            if (mRecycler.getItemAnimator() != null)
//                mRecycler.getItemAnimator().setAddDuration(250);
//            mRecycler.getItemAnimator().setMoveDuration(250);
//            mRecycler.getItemAnimator().setChangeDuration(250);
//            mRecycler.getItemAnimator().setRemoveDuration(250);
//            mRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
//            mRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
//            mRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
//            mRecycler.setItemViewCacheSize(30);
//            mRecycler.setAdapter(mAdapter);
//            mRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    App.setTextViewStyle(mRecycler);
//                }
//            });
//        } else {
//            mAdapter.setData(dataList);
//        }
//
//        if (dataList.size() <= 0) {
//            mErrorView.setVisibility(View.VISIBLE);
//
//            mRecycler.setVisibility(View.GONE);
//        } else {
//            mErrorView.setVisibility(View.GONE);
//            mRecycler.setVisibility(View.VISIBLE);
//
//        }
//
////        if (dataList.size() <= 0) {
////            mErrorView.setVisibility(View.VISIBLE);
////            mIvError.setImageResource(R.drawable.wallet_empty);
////            mTvErrorHead.setVisibility(View.GONE);
////            mTvErrorBody.setText(getString(R.string.error_wallet_empty_header));
////            mRecycler.setVisibility(View.GONE);
////        }
//    }

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
//        if (mVpObservable != null) {
//            RxBus.get().unregister(RxBus.KEY_VP_RECORD_ADDED, mVpObservable);
//        }
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
//        if (now.get(Calendar.HOUR_OF_DAY) >= 12) {
        nextSnapshoot.add(Calendar.DATE, 1);
        nextSnapshoot.set(Calendar.HOUR_OF_DAY, 0);
//        }
        nextSnapshoot.set(Calendar.MINUTE, 0);
        nextSnapshoot.set(Calendar.SECOND, 0);
        nextSnapshoot.set(Calendar.MILLISECOND, 0);

        long remainTime = nextSnapshoot.getTimeInMillis() - now.getTimeInMillis();

        String time = String.format(Locale.getDefault(), getString(R.string.time_hour_min_sec),
                TimeUnit.MILLISECONDS.toHours(remainTime),
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));

        mTvCountdownVp.setText(time);
    }

    public class VpPagerAdapter extends FragmentStatePagerAdapter {

        VpPagerAdapter(FragmentManager fm) {
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

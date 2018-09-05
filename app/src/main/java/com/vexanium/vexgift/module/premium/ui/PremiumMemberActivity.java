package com.vexanium.vexgift.module.premium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.rd.PageIndicatorView;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.PremiumPlan;
import com.vexanium.vexgift.bean.model.PremiumPurchase;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.UserAddress;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.PremiumDueDateResponse;
import com.vexanium.vexgift.bean.response.PremiumHistoryResponse;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.bean.response.PremiumPurchaseResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.module.premium.presenter.IPremiumPresenter;
import com.vexanium.vexgift.module.premium.presenter.IPremiumPresenterImpl;
import com.vexanium.vexgift.module.premium.ui.adapter.PremiumPlanAdapter;
import com.vexanium.vexgift.module.premium.ui.helper.AdapterBuyOnClick;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.NetworkUtil;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.widget.FixedSpeedScroller;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_premium_member, toolbarTitle = R.string.premium_member, withLoadingAnim = true)
public class PremiumMemberActivity extends BaseActivity<IPremiumPresenter> implements IProfileView, AdapterBuyOnClick {

    public static final int PAGE_COUNT = 4;

    LoopingViewPager mVpPremium;
    PageIndicatorView mPiPremium;
    RecyclerView mRvPremiumPlan;

    ImageButton mHistoryButton;
    RelativeLayout mRlBecomePremiumTopContainer;
    LinearLayout mLlAlreadyPremiumTopContainer;
    TextView mTvAlreadyPremium;

    ArrayList<PremiumPurchase> mPremiumHistoryList = new ArrayList<>();

    PremiumPlanAdapter mAdapter;
    private Subscription timeSubsription;

    User user;

    @Override
    protected void initView() {
        mPresenter = new IPremiumPresenterImpl(this);
        user = User.getCurrentUser(this);

        mHistoryButton = findViewById(R.id.ib_history_premium);
        mHistoryButton.setEnabled(false);
        mHistoryButton.setVisibility(View.GONE);
        mVpPremium = findViewById(R.id.vp_premium_member);
        mPiPremium = findViewById(R.id.pi_premium_member);
        mRvPremiumPlan = findViewById(R.id.rv_premium);
        mRlBecomePremiumTopContainer = findViewById(R.id.rl_premium_top_become_premium);

        mLlAlreadyPremiumTopContainer = findViewById(R.id.ll_premium_top_already_premium);
        mTvAlreadyPremium = findViewById(R.id.tv_already_premium);

        ArrayList<IconText> data = new ArrayList<>();
        data.add(new IconText(R.drawable.ic_premium_voucher, R.string.premium_access_voucher));
        data.add(new IconText(R.drawable.ic_premium_fast, R.string.premium_early_voucher));
        //data.add(new IconText(R.drawable.ic_premium_referral, R.string.premium_referral_bonus));
        data.add(new IconText(R.drawable.ic_premium_luckydraw, R.string.premium_lucky_draw));
        data.add(new IconText(R.drawable.ic_premium_airdrop, R.string.premium_airdrop_token));

        PremiumPagerAdapter adapter = new PremiumPagerAdapter(this, data, true);

        mVpPremium.setAdapter(adapter);
        mVpPremium.setOffscreenPageLimit(PAGE_COUNT);

        mPiPremium.setCount(mVpPremium.getIndicatorCount());

        //Set IndicatorPageChangeListener on LoopingViewPager.
        //When the methods are called, update the Indicator accordingly.
        mVpPremium.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
            @Override
            public void onIndicatorProgress(int selectingPosition, float progress) {
            }

            @Override
            public void onIndicatorPageChange(int newIndicatorPosition) {
                mPiPremium.setSelection(newIndicatorPosition);
            }
        });

        mRvPremiumPlan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new PremiumPlanAdapter(this, this);
        mRvPremiumPlan.setAdapter(mAdapter);

        mHistoryButton.setOnClickListener(this);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mVpPremium.getContext(), null);
            // scroller.setFixedDuration(5000);
            mScroller.set(mVpPremium, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        updateData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ib_history:
                Intent intent = new Intent(this, PremiumHistoryActivity.class);
                intent.putExtra("premium_history_list", mPremiumHistoryList);
                startActivity(intent);
                break;

        }

    }

    @Override
    public void onItemClick(PremiumPlan data) {
        if (!user.isAuthenticatorEnable() || !user.isKycApprove() || User.getUserAddressStatus() != 1) {
            StaticGroup.openRequirementDialog(PremiumMemberActivity.this, true);
        } else {
            if (mPremiumHistoryList == null || mPremiumHistoryList.size() == 0 || mPremiumHistoryList.get(0).getStatus() != 0) {
                doBuy(data);
            } else {
                showPendingWarning();
            }

        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof PremiumListResponse) {
                PremiumListResponse premiumListResponse = (PremiumListResponse) data;
                setPremiumPlanList(premiumListResponse);
            } else if (data instanceof PremiumPurchaseResponse) {
                PremiumPurchaseResponse premiumPurchaseResponse = (PremiumPurchaseResponse) data;
                Intent intent = new Intent(PremiumMemberActivity.this, PremiumHistoryDetailActivity.class);
                intent.putExtra("premium_history_detail", premiumPurchaseResponse.getPremiumPurchase());
                startActivity(intent);
                callPremiumHistoryList();
            } else if (data instanceof PremiumHistoryResponse) {
                mPremiumHistoryList = ((PremiumHistoryResponse) data).getPremiumPurchase();
                Collections.sort(mPremiumHistoryList, new Comparator<PremiumPurchase>() {
                    @Override
                    public int compare(PremiumPurchase t0, PremiumPurchase t1) {
                        return Integer.compare(t1.getId(), t0.getId());
                    }
                });
                mHistoryButton.setVisibility(View.VISIBLE);
                mHistoryButton.setEnabled(true);
            } else if (data instanceof PremiumDueDateResponse) {
                int dueDate = ((PremiumDueDateResponse) data).getPremiumUntil();
                user.setPremiumUntil(dueDate);
                user.setPremiumUntil(dueDate);
                User.updateCurrentUser(PremiumMemberActivity.this, user);

                validatePremiumView(user.getPremiumUntil());
            } else if (data instanceof UserAddressResponse) {
                UserAddress userAddress = ((UserAddressResponse) data).getUserAddress();
                TpUtil tpUtil = new TpUtil(this);
                tpUtil.put(TpUtil.KEY_USER_ADDRESS, JsonUtil.toString(userAddress));

                if (userAddress != null && !userAddress.equals("") && userAddress.getStatus() == 1) {
                    callPremiumHistoryList();
                    callPremiumDueDate();
                    mHistoryButton.setVisibility(View.VISIBLE);
                } else {
                    mHistoryButton.setVisibility(View.GONE);
                }
            }

        } else if (errorResponse != null) {
            if (!errorResponse.getMeta().getMessage().contains("Address not found")) {
                StaticGroup.showCommonErrorDialog(this, errorResponse);
            }
        }
    }

    @Override
    protected void onStart() {
        KLog.v("VexPointActivity", "onStart: ");
        super.onStart();
        startDateTimer();
    }

    @Override
    protected void onPause() {
        mVpPremium.pauseAutoScroll();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
            timeSubsription = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDateTimer();
        mVpPremium.resumeAutoScroll();
        user = User.getCurrentUser(this);
        updateData();
        validatePremiumView(user.getPremiumUntil());
    }


    @Override
    protected void onDestroy() {
        KLog.v("VexPointActivity", "onDestroy: ");
        super.onDestroy();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
        }
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
                            if (!StaticGroup.isScreenOn(PremiumMemberActivity.this, true)) {
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
        TextView mTvCountdownVp = findViewById(R.id.tv_countdown);
        Calendar now = Calendar.getInstance();
        Calendar premiumUntil = Calendar.getInstance();
        premiumUntil.setTimeInMillis(TimeUnit.SECONDS.toMillis(user.getPremiumUntil()));

        long remainTime = premiumUntil.getTimeInMillis() - now.getTimeInMillis();

        String time = String.format(Locale.getDefault(), "%d DAY, %02d HOUR, %02d MIN",
                TimeUnit.MILLISECONDS.toDays(remainTime),
                TimeUnit.MILLISECONDS.toHours(remainTime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(remainTime)),
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)));

        mTvCountdownVp.setText(time);
    }

    private void updateData() {
        if (NetworkUtil.isOnline(this)) {
            if (User.getUserAddressStatus() != 1) {
                callUserActAddress();
            } else {
                callPremiumDueDate();
                callPremiumHistoryList();
            }

            callPremiumPlanList();
        } else {
            findViewById(R.id.tv_premium_plan).setVisibility(View.GONE);
            StaticGroup.showCommonErrorDialog(this, getString(R.string.error_internet_header), getString(R.string.error_internet_body));
        }
    }

    public void validatePremiumView(long premiumDueDate) {
        if (user.isPremiumMember()) {
            updateView(1);
            String ts = getTimeStampDate(premiumDueDate);
            mTvAlreadyPremium.setText(String.format(getString(R.string.premium_already_premium), ts));
            startDateTimer();
        } else {
            updateView(0);
            if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
                timeSubsription.unsubscribe();
                timeSubsription = null;
            }

        }
    }

    public void setPremiumPlanList(PremiumListResponse premiumListResponse) {
        mAdapter.removeAll();
        mAdapter.addItemList(premiumListResponse.getPremiumPlans());
        mAdapter.notifyDataSetChanged();
    }

    private void callUserActAddress() {
        mPresenter.requestGetActAddress(user.getId());
    }

    private void callPremiumDueDate() {
        mPresenter.requestUserPremiumDueDate(user.getId());
    }

    private void callPremiumHistoryList() {
        mPresenter.requestUserPremiumHistory(user.getId());
    }

    private void callPremiumPlanList() {
        mPresenter.requestPremiumList(user.getId());
    }

    private void callPurchasePremium(PremiumPlan plan) {
        mPresenter.purchasePremium(user.getId(), plan.getDuration(), plan.getPrice(), plan.getCurrency());
    }

    private void updateView(int viewType) {
        if (viewType == 0) {
            //if not premium
            mLlAlreadyPremiumTopContainer.setVisibility(View.GONE);
            mRlBecomePremiumTopContainer.setVisibility(View.VISIBLE);
        } else if (viewType == 1) {
            //if already premium
            mRlBecomePremiumTopContainer.setVisibility(View.GONE);
            mLlAlreadyPremiumTopContainer.setVisibility(View.VISIBLE);
        } else {
            //buying premium
            mRlBecomePremiumTopContainer.setVisibility(View.GONE);
            mLlAlreadyPremiumTopContainer.setVisibility(View.GONE);
        }
    }

    private void showPendingWarning() {
        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.premium_already_in_progress_title))
                .content(R.string.premium_already_in_progress_body)
                .positiveText(getString(R.string.premium_already_in_progress_check))
                .negativeText(getString(R.string.premium_already_in_progress_cancel))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(PremiumMemberActivity.this, PremiumHistoryDetailActivity.class);
                        intent.putExtra("premium_history_detail", mPremiumHistoryList.get(0));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    private void doBuy(final PremiumPlan plan) {

        View view = View.inflate(this, R.layout.include_buy_premium_confirmation, null);
        final TextView tvDay = view.findViewById(R.id.tv_premium_confirmation_day);
        final TextView tvVex = view.findViewById(R.id.tv_premium_confirmation_vex);
        final TextView tvTotal = view.findViewById(R.id.tv_premium_confirmation_total_amount);

        int day = plan.getDuration() / 24 / 3600;

        tvDay.setText(plan.getName());
        tvVex.setText(plan.getPrice() / day + " " + getString(R.string.premium_buy_vex));
        tvTotal.setText(plan.getPrice() + " VEX");

        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.premium_buy_confirmation_title))
                .content(R.string.premium_buy_confirmation_body)
                .addCustomView(view)
                .positiveText(getString(R.string.premium_buy_now_button))
                .negativeText(getString(R.string.premium_buy_cancel_button))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        if (User.getUserAddress() != null && !User.getUserAddress().equals("")) {
                            callPurchasePremium(plan);
                        } else {

                        }
                        dialog.dismiss();
                    }
                })
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    public String getTimeStampDate(long timeStamp) {
        long l = TimeUnit.SECONDS.toMillis(timeStamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public class PremiumPagerAdapter extends LoopingPagerAdapter<IconText> {

        public PremiumPagerAdapter(Context context, ArrayList<IconText> itemList, boolean isInfinite) {
            super(context, itemList, isInfinite);
        }

        @Override
        protected View inflateView(int viewType, ViewGroup container, int listPosition) {
            return LayoutInflater.from(context).inflate(R.layout.item_premium_pager, container, false);
        }

        @Override
        protected void bindView(View convertView, int listPosition, int viewType) {
            ImageView icon = convertView.findViewById(R.id.iv_premium);
            TextView description = convertView.findViewById(R.id.tv_premium);

            icon.setImageResource(itemList.get(listPosition).iconId);
            description.setText(context.getText(itemList.get(listPosition).stringId));
        }
    }

    public class IconText {
        int iconId;
        int stringId;

        public IconText(int iconId, int stringId) {
            this.iconId = iconId;
            this.stringId = stringId;
        }

        public int getIconId() {
            return iconId;
        }

        public int getStringId() {
            return stringId;
        }
    }

}

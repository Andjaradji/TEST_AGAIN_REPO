package com.vexanium.vexgift.module.deposit.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.Deposit;
import com.vexanium.vexgift.bean.model.DepositOption;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.UserDeposit;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserDepositResponse;
import com.vexanium.vexgift.bean.response.UserDepositSingleResponse;
import com.vexanium.vexgift.database.TableDepositDaoUtil;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenter;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenterImpl;
import com.vexanium.vexgift.module.deposit.view.IDepositView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_deposit_list, withLoadingAnim = true, toolbarTitle = R.string.deposit_title)
public class DepositListActivity extends BaseActivity<IDepositPresenter> implements IDepositView {

    public static final int STATE_CHOOSE = 1001;
    public static final int STATE_PENDING = 1002;
    public static final int STATE_CONFIRMED = 1003;
    public static final int STATE_REJECTED = 1004;

    FrameLayout mFlFragmentContainer;

    ImageView mIvHeaderIcon;
    TextView mTvHeaderStep, mTvHeaderTitle;
    private Subscription timeSubsription;
    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<DepositOption> mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    ArrayList<DepositOption> depositOptions;
    DepositOption selectedDepositOption;

    RecyclerView mRecyclerview;
    UserDepositSingleResponse userDepositResponse;

    UserDeposit userDeposit;

    User user;

    int state = STATE_CHOOSE;
    public int selectedOption = -1;
    private int depositId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

        mPresenter = new IDepositPresenterImpl(this);
        user = User.getCurrentUser(this);
        mIvHeaderIcon = findViewById(R.id.iv_header_icon);
        mTvHeaderStep = findViewById(R.id.tv_step);
        mTvHeaderTitle = findViewById(R.id.tv_title);

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestUserDepositList(user.getId());
            }
        });

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        mFlFragmentContainer = findViewById(R.id.fl_fragment_container);
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(mFlFragmentContainer.getId(), Deposit1Fragment.newInstance()).commit();

        if (getIntent().hasExtra("user_deposit")) {
            userDeposit = (UserDeposit) JsonUtil.toObject(getIntent().getStringExtra("user_deposit"), UserDeposit.class);
            if (userDeposit != null) {
                if (userDeposit.getStatus() == 0) {
                    state = STATE_PENDING;
                } else if (userDeposit.getStatus() == 1) {
                    state = STATE_CONFIRMED;
                } else {
                    state = STATE_REJECTED;
                }
            }
        } else if (getIntent().hasExtra("deposit")) {
            Deposit deposit = (Deposit) JsonUtil.toObject(getIntent().getStringExtra("deposit"), Deposit.class);
            if (deposit != null) {
                depositOptions = deposit.getDepositOptions();
                depositId = deposit.getId();
                ViewUtil.setText(this, R.id.tv_toolbar_title, deposit.getName());
            }
            if (depositOptions == null) {
                depositOptions = new ArrayList<>();
                mPresenter.requestDepositList(user.getId());
            } else if (depositOptions.size() > 0) {
                selectedOption = depositOptions.get(0).getId();
                selectedDepositOption = depositOptions.get(0);
            }

            setDepositOptionList();
        }

        updateView();

        findViewById(R.id.btn_choose).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_choose:
                if (selectedOption == -1) {
                    new VexDialog.Builder(this)
                            .optionType(DialogOptionType.OK)
                            .title(getString(R.string.deposit_no_choice_title))
                            .content(getString(R.string.deposit_no_choice_desc))
                            .autoDismiss(true)
                            .show();
                } else {
                    String msg = String.format(getString(R.string.deposit_dialog_desc), selectedDepositOption.getAmount() + "");
                    new VexDialog.Builder(this)
                            .optionType(DialogOptionType.YES_NO)
                            .title(getString(R.string.deposit_dialog_title))
                            .content(msg)
                            .positiveText("Yes")
                            .negativeText("Cancel")
                            .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                @Override
                                public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                    if (ClickUtil.isFastDoubleClick()) return;
                                    if (depositId != -1) {
                                        mPresenter.requestDeposit(user.getId(), depositId, selectedOption);
                                    }
                                }
                            })
                            .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                                @Override
                                public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                }
                            })
                            .autoDismiss(true)
                            .show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof DepositListResponse) {
                DepositListResponse depositListResponse = (DepositListResponse) data;

                TableDepositDaoUtil.getInstance().saveDepositsToDb(JsonUtil.toString(depositListResponse));

            } else if (data instanceof UserDepositSingleResponse) {
                userDepositResponse = (UserDepositSingleResponse) data;
                TpUtil tpUtil = new TpUtil(this);
                tpUtil.put(TpUtil.KEY_USER_DEPOSIT, JsonUtil.toString(userDepositResponse));

                userDeposit = userDepositResponse.getUserDeposit();

                if (userDeposit.getStatus() == 0) {
                    state = STATE_PENDING;
                    updateView();
                }
            } else if (data instanceof UserDepositResponse) {
                UserDepositResponse userDepositResponse = (UserDepositResponse) data;
                KLog.json("DepositActivity","HPtes: "+JsonUtil.toString(userDepositResponse));
                TableDepositDaoUtil.getInstance().saveUserDepositsToDb(JsonUtil.toString(userDepositResponse));

                userDeposit = userDepositResponse.findUserDepositById(userDeposit.getId());
                if (userDeposit.getStatus() == 0) {
                    state = STATE_PENDING;
                }else if(userDeposit.getStatus() == 1){
                    state = STATE_CONFIRMED;
                }else if(userDeposit.getStatus() == 2){
                    state = STATE_REJECTED;
                }
                updateView();
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
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
        user = User.getCurrentUser(this);
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
                            if (!StaticGroup.isScreenOn(DepositListActivity.this, true)) {
                                if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
                                    timeSubsription.unsubscribe();
                                }
                            } else {
                                setWatchText();
                                setWatchTextDistribute();
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
        if (userDeposit == null) return;
        TextView mTvCountdownVp = findViewById(R.id.tv_time_left_deposit);
        Calendar now = Calendar.getInstance();
        Calendar premiumUntil = Calendar.getInstance();
//        KLog.v("DepositListActivity","setWatchText: HPtes "+userDeposit.getTransferBefore()+"  "+now.getTimeInMillis());
        premiumUntil.setTimeInMillis(TimeUnit.SECONDS.toMillis(userDeposit.getTransferBefore()));


        long remainTime = premiumUntil.getTimeInMillis() - now.getTimeInMillis();

        String time = String.format(Locale.getDefault(), getString(R.string.time_hour_min_sec),
                TimeUnit.MILLISECONDS.toHours(remainTime),
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));

        mTvCountdownVp.setText(time);
    }

    private void setWatchTextDistribute() {
        if (userDeposit == null) return;

        TextView mTvCountdownVp = findViewById(R.id.tv_time_distribute);
        Calendar now = Calendar.getInstance();
        Calendar premiumUntil = Calendar.getInstance();
        premiumUntil.setTimeInMillis(TimeUnit.SECONDS.toMillis(userDeposit.getTransferBefore()));

        long remainTime = premiumUntil.getTimeInMillis() - now.getTimeInMillis();

        String time = String.format(Locale.getDefault(), getString(R.string.time_day_hour_min),
                TimeUnit.MILLISECONDS.toDays(remainTime),
                TimeUnit.MILLISECONDS.toHours(remainTime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(remainTime)),
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)));

        mTvCountdownVp.setText(time);
    }

    public void setDepositOptionList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<DepositOption>(this, depositOptions, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_deposit_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final DepositOption item) {

                    holder.setText(R.id.tv_deposit_title, item.getName());
                    holder.setText(R.id.tv_deposit_subtitle, String.format(getString(R.string.deposit_option_qty), item.getQuantityLeft(), item.getQuantityAvailable()));

                    holder.setBackgroundRes(R.id.rl_deposit_list_container, item.getId() == selectedOption ? R.drawable.shape_ripple_orange_rounded : R.drawable.shape_ripple_grey_rounded);

                    holder.setTextColor(R.id.tv_deposit_title, getResources().getColor(item.getId() == selectedOption ? R.color.material_black_text_color : R.color.material_black_sub_text_color));
                    holder.setTextColor(R.id.tv_deposit_subtitle, getResources().getColor(item.getId() == selectedOption ? R.color.material_black_text_color : R.color.material_black_sub_text_color));

                    holder.setOnClickListener(R.id.rl_deposit_list_container, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            selectedOption = item.getId();
                            selectedDepositOption = item;
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                }
            };
            mAdapter.setHasStableIds(true);
            mRecyclerview.setLayoutManager(layoutListManager);
            mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this, 16)));
            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
            if (mRecyclerview.getItemAnimator() != null)
                mRecyclerview.getItemAnimator().setAddDuration(250);
            mRecyclerview.getItemAnimator().setMoveDuration(250);
            mRecyclerview.getItemAnimator().setChangeDuration(250);
            mRecyclerview.getItemAnimator().setRemoveDuration(250);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setItemViewCacheSize(30);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    App.setTextViewStyle(mRecyclerview);
                }
            });
        } else {
            mAdapter.setData(depositOptions);
        }

//        if (data.size() <= 0) {
//            mErrorView.setVisibility(View.VISIBLE);
//            mIvError.setImageResource(R.drawable.voucher_empty);
//            mTvErrorHead.setText(getString(R.string.error_voucher_empty_header));
//            mTvErrorBody.setText(getString(R.string.error_my_voucher_empty_body));
//
//            mRecyclerview.setVisibility(View.GONE);
//        } else {
//            mErrorView.setVisibility(View.GONE);
//            mRecyclerview.setVisibility(View.VISIBLE);
//
//        }
    }

    public void updateView() {
        switch (state) {
            case STATE_CHOOSE:
                mIvHeaderIcon.setImageResource(R.drawable.ic_premium_luckydraw);
                mTvHeaderStep.setText("Step 1/3");
                mTvHeaderTitle.setText("Deposit Amount");

                findViewById(R.id.ll_phase_1).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_phase_2).setVisibility(View.GONE);
                findViewById(R.id.ll_phase_3).setVisibility(View.GONE);
                break;
            case STATE_PENDING:
                mIvHeaderIcon.setImageResource(R.drawable.ic_premium_luckydraw);
                mTvHeaderStep.setText("Step 2/3");
                mTvHeaderTitle.setText("Deposit Vex");

                findViewById(R.id.ll_phase_1).setVisibility(View.GONE);
                findViewById(R.id.ll_phase_2).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_phase_3).setVisibility(View.GONE);

                if (userDeposit != null) {
                    try {
                        ViewUtil.setText(this, R.id.tv_vex_count, userDeposit.getDepositOption().getAmount() + " VEX");
                    } catch (Exception e) {
                        ViewUtil.setText(this, R.id.tv_vex_count, selectedDepositOption.getAmount() + " VEX");
                    }
                    try {
                        ViewUtil.setText(this, R.id.tv_address_send_to, userDeposit.getDepositTo());
                    } catch (Exception e) {
                        ViewUtil.setText(this, R.id.tv_address_send_to, "-");
                    }
                }
                break;
            case STATE_CONFIRMED:
            case STATE_REJECTED:
                mIvHeaderIcon.setImageResource(R.drawable.ic_premium_luckydraw);
                mTvHeaderStep.setText("Step 3/3");
                mTvHeaderTitle.setText("Deposit Detail");

                findViewById(R.id.ll_phase_1).setVisibility(View.GONE);
                findViewById(R.id.ll_phase_2).setVisibility(View.GONE);
                findViewById(R.id.ll_phase_3).setVisibility(View.VISIBLE);

                if (userDeposit != null) {
                    try {
                        ViewUtil.setText(this, R.id.tv_vex_count2, userDeposit.getDepositOption().getAmount() + " VEX");
                    } catch (Exception e) {
                        ViewUtil.setText(this, R.id.tv_vex_count2, "-");
                    }
                    try {
                        ViewUtil.setText(this, R.id.tv_vex_distribute, (userDeposit.getDepositOption().getAmount() * (userDeposit.getDepositOption().getCoinBonus() / 100) + " VEX"));
                    } catch (Exception e) {
                        ViewUtil.setText(this, R.id.tv_vex_distribute, "-");
                    }
                    try {
                        ViewUtil.setText(this, R.id.tv_transaction, userDeposit.getDepositTxId());
                    } catch (Exception e) {
                        ViewUtil.setText(this, R.id.tv_transaction, "-");
                    }

                    findViewById(R.id.ll_rejected).setVisibility(userDeposit.getStatus() == 1 ? View.GONE : View.VISIBLE);
                    findViewById(R.id.ll_confirmed).setVisibility(userDeposit.getStatus() == 2 ? View.GONE : View.VISIBLE);
                    if (userDeposit.getStatus() == 2) {
                        findViewById(R.id.ll_confirmed_view).setVisibility(View.GONE);
                    }
                }

                break;
        }
    }
}

package com.vexanium.vexgift.module.tokensale.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.UserDeposit;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserDepositResponse;
import com.vexanium.vexgift.database.TableDepositDaoUtil;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenter;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenterImpl;
import com.vexanium.vexgift.module.deposit.ui.DepositListActivity;
import com.vexanium.vexgift.module.deposit.view.IDepositView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_deposit_history, toolbarTitle = R.string.token_sale_history_title, withLoadingAnim = true)
public class TokenSaleHistoryActivity extends BaseActivity<IDepositPresenter> implements IDepositView {

    UserDepositResponse userDepositResponse;
    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<UserDeposit> mAdapter;
    RecyclerView mRecyclerview;
    private ArrayList<UserDeposit> userDeposits;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IDepositPresenterImpl(this);

        userDepositResponse = TableDepositDaoUtil.getInstance().getUserDepositListResponse();
        if (userDepositResponse != null) {
            KLog.v("DepositHistoryActivity", "initView: HPtes tidak kosong");

            userDeposits = userDepositResponse.getUserDeposits();
        } else {
            KLog.v("DepositHistoryActivity", "initView: HPtes kosong");
        }
        if (userDeposits == null) {
            userDeposits = new ArrayList<>();
        }

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestUserDepositList(user.getId());
            }
        });

        mPresenter.requestUserDepositList(user.getId());

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        setDepositHistoryList();

        if (getIntent().hasExtra("id")) {
            int id = getIntent().getIntExtra("id", 0);
            if (id > 0) {
                if (userDepositResponse != null) {
                    UserDeposit ud = userDepositResponse.findUserDepositById(id);
                    if (ud != null) {
                        Intent intent = new Intent(this, DepositListActivity.class);
                        intent.putExtra("user_deposit", JsonUtil.toString(ud));
                        startActivity(intent);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.requestUserDepositList(user.getId());

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof UserDepositResponse) {
                UserDepositResponse userDepositResponse = (UserDepositResponse) data;
                TableDepositDaoUtil.getInstance().saveUserDepositsToDb(JsonUtil.toString(userDepositResponse));

                userDeposits = userDepositResponse.getUserDeposits();
                setDepositHistoryList();
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    public void setDepositHistoryList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<UserDeposit>(this, userDeposits, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_deposit_history;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final UserDeposit item) {

                    if (item.getDeposit() != null) {
                        holder.setText(R.id.tv_deposit_history_title, item.getDeposit().getName());
                    } else {
                        holder.setText(R.id.tv_deposit_history_title, "-");
                    }

                    if (item.getDepositOption() != null) {
                        holder.setText(R.id.tv_deposit_history_amount, item.getDepositOption().getAmount() + " VEX");
                    } else {
                        holder.setText(R.id.tv_deposit_history_amount, "-");
                    }

                    holder.setText(R.id.tv_deposit_history_subtitle, item.getCreatedAtDate());
                    if (item.getStatus() == 0) {
                        holder.setTextColor(R.id.tv_deposit_history_status, getResources().getColor(R.color.material_black_text_color));
                        holder.setText(R.id.tv_deposit_history_status, getText(R.string.premium_purchase_pending));
                    } else if (item.getStatus() == 1) {
                        holder.setTextColor(R.id.tv_deposit_history_status, getResources().getColor(R.color.vexpoint_plus));
                        holder.setText(R.id.tv_deposit_history_status, getText(R.string.premium_purchase_success));
                    } else {
                        holder.setTextColor(R.id.tv_deposit_history_status, getResources().getColor(R.color.vexpoint_minus));
                        holder.setText(R.id.tv_deposit_history_status, getText(R.string.premium_purchase_failed));
                    }

                    holder.setOnClickListener(R.id.rl_deposit_history_item, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            Intent intent = new Intent(TokenSaleHistoryActivity.this, DepositListActivity.class);
                            intent.putExtra("user_deposit", JsonUtil.toString(item));
                            startActivity(intent);
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
            mAdapter.setData(userDeposits);
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
}

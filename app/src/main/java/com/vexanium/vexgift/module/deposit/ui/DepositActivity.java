package com.vexanium.vexgift.module.deposit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserDepositResponse;
import com.vexanium.vexgift.database.TableDepositDaoUtil;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenter;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenterImpl;
import com.vexanium.vexgift.module.deposit.view.IDepositView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_deposit, withLoadingAnim = true)
public class DepositActivity extends BaseActivity<IDepositPresenter> implements IDepositView {

    LinearLayout mBtnDeposit;

    User user;
    BaseRecyclerAdapter<Deposit> mAdapter;
    ArrayList<Deposit> deposits;
    GridLayoutManager layoutListManager;
    RecyclerView mRecyclerview;
    private SwipeRefreshLayout mRefreshLayout;
    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IDepositPresenterImpl(this);
        user = User.getCurrentUser(this);

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);
        
        mBtnDeposit = findViewById(R.id.btn_deposit);

        DepositListResponse depositListResponse = TableDepositDaoUtil.getInstance().getDepositListResponse();
        if (depositListResponse != null) {
            deposits = depositListResponse.getDeposits();
        }

        if (deposits == null) {
            deposits = new ArrayList<>();
        }

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestDepositList(user.getId());
            }
        });

        mPresenter.requestDepositList(user.getId());
        mPresenter.requestUserDepositList(user.getId());

        if(getIntent().hasExtra("id")){
            int id = getIntent().getIntExtra("id",0);
            if(id > 0){
                Intent intent = new Intent(this, DepositHistoryActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        }

        ViewUtil.setOnClickListener(this, this, R.id.back_button, R.id.history_button);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.history_button:
                Intent intent = new Intent(DepositActivity.this, DepositHistoryActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof DepositListResponse) {
                DepositListResponse depositListResponse = (DepositListResponse) data;
                deposits = depositListResponse.getDeposits();

                TableDepositDaoUtil.getInstance().saveDepositsToDb(JsonUtil.toString(depositListResponse));

                setDepositPlanList();

            } else if (data instanceof UserDepositResponse) {
                UserDepositResponse userDepositResponse = (UserDepositResponse) data;
                KLog.json("DepositActivity", "HPtes: " + JsonUtil.toString(userDepositResponse));
                TableDepositDaoUtil.getInstance().saveUserDepositsToDb(JsonUtil.toString(userDepositResponse));
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    public void setDepositPlanList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<Deposit>(this, deposits, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_deposit_program;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final Deposit item) {

                    holder.setText(R.id.tv_title, item.getName());
                    holder.setText(R.id.tv_desc, item.getDescription());
                    String time = String.format("%s - %s", StaticGroup.getDate(item.getStartTime()), StaticGroup.getDate(item.getEndTime()));
                    holder.setText(R.id.tv_deposit_time, time );
                    holder.setText(R.id.tv_start, StaticGroup.getDate(item.getStartTime()));
                    holder.setText(R.id.tv_end, StaticGroup.getDate(item.getEndTime()));
                    holder.setText(R.id.tv_coin_deposited, StaticGroup.convertVpFormat(item.getCoinDeposited(),true) + "");
                    holder.setText(R.id.tv_max_coin, StaticGroup.convertVpFormat(item.getMaxCoin(),true) + "");
                    holder.setAlpha(R.id.root_view, item.isAvailable() ? 1f: 0.7f);

                    App.setTextViewStyle((ViewGroup) holder.getView(R.id.root_view));

                    holder.setOnClickListener(R.id.btn_deposit, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            if(item.isAvailable()) {
                                Intent intent = new Intent(DepositActivity.this, DepositListActivity.class);
                                intent.putExtra("deposit", JsonUtil.toString(item));
                                startActivity(intent);
                            }else {
                                new VexDialog.Builder(DepositActivity.this)
                                        .optionType(DialogOptionType.OK)
                                        .title(getString(R.string.deposit_not_available))
                                        .content(getString(R.string.deposit_not_available_desc))
                                        .autoDismiss(true)
                                        .show();
                            }
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

        } else {
            mAdapter.setData(deposits);
        }

        if (deposits.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.error_deposit_empty_header));
            mTvErrorBody.setText(getString(R.string.error_my_deposit_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerview.setVisibility(View.VISIBLE);

        }
    }
}

package com.vexanium.vexgift.module.buyback.ui;

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

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.Buyback;
import com.vexanium.vexgift.bean.model.BuybackOption;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.BuybackResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.buyback.presenter.IBuybackPresenter;
import com.vexanium.vexgift.module.buyback.presenter.IBuybackPresenterImpl;
import com.vexanium.vexgift.module.buyback.view.IBuybackView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_token_sale, withLoadingAnim = true)
public class BuybackActivity extends BaseActivity<IBuybackPresenter> implements IBuybackView {

    User user;
    BuybackResponse buybackResponse;
    BaseRecyclerAdapter<Buyback> mAdapter;
    ArrayList<Buyback> buybacks;
    GridLayoutManager layoutListManager;
    RecyclerView mRecyclerview;
    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IBuybackPresenterImpl(this);
        user = User.getCurrentUser(this);

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);

//        DepositListResponse depositListResponse = TableDepositDaoUtil.getInstance().getDepositListResponse();
//        if (depositListResponse != null) {
//            tokenSales = depositListResponse.getDeposits();
//        }

        if (buybacks == null) {
            buybacks = new ArrayList<>();
        }

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestBuybackList(user.getId());
            }
        });

        mPresenter.requestBuybackList(user.getId());

        if (getIntent().hasExtra("id")) {
            int id = getIntent().getIntExtra("id", 0);
            if (id > 0) {
                Intent intent = new Intent(this, BuybackHistoryActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }

        ViewUtil.setOnClickListener(this, this, R.id.back_button, R.id.history_button);

        //TODO comment if ready to launch
        //findViewById(R.id.history_button).setVisibility(View.GONE);
        mRefreshLayout.setEnabled(false);

        //TODO uncomment if ready to launch
        findViewById(R.id.iv_coming_soon).setVisibility(View.GONE);
        findViewById(R.id.iv_toolbar_logo).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_toolbar_title)).setText("BUYBACK");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.history_button:
                Intent intent = new Intent(BuybackActivity.this, BuybackHistoryActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof BuybackResponse) {
                buybackResponse = (BuybackResponse) data;
                buybacks = buybackResponse.getBuybacks();

                setBuybackList();

            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    public void setBuybackList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<Buyback>(this, buybacks, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_buyback_program;
                }

                @Override
                public void bindData(BaseRecyclerViewHolder holder, int position, Buyback item) {
                    holder.setText(R.id.tv_title, item.getName());
                    holder.setText(R.id.tv_desc, item.getDescription());
                    String time = String.format("%s - %s", item.getTimeStampDate(item.getStartTime()), item.getTimeStampDate(item.getEndTime()));
                    holder.setText(R.id.tv_sale_time, time);
                    holder.setText(R.id.tv_token_type, item.getCoinName() + " (" + item.getCoinType() + ")");
                    String left = String.format("%.03f", item.getCoinBought());
                    String available = String.format("%.03f", item.getTotalCoin());
                    holder.setText(R.id.tv_token_left, left);
                    holder.setText(R.id.tv_token_total, available);

                    setBuybackPaymentOptionList(holder, position, item);

                    App.setTextViewStyle((ViewGroup) holder.getView(R.id.root_view));

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
            mAdapter.setData(buybacks);
        }

        if (buybacks.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.error_buyback_empty_header));
            mTvErrorBody.setText(getString(R.string.error_my_buyback_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerview.setVisibility(View.VISIBLE);

        }
    }

    public void setBuybackPaymentOptionList(BaseRecyclerViewHolder holder, final int tokenPosition, final Buyback token) {
        final LinearLayout btnPaymentOptions = (LinearLayout) holder.getView(R.id.btn_payment_options);
        final RecyclerView recyclerView = holder.getRecyclerView(R.id.rv_payment_options);
        if (recyclerView != null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(gridLayoutManager);

            holder.getRecyclerView(R.id.rv_payment_options).setAdapter(new BaseRecyclerAdapter<BuybackOption>(this, token.getBuybackOptions(), gridLayoutManager) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_buyback_payment_options;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, final int position, BuybackOption item) {
                    if (item.getCoinName() != null) {
                        String title = String.format("Buyback with %s", item.getCoinName());
                        holder.setText(R.id.tv_payment_title, title);
                    } else {
                        holder.setText(R.id.tv_payment_title, "error");
                        holder.getView(R.id.btn_buy).setEnabled(false);
                    }

                    if (item.getPrice() > 0) {
                        String coin = String.format("%.010f", item.getPrice());
                        String body = String.format("1 %s = %s", "VEX", coin + " " + item.getCoinName());
                        holder.setText(R.id.tv_payment_body, body);
                    } else {
                        holder.setText(R.id.tv_payment_body, "error");
                        holder.getView(R.id.btn_buy).setEnabled(false);
                    }

                    holder.setText(R.id.tv_slot_total, ""+item.getLimitSellPerUser());

//                    holder.setText(R.id.tv_slot_left, item.get)

                    App.setTextViewStyle((ViewGroup) holder.getView(R.id.root_view));

                    holder.getView(R.id.btn_buy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(BuybackActivity.this, BuybackBuyActivity.class);
                            intent.putExtra("buyback", JsonUtil.toString(buybackResponse));
                            intent.putExtra("buyback_position", tokenPosition);
                            intent.putExtra("option_position", holder.getAdapterPosition());
                            startActivity(intent);
                        }
                    });
                }


            });

            btnPaymentOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerView.getVisibility() == View.GONE) {
                        btnPaymentOptions.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_payment_options).setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            btnPaymentOptions.setVisibility(View.GONE);
        }
    }
}

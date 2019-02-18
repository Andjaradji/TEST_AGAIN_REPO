package com.vexanium.vexgift.module.buyback.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.BuybackHistory;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.BuybackHistoryResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.buyback.presenter.IBuybackPresenter;
import com.vexanium.vexgift.module.buyback.presenter.IBuybackPresenterImpl;
import com.vexanium.vexgift.module.buyback.view.IBuybackView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_token_sale_history, toolbarTitle = R.string.buyback_history_title, withLoadingAnim = true)
public class BuybackHistoryActivity extends BaseActivity<IBuybackPresenter> implements IBuybackView {

    BuybackHistoryResponse buybackHistoryResponse;
    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<BuybackHistory> mAdapter;
    RecyclerView mRecyclerview;
    private ArrayList<BuybackHistory> buybackHistories;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IBuybackPresenterImpl(this);

        buybackHistories = new ArrayList<>();

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestBuybackHistoryList(user.getId());
            }
        });

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        setBuybackHistoryList();

        if (getIntent().hasExtra("id")) {
            int id = getIntent().getIntExtra("id", 0);
            if (id > 0) {
                //do something
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.requestBuybackHistoryList(user.getId());

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof BuybackHistoryResponse) {
                buybackHistoryResponse = (BuybackHistoryResponse) data;

                buybackHistories = buybackHistoryResponse.getBuybackHistories();
                setBuybackHistoryList();
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    public void setBuybackHistoryList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<BuybackHistory>(this, buybackHistories, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_token_sale_history;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, final int position, final BuybackHistory item) {


                    holder.setText(R.id.tv_token_sale_history_title, "BUYBACK #" + item.getId());

                    if (item.getBuybackOption() != null && item.getBuybackOption() != null) {

                        holder.setText(R.id.tv_token_sale_history_amount, item.getAmount() * item.getBuybackOption().getPrice() + " " + item.getBuybackOption().getCoinName());
                    } else {
                        holder.setText(R.id.tv_token_sale_history_amount, item.getAmount() + "");
                    }
                    holder.setText(R.id.tv_token_sale_history_subtitle, item.getCreatedAtDate());

                    if (item.getStatus() == 0) {
                        holder.setTextColor(R.id.tv_token_sale_history_status, getResources().getColor(R.color.material_black_text_color));
                        holder.setText(R.id.tv_token_sale_history_status, getText(R.string.premium_purchase_pending));
                    } else if (item.getStatus() == 1) {
                        holder.setTextColor(R.id.tv_token_sale_history_status, getResources().getColor(R.color.vexpoint_plus));
                        holder.setText(R.id.tv_token_sale_history_status, getText(R.string.premium_purchase_success));
                        if (item.getDistributionAddress() == null) {
                            holder.setViewGone(R.id.iv_red_dot, false);
                        } else {
                            holder.setViewGone(R.id.iv_red_dot, true);
                        }
                    } else {
                        holder.setTextColor(R.id.tv_token_sale_history_status, getResources().getColor(R.color.vexpoint_minus));
                        holder.setText(R.id.tv_token_sale_history_status, getText(R.string.premium_purchase_failed));
                    }

                    App.setTextViewStyle((ViewGroup) holder.getView(R.id.rl_token_sale_history_item));


                    holder.setOnClickListener(R.id.rl_token_sale_history_item, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            Intent intent = new Intent(BuybackHistoryActivity.this, BuybackHistoryDetailActivity.class);
                            intent.putExtra("buyback_history_detail", JsonUtil.toString(buybackHistoryResponse));
                            intent.putExtra("position", holder.getAdapterPosition());
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
            mAdapter.setData(buybackHistories);
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

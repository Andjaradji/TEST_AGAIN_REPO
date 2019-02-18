package com.vexanium.vexgift.module.exchanger.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.Exchange;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.ExchangeResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.exchanger.presenter.IExchangePresenter;
import com.vexanium.vexgift.module.exchanger.presenter.IExchangePresenterImpl;
import com.vexanium.vexgift.module.exchanger.view.IExchangeView;
import com.vexanium.vexgift.util.MeasureUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@ActivityFragmentInject(contentViewId = R.layout.activity_buy_vex, toolbarTitle = R.string.buy_vex, withLoadingAnim = false)
public class BuyVexActivity extends BaseActivity<IExchangePresenter> implements IExchangeView {

    RecyclerView mRecyclerview;
    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<Exchange> mAdapter;
    ArrayList<Exchange> dataList;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        mPresenter = new IExchangePresenterImpl(this);
        user = User.getCurrentUser(this);

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setEnabled(false);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mPresenter.requestTokenSaleHistoryList(user.getId());
            }
        });

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mPresenter.requestExchangeList(user.getId());

    }

    public void setVexExchangeList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<Exchange>(this, dataList, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_vex_exchanger;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, final int position, final Exchange item) {
                    holder.setText(R.id.tv_vex_exchanger_title, item.getName());
                    holder.setImageUrl(R.id.iv_exchange_logo, item.getLogo(), R.drawable.placeholder);
                    holder.setOnClickListener(R.id.rl_root, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            StaticGroup.openAndroidBrowser(BuyVexActivity.this, item.getLink());
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
            mAdapter.setData(dataList);
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

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof ExchangeResponse) {

                dataList = new ArrayList<>(((ExchangeResponse) data).getExchanges());

                Collections.sort(dataList, new Comparator<Exchange>() {
                    @Override
                    public int compare(Exchange exchange, Exchange t1) {
                        return Integer.compare(exchange.getPriority(), t1.getPriority());
                    }
                });
                setVexExchangeList();
            }
        }

    }
}

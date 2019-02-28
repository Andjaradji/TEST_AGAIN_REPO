package com.vexanium.vexgift.module.wallet.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.vexanium.vexgift.bean.model.TokenSaleHistory;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.WalletWithdrawal;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.TokenSaleHistoryResponse;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.tokensale.presenter.ITokenSalePresenterImpl;
import com.vexanium.vexgift.module.tokensale.ui.TokenSaleHistoryActivity;
import com.vexanium.vexgift.module.tokensale.ui.TokenSaleHistoryDetailActivity;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenterImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_wallet_withdraw_history, toolbarTitle = R.string.wallet_withdraw_history, withLoadingAnim = true)
public class WalletWithdrawHistoryActivity extends BaseActivity<IWalletPresenter> implements IWalletView {

    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<WalletWithdrawal> mAdapter;
    RecyclerView mRecyclerview;
    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    private ArrayList<WalletWithdrawal> walletWithdrawals;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IWalletPresenterImpl(this);

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);
        
        walletWithdrawals = new ArrayList<>();

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestGetWallet(user.getId());
            }
        });

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        setTokenSaleHistoryList();

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
        mPresenter.requestGetWallet(user.getId());

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof WalletResponse) {
                WalletResponse walletResponse = (WalletResponse) data;
                if (walletResponse != null) {
                    if (walletResponse.getWallet() != null) {
                        TableContentDaoUtil.getInstance().saveWalletsToDb(JsonUtil.toString(walletResponse));
                    }
                }

            }

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    public void setTokenSaleHistoryList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<WalletWithdrawal>(this, walletWithdrawals, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_withdraw_history;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, final int position, final WalletWithdrawal item) {


                    holder.setText(R.id.tv_token_sale_history_title, "WITHDRAW #" + item.getId());


                    holder.setText(R.id.tv_token_sale_history_subtitle, item.getCreatedAtDate());

                    if (item.getStatus().equalsIgnoreCase("pending")) {
                        holder.setTextColor(R.id.tv_withdraw_history_status, getResources().getColor(R.color.material_black_text_color));
                        holder.setText(R.id.tv_withdraw_history_status, getText(R.string.premium_purchase_pending));
                    } else if (item.getStatus().equalsIgnoreCase("success")) {
                        holder.setTextColor(R.id.tv_withdraw_history_status, getResources().getColor(R.color.vexpoint_plus));
                        holder.setText(R.id.tv_withdraw_history_status, getText(R.string.premium_purchase_success));

                    } else {
                        holder.setTextColor(R.id.tv_withdraw_history_status, getResources().getColor(R.color.vexpoint_minus));
                        holder.setText(R.id.tv_withdraw_history_status, getText(R.string.premium_purchase_failed));
                    }

                    App.setTextViewStyle((ViewGroup) holder.getView(R.id.rl_withdraw_history_item));

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
            mAdapter.setData(walletWithdrawals);
        }

        if (walletWithdrawals.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.wallet_withdraw_empty_header));
            mTvErrorBody.setText(getString(R.string.wallet_withdraw_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerview.setVisibility(View.VISIBLE);

        }
    }
}

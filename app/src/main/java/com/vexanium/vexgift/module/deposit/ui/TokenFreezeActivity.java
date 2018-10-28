package com.vexanium.vexgift.module.deposit.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
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
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.VexVault;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.VexVaultResponse;
import com.vexanium.vexgift.database.TableDepositDaoUtil;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenter;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenterImpl;
import com.vexanium.vexgift.module.deposit.view.IDepositView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_token_freeze, withLoadingAnim = true, toolbarTitle = R.string.token_freeze_title)
public class TokenFreezeActivity extends BaseActivity<IDepositPresenter> implements IDepositView {

    User user;
    BaseRecyclerAdapter<VexVault> mAdapter;
    ArrayList<VexVault> vexVaults;
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
        user = User.getCurrentUser(this);
        mPresenter = new IDepositPresenterImpl(this);

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);

        VexVaultResponse vexVaultResponse = TableDepositDaoUtil.getInstance().getVexVaultListResponse();
        if (vexVaultResponse != null) {
            ViewUtil.setText(TokenFreezeActivity.this, R.id.tv_total_frozen, vexVaultResponse.getTotalFrozen()+" VEX");
            vexVaults = vexVaultResponse.getVexVaults();
        }

        if (vexVaults == null) {
            vexVaults = new ArrayList<>();
        }

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestDepositList(user.getId());
            }
        });

        mPresenter.requestTokenFreeze(user.getId());
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            default:
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof VexVaultResponse) {
                VexVaultResponse vexVaultResponse = (VexVaultResponse) data;
                vexVaults = vexVaultResponse.getVexVaults();

                TableDepositDaoUtil.getInstance().saveTokenFreezeToDb(JsonUtil.toString(vexVaultResponse));
                ViewUtil.setText(TokenFreezeActivity.this, R.id.tv_total_frozen, vexVaultResponse.getTotalFrozen()+" VEX");

                setVexVaults();

            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    public void setVexVaults() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<VexVault>(this, vexVaults, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_token_freeze_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final VexVault item) {

                    holder.setText(R.id.tv_title, item.getTitle());
                    holder.setText(R.id.tv_desc, item.getDescription());
                    holder.setText(R.id.tv_coin_amount, item.getCoinAmount() + " "+item.getCoinName());

                    App.setTextViewStyle((ViewGroup) holder.getView(R.id.rl_root));
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
            mAdapter.setData(vexVaults);
        }

        if (vexVaults.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.error_voucher_empty_header));
            mTvErrorBody.setText(getString(R.string.error_my_voucher_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerview.setVisibility(View.VISIBLE);

        }
    }
}

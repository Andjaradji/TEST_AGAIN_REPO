package com.vexanium.vexgift.module.tokensale.ui;

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
import com.vexanium.vexgift.bean.model.TokenSale;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.TokenSaleResponse;
import com.vexanium.vexgift.bean.response.UserDepositResponse;
import com.vexanium.vexgift.database.TableDepositDaoUtil;
import com.vexanium.vexgift.module.deposit.ui.DepositListActivity;
import com.vexanium.vexgift.module.tokensale.presenter.ITokenSalePresenter;
import com.vexanium.vexgift.module.tokensale.presenter.ITokenSalePresenterImpl;
import com.vexanium.vexgift.module.tokensale.view.ITokenSaleView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_deposit, withLoadingAnim = true)
public class TokenSaleActivity extends BaseActivity<ITokenSalePresenter> implements ITokenSaleView {

    LinearLayout mBtnDeposit;

    User user;
    BaseRecyclerAdapter<TokenSale> mAdapter;
    ArrayList<TokenSale> tokenSales;
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
        mPresenter = new ITokenSalePresenterImpl(this);
        user = User.getCurrentUser(this);

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);
        
        mBtnDeposit = findViewById(R.id.btn_deposit);

//        DepositListResponse depositListResponse = TableDepositDaoUtil.getInstance().getDepositListResponse();
//        if (depositListResponse != null) {
//            tokenSales = depositListResponse.getDeposits();
//        }

        if (tokenSales == null) {
            tokenSales = new ArrayList<>();
        }

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mPresenter.requestDepositList(user.getId());
                mPresenter.requestTokenSaleList(user.getId());
            }
        });

        mPresenter.requestTokenSaleList(user.getId());

        if(getIntent().hasExtra("id")){
            int id = getIntent().getIntExtra("id",0);
            if(id > 0){
//                Intent intent = new Intent(this, DepositHistoryActivity.class);
//                intent.putExtra("id",id);
//                startActivity(intent);
            }
        }

        ViewUtil.setText(this, R.id.tv_toolbar_title, getString(R.string.token_sale_title));
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
                //Intent intent = new Intent(TokenSaleActivity.this, DepositHistoryActivity.class);
                //startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof TokenSaleResponse) {
                TokenSaleResponse tokenSaleResponse = (TokenSaleResponse) data;
                tokenSales = tokenSaleResponse.getTokenSales();

                //TableDepositDaoUtil.getInstance().saveDepositsToDb(JsonUtil.toString(depositListResponse));

                setTokenSaleList();

            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    public void setTokenSaleList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<TokenSale>(this, tokenSales, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_token_sale_program;
                }

                @Override
                public void bindData(BaseRecyclerViewHolder holder, int position, TokenSale item) {
                    holder.setText(R.id.tv_title, item.getTitle());
                    holder.setText(R.id.tv_desc, item.getDescription());
                    String time = String.format("%s - %s", item.getTimeStampDate(item.getStartTime()), item.getTimeStampDate(item.getEndTime()));
                    holder.setText(R.id.tv_sale_time,time);
                    holder.setText(R.id.tv_token_type,item.getTokenName()+ " ("+ item.getTokenType()+")");
                    holder.setText(R.id.tv_token_left,item.getTokenLeft()+"");
                    holder.setText(R.id.tv_token_total,item.getTokenAvailable()+"");

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
            mAdapter.setData(tokenSales);
        }

        if (tokenSales.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.error_token_sale_empty_header));
            mTvErrorBody.setText(getString(R.string.error_my_token_sale_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerview.setVisibility(View.VISIBLE);

        }
    }
}

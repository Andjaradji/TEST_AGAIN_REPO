package com.vexanium.vexgift.module.wallet.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.fixture.Exchanger;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.fixture.WalletToken;
import com.vexanium.vexgift.bean.model.WalletRecord;
import com.vexanium.vexgift.module.tokensale.ui.TokenSaleHistoryActivity;
import com.vexanium.vexgift.module.tokensale.ui.TokenSaleHistoryDetailActivity;
import com.vexanium.vexgift.module.wallet.ui.adapter.WalletAdapter;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.fragment_wallet)
public class WalletFragment extends BaseFragment {

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody, mTotalAsset;
    GridLayoutManager layoutListManager;
    private RecyclerView mRecycler;
    private BaseRecyclerAdapter<WalletToken> mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        ViewUtil.setText(fragmentRootView, R.id.tv_toolbar_title, "MY WALLET");

        mRecycler = fragmentRootView.findViewById(R.id.rv_wallet_coin);
        mTotalAsset = fragmentRootView.findViewById(R.id.tv_total_asset);

        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        ImageView mIvComingSoon = fragmentRootView.findViewById(R.id.iv_coming_soon);

        mRefreshLayout = fragmentRootView.findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecordlist(FixtureData.tokenList);
            }
        });

        //Coming soon view
        mIvComingSoon.setVisibility(View.GONE);
        boolean isDev = false;
        mIvComingSoon.setVisibility(isDev ? View.GONE : View.VISIBLE);
//        mMainContainer.setVisibility(isDev ? View.VISIBLE : View.GONE);
//        mButtonContainer.setVisibility(isDev ? View.VISIBLE : View.GONE);
//        mRecordContainer.setVisibility(isDev ? View.VISIBLE : View.GONE);

        if (mIvComingSoon.getVisibility() != View.VISIBLE) {
            layoutListManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
            mRecycler.setLayoutManager(layoutListManager);
            setRecordlist(FixtureData.tokenList);

        }

        App.setTextViewStyle((ViewGroup) fragmentRootView);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Wallet Fragment View")
                .putContentType("Wallet")
                .putContentId("wallet"));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("NotifFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setRecordlist(ArrayList<WalletToken> dataList) {
        mRefreshLayout.setRefreshing(false);
        float totalAsset = 0f;
        for(WalletToken token : FixtureData.tokenList){
            totalAsset += token.getAmount()*token.getEstPriceInIDR();
        }

        DecimalFormat df = new DecimalFormat("#,###.##");

        mTotalAsset.setText(df.format(totalAsset));

        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<WalletToken>(getActivity(), dataList, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_wallet_coin_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, final int position, final WalletToken item) {
                    holder.setText(R.id.tv_wallet_coin_title, item.getName());
                    holder.setImageResource(R.id.iv_icon, item.getResIcon());
                    DecimalFormat df = new DecimalFormat("#,###.##");
                    holder.setText(R.id.tv_wallet_coin_subtitle, "~"+df.format(item.getEstPriceInIDR())+" IDR");
                    holder.setText(R.id.tv_wallet_coin_amount, df.format(item.getAmount()));
                    holder.setOnClickListener(R.id.rl_wallet_coin_item, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            Intent intent = new Intent(getActivity(), WalletDetailActivity.class);
                            intent.putExtra("wallet_token", item);
                            startActivity(intent);
                        }
                    });

                }
            };
            mAdapter.setHasStableIds(true);
            mRecycler.setItemAnimator(new DefaultItemAnimator());
            if (mRecycler.getItemAnimator() != null)
                mRecycler.getItemAnimator().setAddDuration(250);
            mRecycler.getItemAnimator().setMoveDuration(250);
            mRecycler.getItemAnimator().setChangeDuration(250);
            mRecycler.getItemAnimator().setRemoveDuration(250);
            mRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecycler.setItemViewCacheSize(30);
            mRecycler.setAdapter(mAdapter);
            mRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    App.setTextViewStyle(mRecycler);
                }
            });
        } else {
            mAdapter.setData(dataList);
        }

        if (dataList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.error_token_sale_empty_header));
            mTvErrorBody.setText(getString(R.string.error_my_token_sale_empty_body));

            mRecycler.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);

        }

//        if (dataList.size() <= 0) {
//            mErrorView.setVisibility(View.VISIBLE);
//            mIvError.setImageResource(R.drawable.wallet_empty);
//            mTvErrorHead.setVisibility(View.GONE);
//            mTvErrorBody.setText(getString(R.string.error_wallet_empty_header));
//            mRecycler.setVisibility(View.GONE);
//        }
    }

}

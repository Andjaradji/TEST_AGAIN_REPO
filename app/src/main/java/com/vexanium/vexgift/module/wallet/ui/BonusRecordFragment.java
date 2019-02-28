package com.vexanium.vexgift.module.wallet.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
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
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.Wallet;
import com.vexanium.vexgift.bean.model.WalletBonus;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.vexpoint.ui.adapter.VexPointAdapter;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.RxBus;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.fragment_vexpoint)
public class BonusRecordFragment extends BaseFragment {

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    ArrayList<WalletBonus> dataList = new ArrayList<>();
    WalletResponse walletResponse;
    private BaseRecyclerAdapter<WalletBonus> mBonusLogAdapter;
    private RecyclerView mRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private VexPointAdapter mAdapter;
    private Observable<Boolean> mBonusObservable;

    public static BonusRecordFragment newInstance() {
        return new BonusRecordFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        mRecyclerview = fragmentRootView.findViewById(R.id.rv_vexpoint);

        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mBonusObservable = RxBus.get().register(RxBus.KEY_WALLET_TRANSACTION_RECORD_ADDED, Boolean.class);
        mBonusObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean b) {
                getDataFromDb();
            }
        });

        getDataFromDb();

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("PointRecordFragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBonusObservable != null) {
            RxBus.get().unregister(RxBus.KEY_WALLET_TRANSACTION_RECORD_ADDED, mBonusObservable);
        }
    }

    public void getDataFromDb() {
        walletResponse = TableContentDaoUtil.getInstance().getWallet();
        if (walletResponse != null && walletResponse.getWallet() != null) {
            Wallet wallet = walletResponse.getWallet();
            if (wallet.getWalletBonuses() != null) {
                dataList = wallet.getWalletBonuses();
                initTransactionLog();
            }
        }
    }

    public void initTransactionLog() {
        if (mBonusLogAdapter == null) {
            mBonusLogAdapter = new BaseRecyclerAdapter<WalletBonus>(this.getActivity(), dataList, linearLayoutManager) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_bonus_log;
                }

                @Override
                public void bindData(BaseRecyclerViewHolder holder, int position, final WalletBonus item) {
                    holder.setText(R.id.tv_item_name, item.getType());

                    String date = item.getCreatedAtDate() + Html.fromHtml("&nbsp;");

                    holder.setText(R.id.tv_item_detail, date);

                    if (item.getStatus().equalsIgnoreCase("deposit")) {
                        holder.setText(R.id.tv_indicator, "+ " + item.getAmount());
                        holder.setTextColor(R.id.tv_tx, getContext().getResources().getColor(R.color.vexpoint_plus));
                    } else {
                        holder.setText(R.id.tv_indicator, "- " + item.getAmount());
                        holder.setTextColor(R.id.tv_tx, getContext().getResources().getColor(R.color.vexpoint_minus));
                    }
                }
            };

            mBonusLogAdapter.setHasStableIds(true);
            mRecyclerview.setLayoutManager(linearLayoutManager);
            if (this.getActivity() != null)
                mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this.getActivity(), 16)));
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
            mRecyclerview.setAdapter(mBonusLogAdapter);
            mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    App.setTextViewStyle(mRecyclerview);
                }
            });
        } else {
            mBonusLogAdapter.setData(dataList);
        }

        if (dataList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.wallet_empty);
            mTvErrorHead.setText(getString(R.string.wallet_bonus_log_empty_title));
            //mTvErrorBody.setVisibility(View.GONE);
            mTvErrorBody.setText(getString(R.string.wallet_bonus_log_empty_text));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);

            mRecyclerview.setVisibility(View.VISIBLE);
        }
    }
}

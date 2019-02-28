package com.vexanium.vexgift.module.referral.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
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
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.ReferralSpecialEvent;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.WalletReferralResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.vexpoint.ui.adapter.VexPointAdapter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.RxBus;

import java.io.Serializable;
import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.fragment_vexpoint)
public class ReferralListFragment extends BaseFragment<IWalletPresenter> implements IWalletView {

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    ArrayList<ReferralSpecialEvent> dataList = new ArrayList<>();
    WalletReferralResponse walletReferralResponse;
    private BaseRecyclerAdapter<ReferralSpecialEvent> mTransactionLogAdapter;
    private RecyclerView mRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private VexPointAdapter mAdapter;
    private Observable<Boolean> mTransactionObservable;
    private boolean isCounted;

    public static ReferralListFragment newInstance(boolean isCounted) {
        ReferralListFragment fragment = new ReferralListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isCounted", isCounted);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View fragmentRootView) {
        mRecyclerview = fragmentRootView.findViewById(R.id.rv_vexpoint);

        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mTransactionObservable = RxBus.get().register(RxBus.KEY_WALLET_REFERRAL_RECORD_ADDED, Boolean.class);
        mTransactionObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean b) {
                getDataFromDb();
            }
        });

        isCounted = getArguments().getBoolean("isCounted", true);

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
        if (mTransactionObservable != null) {
            RxBus.get().unregister(RxBus.KEY_WALLET_TRANSACTION_RECORD_ADDED, mTransactionObservable);
        }
    }


    public void getDataFromDb() {
        walletReferralResponse = TableContentDaoUtil.getInstance().getWalletReferral();
        if (walletReferralResponse != null) {
            ArrayList<ReferralSpecialEvent> referralSpecialEvents = isCounted ? walletReferralResponse.getCountedReferrals() : walletReferralResponse.getUncountedReferrals();
            if (referralSpecialEvents != null) {
                dataList = referralSpecialEvents;
                initReferralLog();
            }
        }
    }

    public void initReferralLog() {
        if (mTransactionLogAdapter == null) {
            mTransactionLogAdapter = new BaseRecyclerAdapter<ReferralSpecialEvent>(this.getActivity(), dataList, linearLayoutManager) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_wallet_referral_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final ReferralSpecialEvent item) {

                    float totalVex = 0;
                    if (item.getWallet() != null) {
                        totalVex = item.getWallet().getBalance();
                    }

                    holder.setText(R.id.tv_referral_user, String.format("%s (%s)", item.getName(), !TextUtils.isEmpty(item.getEmail()) ? item.getEmail() : item.getPhoneNumber()));

                    String date = item.getCreatedAtDate() + Html.fromHtml("&nbsp;");

                    holder.setText(R.id.tv_time, date);

                    holder.getTextView(R.id.tv_referral_user).setSelected(true);
                    holder.setImageResource(R.id.iv_kyc, isCounted ? R.drawable.ic_checked_green : R.drawable.ic_unchecked_green);

                    holder.setOnClickListener(R.id.ll_referral_root, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            holder.setViewGone(R.id.ll_detail, view.findViewById(R.id.ll_detail).getVisibility() == View.VISIBLE);
                            holder.getView(R.id.iv_arrow).setRotation(view.findViewById(R.id.ll_detail).getVisibility() == View.VISIBLE ? 270 : 90);
                        }
                    });


                    String s = getString(R.string.wallet_referral_total_vex) + " " + totalVex + "";
                    holder.setText(R.id.tv_already_counted, s);

                }
            };

            mTransactionLogAdapter.setHasStableIds(true);
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
            mRecyclerview.setAdapter(mTransactionLogAdapter);
            mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    App.setTextViewStyle(mRecyclerview);
                }
            });
        } else {
            mTransactionLogAdapter.setData(dataList);
        }

        if (dataList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.wallet_empty);
            mTvErrorHead.setText(getString(isCounted ? R.string.wallet_referral_counted_empty_title : R.string.wallet_referral_uncounted_empty_title));
            //mTvErrorBody.setVisibility(View.GONE);
            mTvErrorBody.setText(getString(isCounted ? R.string.wallet_referral_counted_empty_text : R.string.wallet_referral_uncounted_empty_text));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);

            mRecyclerview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

        if (data != null) {

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(getActivity(), errorResponse);
        }
    }
}

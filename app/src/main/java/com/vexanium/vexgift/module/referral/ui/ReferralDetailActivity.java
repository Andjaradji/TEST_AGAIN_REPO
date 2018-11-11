package com.vexanium.vexgift.module.referral.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.vexanium.vexgift.bean.model.Referral;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserReferralResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.database.TableDepositDaoUtil;
import com.vexanium.vexgift.module.referral.presenter.IReferralPresenter;
import com.vexanium.vexgift.module.referral.presenter.IReferralPresenterImpl;
import com.vexanium.vexgift.module.referral.view.IReferralView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_referral_detail, withLoadingAnim = true, toolbarTitle = R.string.referral_detail_title)
public class ReferralDetailActivity extends BaseActivity<IReferralPresenter> implements IReferralView {
    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;

    UserReferralResponse userReferralResponse;
    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<Referral> mAdapter;
    RecyclerView mRecyclerview;
    private ArrayList<Referral> userReferrals;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IReferralPresenterImpl(this);

        userReferralResponse = TableContentDaoUtil.getInstance().getReferrals();
        if (userReferralResponse != null) {
            userReferrals = userReferralResponse.getReferrals();
        }
        if (userReferrals == null) {
            userReferrals = new ArrayList<>();
        }

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestUserReferral(user.getId());
            }
        });

        mPresenter.requestUserReferral(user.getId());

        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        setReferralHistoryList();

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof UserReferralResponse) {
                UserReferralResponse userDepositResponse = (UserReferralResponse) data;
                TableDepositDaoUtil.getInstance().saveUserDepositsToDb(JsonUtil.toString(userDepositResponse));

                userReferrals = userDepositResponse.getReferrals();
                setReferralHistoryList();
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }


    public void setReferralHistoryList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<Referral>(this, userReferrals, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_referral_history;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final Referral item) {
                    if (item.getUser() != null && item.getUser().getName() != null) {
                        holder.setText(R.id.tv_referral_user, item.getUser().getName());
                    } else {
                        holder.setText(R.id.tv_referral_user, "-");
                    }

                    holder.setImageResource(R.id.iv_kyc, item.isKyc() ? R.drawable.ic_checked_green : R.drawable.ic_unchecked_green);
                    holder.setImageResource(R.id.iv_pm, item.isPremiumMember() ? R.drawable.ic_checked_green : R.drawable.ic_unchecked_green);
                    holder.setTextColor(R.id.tv_already_kyc, ContextCompat.getColor(ReferralDetailActivity.this, item.isKyc() ? R.color.material_black_text_color : R.color.material_grey_400));
                    holder.setTextColor(R.id.tv_pm, ContextCompat.getColor(ReferralDetailActivity.this, item.isPremiumMember() ? R.color.material_black_text_color : R.color.material_grey_400));
                    holder.setText(R.id.tv_already_kyc, getString(item.isKyc() ? R.string.referral_detail_already_kyc : R.string.referral_detail_havent_kyc));
                    holder.setText(R.id.tv_pm, getString(item.isKyc() ? R.string.referral_detail_already_buy_premium_member : R.string.referral_detail_havent_buy_premium_member));

                    holder.setText(R.id.tv_time, StaticGroup.getDate(item.getCreatedAt()));
                    holder.setOnClickListener(R.id.ll_referral_root, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            if (holder.getView(R.id.ll_detail).getVisibility() == View.GONE) {
                                holder.setViewGone(R.id.ll_detail, false);
                                holder.getView(R.id.iv_arrow).setRotation(90);
                            } else {
                                holder.setViewGone(R.id.ll_detail, true);
                                holder.getView(R.id.iv_arrow).setRotation(270);
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
            mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    App.setTextViewStyle(mRecyclerview);
                }
            });
        } else {
            mAdapter.setData(userReferrals);
        }

        if (userReferrals.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.error_referral_empty_header));
            mTvErrorBody.setText(getString(R.string.error_referral_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerview.setVisibility(View.VISIBLE);

        }
    }


}

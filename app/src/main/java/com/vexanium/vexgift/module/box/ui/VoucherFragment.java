package com.vexanium.vexgift.module.box.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.model.VoucherCode;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserVouchersResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.box.presenter.IBoxPresenter;
import com.vexanium.vexgift.module.box.presenter.IBoxPresenterImpl;
import com.vexanium.vexgift.module.box.view.IBoxView;
import com.vexanium.vexgift.module.voucher.ui.VoucherRedeemActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.RxBus;

import java.io.Serializable;
import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Amang on 16/07/2018.
 */

@ActivityFragmentInject(contentViewId = R.layout.fragment_box_child)
public class VoucherFragment extends BaseFragment<IBoxPresenter> implements IBoxView {

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    BaseRecyclerAdapter<VoucherCode> mAdapter;
    SwipeRefreshLayout mRefreshLayout;
    GridLayoutManager layoutListManager;
    RecyclerView mRecyclerview;
    Observable<Integer> mVoucherObservable;
    User user;
    private Context context;
    private ArrayList<VoucherCode> data;

    public static VoucherFragment newInstance() {
        return new VoucherFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("VoucherFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View fragmentRootView) {
        mPresenter = new IBoxPresenterImpl(this);
        user = User.getCurrentUser(this.getContext());
        if (getActivity() != null) {
            context = getActivity();
        }

        mRefreshLayout = fragmentRootView.findViewById(R.id.srl_refresh);
        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        mRecyclerview = fragmentRootView.findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        loadData();
        setVoucherList();

        mVoucherObservable = RxBus.get().register(RxBus.KEY_BOX_CHANGED, Integer.class);
        mVoucherObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                mPresenter.requestUserVoucherList(user.getId());
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestUserVoucherList(user.getId());
            }
        });

        mPresenter.requestUserVoucherList(user.getId());
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof UserVouchersResponse) {
                UserVouchersResponse vouchersResponse = (UserVouchersResponse) data;
                TableContentDaoUtil.getInstance().saveBoxsToDb(JsonUtil.toString(vouchersResponse));

                loadData();
                setVoucherList();
            }

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this.getContext(), errorResponse);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.requestUserVoucherList(user.getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVoucherObservable != null) {
            RxBus.get().unregister(RxBus.KEY_BOX_HISTORY_ADDED, mVoucherObservable);
            mVoucherObservable = null;
        }
    }

    public void loadData() {
        if (TableContentDaoUtil.getInstance().getMyBoxContent() != null)
            data = TableContentDaoUtil.getInstance().getMyBoxContent().getActiveVoucher();
        if (data == null) data = new ArrayList<>();
        KLog.v("VoucherFragment", "loadData: ==========================================================");
        KLog.json("HPtes", JsonUtil.toString(data));
        KLog.v("VoucherFragment", "loadData: =======================================================================");
    }

    public void setVoucherList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<VoucherCode>(context, data, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_coupon_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final VoucherCode item) {
                    final Voucher voucher = item.getVoucher();
                    holder.setImageUrl(R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                    holder.setText(R.id.tv_coupon_title, voucher.getTitle());
                    holder.setText(R.id.tv_coupon_exp, voucher.getExpiredDate());
                    holder.setViewInvisible(R.id.ll_qty, true);
                    holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            goToVoucherRedeemActivity(item, holder.getImageView(R.id.iv_coupon_image));
                        }
                    });

                    if (voucher.isForPremium())
                        holder.setViewGone(R.id.iv_premium, false);
                    else
                        holder.setViewGone(R.id.iv_premium, true);

                    holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (voucher.isForPremium() && !user.isPremiumMember()) {
//                                StaticGroup.showPremiumMemberDialog(VoucherFragment.this.getActivity());
//                            } else {
                            goToVoucherRedeemActivity(item, holder.getImageView(R.id.iv_coupon_image));
//                            }
                        }
                    });

                }
            };
            mAdapter.setHasStableIds(true);
            mRecyclerview.setLayoutManager(layoutListManager);
            mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(context, 16)));
            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
            mRecyclerview.getItemAnimator().setAddDuration(250);
            mRecyclerview.getItemAnimator().setMoveDuration(250);
            mRecyclerview.getItemAnimator().setChangeDuration(250);
            mRecyclerview.getItemAnimator().setRemoveDuration(250);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setItemViewCacheSize(30);
            mRecyclerview.setDrawingCacheEnabled(true);
            mRecyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    App.setTextViewStyle(mRecyclerview);
                }
            });
        } else {
            mAdapter.setData(data);
        }

        if (data.size() <= 0) {
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

    private void goToVoucherRedeemActivity(VoucherCode voucherResponse, ImageView ivVoucher) {
        Intent intent = new Intent(this.getActivity(), VoucherRedeemActivity.class);
        intent.putExtra("voucher", JsonUtil.toString(voucherResponse));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this.getActivity(), ivVoucher, "voucher_image");
        startActivity(intent, options.toBundle());
    }
}

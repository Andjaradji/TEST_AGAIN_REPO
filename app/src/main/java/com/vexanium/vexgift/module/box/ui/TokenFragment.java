package com.vexanium.vexgift.module.box.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.vexanium.vexgift.bean.response.VoucherResponse;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.MeasureUtil;

import java.util.ArrayList;

/**
 * Created by Amang on 16/07/2018.
 */

@ActivityFragmentInject(contentViewId = R.layout.fragment_box_child)
public class TokenFragment extends BaseFragment {

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    SwipeRefreshLayout mRefreshLayout;
    GridLayoutManager layoutListManager;
    RecyclerView mRecyclerview;
    private Context context;
    private ArrayList<VoucherResponse> data;
    private User user;

    public static TokenFragment newInstance() {
        return new TokenFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("Token onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View fragmentRootView) {
        if (getActivity() != null) {
            context = getActivity();
        }
        user = User.getCurrentUser(context);

        mRefreshLayout = fragmentRootView.findViewById(R.id.srl_refresh);
        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        mRecyclerview = fragmentRootView.findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        data = new ArrayList<>();
        setVoucherList(data);

        // Setup refresh listener which triggers new data loading
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //fetchTimelineAsync(0);
                updateData();//update data here

            }
        });

    }

    private void updateData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    public void setVoucherList(final ArrayList<VoucherResponse> data) {

        BaseRecyclerAdapter<VoucherResponse> mAdapter = new BaseRecyclerAdapter<VoucherResponse>(context, data, layoutListManager) {
            @Override
            public int getItemViewType(int position) {
                return data.get(position).type;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_coupon_list;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final VoucherResponse item) {
                final Voucher voucher = item.getVoucher();
                holder.setImageUrl(R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_title, voucher.getTitle());
                holder.setText(R.id.tv_coupon_exp, voucher.getExpiredDate());
                holder.setViewInvisible(R.id.ll_qty, true);
                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtil.isFastDoubleClick()) return;
//                        goToVoucherRedeemActivity(item, holder.getImageView(R.id.iv_coupon_image));
                    }
                });

                if (voucher.isForPremium())
                    holder.setViewGone(R.id.iv_premium, false);
                else
                    holder.setViewGone(R.id.iv_premium, true);

                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (voucher.isForPremium() && !user.isPremiumMember()) {
                            StaticGroup.showPremiumMemberDialog(context);
                        } else {
//                            goToVoucherRedeemActivity(item, holder.getImageView(R.id.iv_coupon_image));
                        }
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

        if (data.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.token_empty);
            mTvErrorHead.setText(getString(R.string.error_token_empty_header));
            mTvErrorBody.setText(getString(R.string.error_my_token_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        }
    }
}

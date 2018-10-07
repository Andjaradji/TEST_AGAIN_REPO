package com.vexanium.vexgift.module.exchanger.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.fixture.Exchanger;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.TokenSaleHistory;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.UserAddress;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.bean.response.VexPointRecordResponse;
import com.vexanium.vexgift.module.referral.ui.ReferralActivity;
import com.vexanium.vexgift.module.tokensale.ui.TokenSaleHistoryActivity;
import com.vexanium.vexgift.module.tokensale.ui.TokenSaleHistoryDetailActivity;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenter;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenterImpl;
import com.vexanium.vexgift.module.vexpoint.ui.InvitePointFragment;
import com.vexanium.vexgift.module.vexpoint.ui.PointRecordFragment;
import com.vexanium.vexgift.module.vexpoint.ui.VexAddressActivity;
import com.vexanium.vexgift.module.vexpoint.ui.VexPointGuideActivity;
import com.vexanium.vexgift.module.vexpoint.view.IVexpointView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.IconTextTabBarView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.StaticGroup.convertVpFormat;

@ActivityFragmentInject(contentViewId = R.layout.activity_buy_vex, toolbarTitle = R.string.buy_vex, withLoadingAnim = false)
public class BuyVexActivity extends BaseActivity {

    private SwipeRefreshLayout mRefreshLayout;
    RecyclerView mRecyclerview;
    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<Exchanger> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {

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

        setVexExchangeList();

    }

    public void setVexExchangeList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<Exchanger>(this, FixtureData.exchangerList, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_vex_exchanger;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, final int position, final Exchanger item) {
                    holder.setText(R.id.tv_vex_exchanger_title, item.getName());
                    holder.setOnClickListener(R.id.rl_root, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FixtureData.exchangerList.get(holder.getAdapterPosition()).getUrl()));
                            startActivity(browserIntent);
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
            mAdapter.setData(FixtureData.exchangerList);
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


}

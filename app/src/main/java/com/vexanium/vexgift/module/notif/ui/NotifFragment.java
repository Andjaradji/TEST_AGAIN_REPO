package com.vexanium.vexgift.module.notif.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.vexanium.vexgift.bean.model.Notification;
import com.vexanium.vexgift.bean.model.Vendor;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.notif.presenter.INotifPresenter;
import com.vexanium.vexgift.module.notif.view.INotifView;
import com.vexanium.vexgift.module.voucher.ui.VoucherRedeemActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import rx.Observable;
import rx.functions.Action1;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

@ActivityFragmentInject(contentViewId = R.layout.fragment_notif)
public class NotifFragment extends BaseFragment<INotifPresenter> implements INotifView, View.OnClickListener {
    public static NotifFragment newInstance() {
        return new NotifFragment();
    }

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;

    SwipeRefreshLayout mRefreshLayout;
    private BaseRecyclerAdapter<Notification> mNotifListAdapter;
    private GridLayoutManager layoutListManager;

    private RecyclerView mRecyclerview;
    private ArrayList<Notification> data;
    private Random random;

    private Observable<Integer> mNotifObservable;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("NotifFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

    }

    @Override
    protected void initView(View fragmentRootView) {
        random = new Random();

        mRefreshLayout = (SwipeRefreshLayout)fragmentRootView.findViewById(R.id.srl_refresh);
        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        mRecyclerview = (RecyclerView) fragmentRootView.findViewById(R.id.notif_recyclerview);
        layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        loadData();
        initNotifList();
        StaticGroup.setAllNotifToAbsolute(data);

        ViewUtil.setText(fragmentRootView, R.id.tv_toolbar_title, "NOTIFICATION");
        App.setTextViewStyle((ViewGroup) fragmentRootView);

        mNotifObservable = RxBus.get().register(RxBus.KEY_NOTIF_ADDED, Integer.class);
        mNotifObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer vp) {
                KLog.v("NotifFragment", "call: HPtes masuk");
                loadData();
                mNotifListAdapter.setData(data);
                mNotifListAdapter.notifyDataSetChanged();
            }
        });

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

    private void updateData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        },3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KLog.v("NotifFragment onDestroyView");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNotifObservable != null) {
            RxBus.get().unregister(RxBus.KEY_NOTIF_ADDED, mNotifObservable);
            mNotifObservable = null;
        }
    }

    public void loadData() {
        data = TableContentDaoUtil.getInstance().getNotifs();
        if (data == null)
            data = new ArrayList<>();
        Collections.sort(data, new Comparator<Notification>() {
            @Override
            public int compare(Notification notification, Notification t1) {
                return Long.compare(t1.getTime(), notification.getTime());
            }
        });
        KLog.json("HPtes", JsonUtil.toString(data));
    }

    private void setTextSpan(String content, TextView textView, final Voucher voucher, final boolean isNew) {

        int i1 = content.indexOf("[");
        int i2 = content.indexOf("]") - 1;
        content = content.replace("[", "<b>").replace("]", "</b>");

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(content);
//        textView.setHighlightColor(isNew ? getResources().getColor(R.color.colorPrimaryDark) : Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= 24) {
            textView.setText(Html.fromHtml(content, FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(content));
        }
        SpannableString ss = (SpannableString) textView.getText();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (ClickUtil.isFastDoubleClick()) return;
                goToVoucherDetailActivity(voucher);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(isNew ? R.color.colorPrimaryDark : R.color.material_black_sub_text_color));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, i1, i2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    public void initNotifList() {
        mNotifListAdapter = new BaseRecyclerAdapter<Notification>(this.getActivity(), data, layoutListManager) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_notif_list;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final Notification item) {
                String content;
                switch (item.getType()) {
                    case "exp":
                        content = String.format(getString(R.string.notif_expired), item.getVoucher().getTitle());
                        break;
                    case "exp_soon":
                        content = String.format(getString(R.string.notif_expire_soon), item.getVoucher().getTitle(), item.getVoucher().getExpiredDate());
                        break;
                    case Notification.TYPE_GET_SUCCESS:
                        content = String.format(getString(R.string.notif_success_claim), item.getVoucher().getTitle(), item.getVoucher().getExpiredDate());
                        break;
                    case "avail":
                        content = String.format(getString(R.string.notif_is_available), item.getVoucher().getTitle());
                        break;
                    default:
                        content = String.format(getString(R.string.notif_is_available), item.getVoucher().getTitle());
                }

                setTextSpan(content, holder.getTextView(R.id.tv_content), item.getVoucher(), item.isNew());
                Vendor vendor = item.getVoucher().getVendor();
                holder.setText(R.id.tv_brand, vendor.getName());
                holder.setRoundImageUrl(R.id.iv_photo, vendor.getThumbnail(), R.drawable.placeholder);
                holder.setViewGone(R.id.iv_red_dot, !item.isNew());
                if (item.isNew()) {
                    holder.getTextView(R.id.tv_brand).setTextColor(getResources().getColor(R.color.material_black_text_color));
                    holder.getTextView(R.id.tv_content).setTextColor(getResources().getColor(R.color.material_black_text_color));
                    holder.getTextView(R.id.tv_time).setTextColor(getResources().getColor(R.color.material_black_text_color));
                }
                long timeInterval = (System.currentTimeMillis() - item.getTime()) / 1000;
//                KLog.v("NotifFragment", "HPtes  now [" + System.currentTimeMillis() + "] - time [" + item.getTime() + "] = " + timeInterval + " ");
                if (timeInterval <= 60) {
                    holder.setText(R.id.tv_time, getString(R.string.notif_item_time_now));
                } else if (timeInterval <= 60 * 60) {
                    holder.setText(R.id.tv_time, String.format(getString(R.string.notif_item_time_min), timeInterval / 60));
                } else if (timeInterval <= 24 * 60 * 60) {
                    holder.setText(R.id.tv_time, String.format(getString(R.string.notif_item_time_hour), timeInterval / (60 * 60)));
                } else if (timeInterval <= 7 * 24 * 60 * 60) {
                    holder.setText(R.id.tv_time, String.format(getString(R.string.notif_item_time_day), timeInterval / (24 * 60 * 60)));
                } else if (timeInterval <= 30 * 24 * 60 * 60) {
                    holder.setText(R.id.tv_time, String.format(getString(R.string.notif_item_time_week), timeInterval / (7 * 24 * 60 * 60)));
                } else if (timeInterval <= 365 * 24 * 60 * 60) {
                    holder.setText(R.id.tv_time, String.format(getString(R.string.notif_item_time_month), timeInterval / (30 * 24 * 60 * 60)));
                } else {
                    holder.setText(R.id.tv_time, String.format(getString(R.string.notif_item_time_year), timeInterval / (365 * 24 * 60 * 60)));
                }
                holder.setOnClickListener(R.id.root, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        if (!TextUtils.isEmpty(item.getUrl()))
                            ((MainActivity) getActivity()).openDeepLink(item.getUrl());
                    }
                });
                holder.setOnClickListener(R.id.tv_brand, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        if (!TextUtils.isEmpty(item.getUrl()))
                            ((MainActivity) getActivity()).openDeepLink(item.getUrl());
                    }
                });
                holder.setOnClickListener(R.id.tv_content, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        if (!TextUtils.isEmpty(item.getUrl()))
                            ((MainActivity) getActivity()).openDeepLink(item.getUrl());
                    }
                });
            }
        };

        mNotifListAdapter.setHasStableIds(true);
        mRecyclerview.setLayoutManager(layoutListManager);
        mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this.getActivity(), 16)));
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
        mRecyclerview.setAdapter(mNotifListAdapter);
        mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                App.setTextViewStyle(mRecyclerview);
            }
        });

        if (data.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.notif_empty);
            mTvErrorHead.setText(getString(R.string.error_notif_empty_header));
            //mTvErrorBody.setVisibility(View.GONE);
            mTvErrorBody.setText(getString(R.string.error_notif_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        }
    }

    private void goToVoucherDetailActivity(Voucher voucherResponse) {
        Intent intent = new Intent(this.getActivity(), VoucherRedeemActivity.class);
        intent.putExtra("voucher", JsonUtil.toString(voucherResponse));
        startActivity(intent);
    }
}
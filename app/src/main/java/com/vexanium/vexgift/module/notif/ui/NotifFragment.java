package com.vexanium.vexgift.module.notif.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
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
import com.vexanium.vexgift.bean.model.NotificationModel;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.NotificationResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.notif.presenter.INotifPresenter;
import com.vexanium.vexgift.module.notif.presenter.INotifPresenterImpl;
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

import rx.Observable;
import rx.functions.Action1;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

@ActivityFragmentInject(contentViewId = R.layout.fragment_notif)
public class NotifFragment extends BaseFragment<INotifPresenter> implements INotifView, View.OnClickListener {
    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    SwipeRefreshLayout mRefreshLayout;

    private BaseRecyclerAdapter<NotificationModel> mNotifListAdapter;
    private GridLayoutManager layoutListManager;
    private RecyclerView mRecyclerview;
    private ArrayList<NotificationModel> dataList;
    private User user;
    private Observable<Integer> mNotifObservable;


    public static NotifFragment newInstance() {
        return new NotifFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("NotifFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if(data!=null){
            if(data instanceof NotificationResponse){
                dataList = new ArrayList<>(((NotificationResponse) data).getNotifications());
                initNotifList();
            }
        }else if(errorResponse!=null){

        }
    }

    @Override
    protected void initView(View fragmentRootView) {
        mPresenter = new INotifPresenterImpl(this);
        user = User.getCurrentUser(getActivity());

        mRefreshLayout = (SwipeRefreshLayout) fragmentRootView.findViewById(R.id.srl_refresh);
        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        mRecyclerview = (RecyclerView) fragmentRootView.findViewById(R.id.notif_recyclerview);
        layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        loadData();
        initNotifList();


        mNotifObservable = RxBus.get().register(RxBus.KEY_NOTIF_ADDED, Integer.class);
        mNotifObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer vp) {
                KLog.v("NotifFragment", "call: HPtes masuk");
                mPresenter.requestNotifList(user.getId());
            }
        });


        ViewUtil.setText(fragmentRootView, R.id.tv_toolbar_title, "NOTIFICATION");
        App.setTextViewStyle((ViewGroup) fragmentRootView);

        mPresenter.requestNotifList(user.getId());

        // Setup refresh listener which triggers new dataList loading
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //fetchTimelineAsync(0);
                updateData();//update dataList here

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        StaticGroup.setAllNotificationToAbsolute(dataList);
    }

    @Override
    public void onResume() {
        super.onResume();
        mNotifListAdapter.notifyDataSetChanged();
    }

    private void updateData() {
        mRefreshLayout.setRefreshing(false);
        mPresenter.requestNotifList(user.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KLog.v("NotifFragment onDestroyView");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void loadData() {
        dataList = TableContentDaoUtil.getInstance().getNotifications();
        if (dataList == null)
            dataList = new ArrayList<>();
        Collections.sort(dataList, new Comparator<NotificationModel>() {
            @Override
            public int compare(NotificationModel notification, NotificationModel t1) {
                return t1.getCreatedAtDate().compareTo(notification.getCreatedAtDate());
            }
        });
        KLog.json("HPtes", JsonUtil.toString(dataList));
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
        if (mNotifListAdapter == null) {
            mNotifListAdapter = new BaseRecyclerAdapter<NotificationModel>(this.getActivity(), dataList, layoutListManager) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_notification_list;
                }

                @Override
                public void bindData(BaseRecyclerViewHolder holder, int position, final NotificationModel item) {

                    holder.setText(R.id.tv_brand,item.getTitle());
                    holder.setText(R.id.tv_content, item.getBody());
                    holder.setOnClickListener(R.id.root, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    long timeInterval = (System.currentTimeMillis() - item.getCreatedAtMillis()) / 1000;
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
        } else {
            mNotifListAdapter.setData(dataList);
        }

        if (dataList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.notif_empty);
            mTvErrorHead.setText(getString(R.string.error_notif_empty_header));
            //mTvErrorBody.setVisibility(View.GONE);
            mTvErrorBody.setText(getString(R.string.error_notif_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);

            mRecyclerview.setVisibility(View.VISIBLE);
        }
    }

    private void goToVoucherDetailActivity(Voucher voucherResponse) {
        Intent intent = new Intent(this.getActivity(), VoucherRedeemActivity.class);
        intent.putExtra("voucher", JsonUtil.toString(voucherResponse));
        startActivity(intent);
    }
}
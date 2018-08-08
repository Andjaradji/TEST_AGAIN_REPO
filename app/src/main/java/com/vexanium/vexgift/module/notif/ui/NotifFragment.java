package com.vexanium.vexgift.module.notif.ui;

import android.os.Build;
import android.os.Bundle;
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
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.Brand;
import com.vexanium.vexgift.bean.model.Notification;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.notif.presenter.INotifPresenter;
import com.vexanium.vexgift.module.notif.view.INotifView;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

@ActivityFragmentInject(contentViewId = R.layout.fragment_notif)
public class NotifFragment extends BaseFragment<INotifPresenter> implements INotifView, View.OnClickListener {
    public static NotifFragment newInstance() {
        return new NotifFragment();
    }

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead,mTvErrorBody;

    private BaseRecyclerAdapter<Notification> mNotifListAdapter;
    private GridLayoutManager layoutListManager;

    private RecyclerView mRecyclerview;
    private ArrayList<Notification> data;
    private Random random;

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

        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        mRecyclerview = (RecyclerView) fragmentRootView.findViewById(R.id.notif_recyclerview);
        layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        loadData();
        initNotifList();

        ViewUtil.setText(fragmentRootView,R.id.tv_toolbar_title,"NOTIFICATION");
        App.setTextViewStyle((ViewGroup) fragmentRootView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KLog.v("NotifFragment onDestroyView");
    }

    public void loadData() {
        data = new ArrayList<>();
//        data = FixtureData.notifs;
    }

    private void setTextSpan(String content, TextView textView, final Voucher voucher, final boolean isNew) {
        content = String.format(content, voucher.getTitle());
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
                        content = getString(R.string.notif_expired);
                        break;
                    case "exp_soon":
                        content = getString(R.string.notif_expire_soon);
                        break;
                    case "avail":
                        content = getString(R.string.notif_is_available);
                        break;
                    default:
                        content = getString(R.string.notif_is_available);
                }

                setTextSpan(content, holder.getTextView(R.id.tv_content), item.getVoucher(), item.isNew());
//                Brand brand = item.getVoucher().getBrand();
//                holder.setText(R.id.tv_brand, brand.getTitle());
//                holder.setRoundImageUrl(R.id.iv_photo, brand.getPhoto(), R.drawable.placeholder);
//                holder.setViewGone(R.id.iv_red_dot, !item.isNew());
//                if (item.isNew()) {
//                    holder.getTextView(R.id.tv_brand).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                    holder.getTextView(R.id.tv_content).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                    holder.getTextView(R.id.tv_time).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                }
//                int ranInt = random.nextInt(24 * 7 * 100);
//                if (ranInt <= 100) {
//                    holder.setText(R.id.tv_time, getString(R.string.notif_item_time_now));
//                } else if (ranInt <= 24 * 100) {
//                    holder.setText(R.id.tv_time, String.format(getString(R.string.notif_item_time_hour), ranInt / 100));
//                } else if (ranInt <= 24 * 100 * 7) {
//                    holder.setText(R.id.tv_time, String.format(getString(R.string.notif_item_time_hour), ranInt / 2400));
//                }
//                holder.setOnClickListener(R.id.iv_photo, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
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

        if(data.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.notif_empty);
            mTvErrorHead.setText(getString(R.string.error_notif_empty_header));
            //mTvErrorBody.setVisibility(View.GONE);
            mTvErrorBody.setText(getString(R.string.error_notif_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        }
    }
}
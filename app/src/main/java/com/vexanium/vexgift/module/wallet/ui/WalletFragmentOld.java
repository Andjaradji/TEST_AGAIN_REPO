package com.vexanium.vexgift.module.wallet.ui;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.bean.model.WalletRecord;
import com.vexanium.vexgift.module.wallet.ui.adapter.WalletAdapter;
import com.vexanium.vexgift.util.ViewUtil;

import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.fragment_wallet)
public class WalletFragmentOld extends BaseFragment {

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;

    private RecyclerView mRecycler;
    private WalletAdapter mAdapter;

    public static WalletFragmentOld newInstance() {
        return new WalletFragmentOld();
    }

    @Override
    protected void initView(View fragmentRootView) {
        ViewUtil.setText(fragmentRootView, R.id.tv_toolbar_title, getString(R.string.my_wallet_title));

        mRecycler = fragmentRootView.findViewById(R.id.rv_wallet_record);

        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

//        LinearLayout mMainContainer = fragmentRootView.findViewById(R.id.ll_wallet_main);
//        LinearLayout mButtonContainer = fragmentRootView.findViewById(R.id.ll_wallet_coin_container);
//        ImageView mIvComingSoon = fragmentRootView.findViewById(R.id.iv_coming_soon);
//
//        //Coming soon view
//        mIvComingSoon.setVisibility(View.VISIBLE);
//        mMainContainer.setVisibility(View.GONE);
//        mButtonContainer.setVisibility(View.GONE);
//        //mRecordContainer.setVisibility(View.VISIBLE);
//
//        if (mIvComingSoon.getVisibility() != View.VISIBLE) {
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//            mRecycler.setLayoutManager(linearLayoutManager);
//
//            mAdapter = new WalletAdapter(getActivity());
//            mRecycler.setAdapter(mAdapter);
//
//            if (getContext() != null && getActivity() != null) {
//                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
//                        linearLayoutManager.getOrientation());
//                dividerItemDecoration.setDrawable(getActivity().getResources().getDrawable(R.drawable.shape_divider));
//                mRecycler.addItemDecoration(dividerItemDecoration);
//
//                setRecordlist(new ArrayList<WalletRecord>());
//            }
//        }

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

    private void setRecordlist(ArrayList<WalletRecord> dataList) {

        WalletRecord data = new WalletRecord("ASDNJKSAD23FB", "16-08-2018 15:00 GMT", 1, 1500);
        WalletRecord data1 = new WalletRecord("ASDNJKSAD23FB", "16-08-2018 15:00 GMT", 0, 1500);

        dataList.add(data);
        dataList.add(data1);
        dataList.add(data);
        dataList.add(data1);
        dataList.add(data);
        dataList.add(data1);
        dataList.add(data);
        dataList.add(data1);

        if (dataList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.wallet_empty);
            mTvErrorHead.setVisibility(View.GONE);
            mTvErrorBody.setText(getString(R.string.error_wallet_empty_header));

            mRecycler.setVisibility(View.GONE);
        } else {
            mAdapter.addItemList(dataList);
            mAdapter.notifyDataSetChanged();
        }
    }

}

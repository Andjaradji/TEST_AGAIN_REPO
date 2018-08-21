package com.vexanium.vexgift.module.vexpoint.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.bean.model.VexPointRecord;
import com.vexanium.vexgift.module.vexpoint.ui.adapter.VexPointAdapter;
import com.vexanium.vexgift.util.RxBus;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.fragment_vexpoint)
public class PointRecordFragment extends BaseFragment {

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    ArrayList<VexPointRecord> dataList = new ArrayList<>();
    private RecyclerView mRecycler;
    private LinearLayoutManager linearLayoutManager;
    private VexPointAdapter mAdapter;
    private Observable<Integer> mVpObservable;

    public static PointRecordFragment newInstance() {
        return new PointRecordFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        mRecycler = fragmentRootView.findViewById(R.id.rv_vexpoint);

        mErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorHead = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(linearLayoutManager);

        mAdapter = new VexPointAdapter(getActivity());
        mRecycler.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getActivity().getResources().getDrawable(R.drawable.shape_divider));
        mRecycler.addItemDecoration(dividerItemDecoration);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.getItemAnimator().setAddDuration(250);
        mRecycler.getItemAnimator().setMoveDuration(250);
        mRecycler.getItemAnimator().setChangeDuration(250);
        mRecycler.getItemAnimator().setRemoveDuration(250);

        mVpObservable = RxBus.get().register(RxBus.KEY_VP_RECORD_ADDED, Integer.class);
        mVpObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer vp) {

                mRecycler.setVisibility(View.VISIBLE);
                mTvErrorHead.setVisibility(View.GONE);
                mErrorView.setVisibility(View.GONE);

                KLog.v("PointRecordFragment", "call: called HPtes");
                VexPointRecord data = new VexPointRecord("Vex Point from Snapshoot", "05-08-2018 00:00 GMT", 0, vp);
                dataList.add(data);
                mAdapter.setItemList(dataList);
                mAdapter.notifyDataSetChanged();
            }
        });

        populateData();

    }

    private void populateData() {
        mAdapter.addItemList(dataList);
        mAdapter.notifyDataSetChanged();

        if (dataList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.vp_empty);
            mTvErrorBody.setText(getString(R.string.error_vp_point_record_empty_header));
            mTvErrorHead.setVisibility(View.GONE);
            //mTvErrorBody.setText(getString(R.string.error_my_voucher_empty_body));

            mRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("PointRecordFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

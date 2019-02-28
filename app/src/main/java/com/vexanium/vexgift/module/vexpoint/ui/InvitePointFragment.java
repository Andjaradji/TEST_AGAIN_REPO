package com.vexanium.vexgift.module.vexpoint.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.fragment_vexpoint)
public class InvitePointFragment extends BaseFragment {

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;

    private RecyclerView mRecycler;
    private LinearLayoutManager linearLayoutManager;
    private VexPointAdapter mAdapter;

    public static InvitePointFragment newInstance() {
        return new InvitePointFragment();
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

        populateData();
    }

    private void populateData() {
        ArrayList<VexPointRecord> dataList = new ArrayList<>();

        /*VexPointRecord data1 = new VexPointRecord("Point from VEX Deposit","16-08-2018 15:00 GMT",0,1500);

        dataList.add(data1);
        dataList.add(data1);
        dataList.add(data1);
        dataList.add(data1);*/

        if (dataList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.vp_empty);
            mTvErrorBody.setText(getString(R.string.error_vp_invite_point_empty_header));
            mTvErrorHead.setVisibility(View.GONE);
            //mTvErrorBody.setText(getString(R.string.error_my_voucher_empty_body));

            mRecycler.setVisibility(View.GONE);
        } else {
            mAdapter.addItemList(dataList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("InvitePoint onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

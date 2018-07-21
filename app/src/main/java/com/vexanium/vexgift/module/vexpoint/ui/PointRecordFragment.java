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

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.bean.model.VexPointRecord;
import com.vexanium.vexgift.module.vexpoint.ui.adapter.VexPointAdapter;

import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.fragment_vexpoint)
public class PointRecordFragment extends BaseFragment {

    private RecyclerView mRecycler;

    private LinearLayoutManager linearLayoutManager;
    private VexPointAdapter mAdapter;

    @Override
    protected void initView(View fragmentRootView) {
        mRecycler = fragmentRootView.findViewById(R.id.rv_vexpoint);

        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecycler.setLayoutManager(linearLayoutManager);

        mAdapter = new VexPointAdapter(getActivity());
        mRecycler.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getActivity().getResources().getDrawable(R.drawable.shape_divider));
        mRecycler.addItemDecoration(dividerItemDecoration);

        ArrayList<VexPointRecord> dataList = new ArrayList<>();

        VexPointRecord data = new VexPointRecord("Beli Pulsa","16-08-2018 15:00 GMT",1,1500);
        VexPointRecord data1 = new VexPointRecord("Jual Pulsa","16-08-2018 15:00 GMT",0,1500);

        dataList.add(data);
        dataList.add(data1);
        dataList.add(data);
        dataList.add(data1);
        dataList.add(data);
        dataList.add(data1);
        dataList.add(data);
        dataList.add(data1);

        mAdapter.addItemList(dataList);
        mAdapter.notifyDataSetChanged();

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

    public static PointRecordFragment newInstance() {
        return new PointRecordFragment();
    }
}

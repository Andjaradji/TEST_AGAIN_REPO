package com.vexanium.vexgift.module.vexpoint.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.bean.model.VexPointRecord;

import java.util.ArrayList;

public class VexPointAdapter extends RecyclerView.Adapter<VexPointAdapter.FilterViewHolder> {

    private Context context;
    private ArrayList<VexPointRecord> dataList = new ArrayList<>();

    public VexPointAdapter(Context context) {
        this.context = context;
    }

    public VexPointAdapter(Context context, ArrayList<VexPointRecord> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public VexPointAdapter.FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vexpoint_list, parent, false);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FilterViewHolder holder, int pos) {
        VexPointRecord data = dataList.get(pos);
        holder.mVpMainText.setText(data.getVexPointLogType().getName());
        holder.mVpSubText.setText(data.getCreatedAtDate());

        if (data.getVpLogTypeId() < 3) {
            holder.mVpIndicatorText.setText("+ " + data.getAmount());
            holder.mVpIndicatorText.setTextColor(context.getResources().getColor(R.color.vexpoint_plus));
        } else {
            holder.mVpIndicatorText.setText("- " + data.getAmount());
            holder.mVpIndicatorText.setTextColor(context.getResources().getColor(R.color.vexpoint_minus));
        }
        
        if(data.getVexCounted3() > 0){
            holder.mVpVex3.setText(data.getVexCounted3()+"");
        }

        if(data.getVexCounted2() > 0){
            holder.mVpVex2.setText(data.getVexCounted2()+"");
        }
        if(data.getVexCounted1() > 0){
            holder.mVpVex1.setText(data.getVexCounted1()+"");
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mDetailContainer.getVisibility() == View.GONE){
                    holder.mDetailContainer.setVisibility(View.VISIBLE);
                }else{
                    holder.mDetailContainer.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItem(VexPointRecord item) {
        dataList.add(item);
    }

    public void addItemList(ArrayList<VexPointRecord> item) {
        dataList.addAll(item);
    }

    public void setItemList(ArrayList<VexPointRecord> itemList) {
        dataList = itemList;
    }

    public void removeAll() {
        dataList.clear();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mDetailContainer;
        LinearLayout mContainer;
        TextView mVpMainText, mVpSubText, mVpIndicatorText;
        TextView mVpVex3, mVpVex2, mVpVex1;
        private Context mContext;


        public FilterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mContainer = itemView.findViewById(R.id.ll_filter_item);
            mVpMainText = itemView.findViewById(R.id.tv_vexpoint_item_name);
            mVpSubText = itemView.findViewById(R.id.tv_vexpoint_item_detail);
            mVpIndicatorText = itemView.findViewById(R.id.tv_vexpoint_indicator);
            mDetailContainer = itemView.findViewById(R.id.rl_detail_container);
            mVpVex3 = itemView.findViewById(R.id.tv_vex3);
            mVpVex2 = itemView.findViewById(R.id.tv_vex2);
            mVpVex1 = itemView.findViewById(R.id.tv_vex1);
        }


    }
}

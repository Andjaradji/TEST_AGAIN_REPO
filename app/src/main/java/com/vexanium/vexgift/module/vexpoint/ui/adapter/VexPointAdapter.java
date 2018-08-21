package com.vexanium.vexgift.module.vexpoint.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int pos) {
        VexPointRecord data = dataList.get(pos);
        holder.mVpMainText.setText(data.getTitle());
        holder.mVpSubText.setText(data.getDescription());

        if (data.getType() == 0) {
            holder.mVpIndicatorText.setText("+ " + data.getAmount());
            holder.mVpIndicatorText.setTextColor(context.getResources().getColor(R.color.vexpoint_plus));
        } else {
            holder.mVpIndicatorText.setText("- " + data.getAmount());
            holder.mVpIndicatorText.setTextColor(context.getResources().getColor(R.color.vexpoint_minus));
        }

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

        LinearLayout mContainer;
        TextView mVpMainText, mVpSubText, mVpIndicatorText;
        private Context mContext;


        public FilterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mContainer = itemView.findViewById(R.id.ll_filter_item);
            mVpMainText = itemView.findViewById(R.id.tv_vexpoint_item_name);
            mVpSubText = itemView.findViewById(R.id.tv_vexpoint_item_detail);
            mVpIndicatorText = itemView.findViewById(R.id.tv_vexpoint_indicator);
        }


    }
}

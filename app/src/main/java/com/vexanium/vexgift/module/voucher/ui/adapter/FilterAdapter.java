package com.vexanium.vexgift.module.voucher.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vexanium.vexgift.R;

import java.util.ArrayList;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private Context context;
    private ArrayList<String> dataList = new ArrayList<>();

    public FilterAdapter(Context context) {
        this.context = context;
    }

    public FilterAdapter(Context context, ArrayList<String> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public FilterAdapter.FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int pos) {
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.mItem.setText(dataList.get(pos));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItem(String item) {
        dataList.add(item);
    }

    public void addItemList(ArrayList<String> item) {
        dataList.addAll(item);
    }

    public void removeAll() {
        dataList.clear();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mContainer;
        TextView mItem;
        private Context mContext;


        public FilterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mContainer = itemView.findViewById(R.id.ll_filter_item);
            mItem = itemView.findViewById(R.id.tv_filter);
        }


    }
}

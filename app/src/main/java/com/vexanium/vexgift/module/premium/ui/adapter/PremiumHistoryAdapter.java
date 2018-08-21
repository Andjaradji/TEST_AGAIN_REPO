package com.vexanium.vexgift.module.premium.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.bean.model.PremiumPurchase;
import com.vexanium.vexgift.module.premium.ui.helper.AdapterHistoryOnClick;

import java.util.ArrayList;

public class PremiumHistoryAdapter extends RecyclerView.Adapter<PremiumHistoryAdapter.FilterViewHolder> {

    private Context context;
    private ArrayList<PremiumPurchase> dataList = new ArrayList<>();
    private AdapterHistoryOnClick listener;

    public PremiumHistoryAdapter(Context context, AdapterHistoryOnClick listener) {
        this.context = context;
        this.listener = listener;
    }

    public PremiumHistoryAdapter(Context context, AdapterHistoryOnClick listener, ArrayList<PremiumPurchase> dataList) {
        this.context = context;
        this.listener = listener;
        this.dataList = dataList;
    }

    @Override
    public PremiumHistoryAdapter.FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_premium_purchase_history, parent, false);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int pos) {
        final PremiumPurchase data = dataList.get(pos);

        holder.mHistoryTitle.setText("PREMIUM #" + data.getId());
        holder.mHistorySubtitle.setText(data.getCreatedAtDate());
        holder.mHistoryAmount.setText(data.getPaidAmount() + " VEX");

        if (data.getStatus() == 0) {
            holder.mHistoryStatus.setText(context.getText(R.string.premium_purchase_pending));
        } else if (data.getStatus() == 1) {
            holder.mHistoryStatus.setTextColor(context.getResources().getColor(R.color.vexpoint_plus));
            holder.mHistoryStatus.setText(context.getText(R.string.premium_purchase_success));
        } else {
            holder.mHistoryStatus.setTextColor(context.getResources().getColor(R.color.vexpoint_minus));
            holder.mHistoryStatus.setText(context.getText(R.string.premium_purchase_failed));
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(data);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItem(PremiumPurchase item) {
        dataList.add(item);
    }

    public void addItemList(ArrayList<PremiumPurchase> item) {
        dataList.addAll(item);
    }

    public void removeAll() {
        dataList.clear();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mContainer;
        TextView mHistoryTitle, mHistorySubtitle, mHistoryAmount, mHistoryStatus;
        private Context mContext;


        public FilterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mContainer = itemView.findViewById(R.id.rl_purchase_history_item);
            mHistoryTitle = itemView.findViewById(R.id.tv_purchase_history_title);
            mHistorySubtitle = itemView.findViewById(R.id.tv_purchase_history_subtitle);
            mHistoryAmount = itemView.findViewById(R.id.tv_purchase_history_amount);
            mHistoryStatus = itemView.findViewById(R.id.tv_purchase_history_status);
        }


    }
}

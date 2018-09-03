package com.vexanium.vexgift.module.premium.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.bean.model.PremiumPlan;
import com.vexanium.vexgift.module.premium.ui.helper.AdapterBuyOnClick;

import java.util.ArrayList;

public class PremiumPlanAdapter extends RecyclerView.Adapter<PremiumPlanAdapter.FilterViewHolder> {

    private Context context;
    private ArrayList<PremiumPlan> dataList = new ArrayList<>();
    private AdapterBuyOnClick listener;

    public PremiumPlanAdapter(Context context, AdapterBuyOnClick listener) {
        this.context = context;
        this.listener = listener;
    }

    public PremiumPlanAdapter(Context context, AdapterBuyOnClick listener, ArrayList<PremiumPlan> dataList) {
        this.context = context;
        this.listener = listener;
        this.dataList = dataList;
    }

    @Override
    public PremiumPlanAdapter.FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_premium_member_plan, parent, false);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int pos) {
        final PremiumPlan data = dataList.get(pos);
//        int day = data.getDuration() / 24 / 3600;
        holder.mPremiumTitle.setText(data.getName());
        holder.mPremiumTitle.setSelected(true);
        holder.mPremiumSubtitle.setText(data.getPrice() + " VEX");
        holder.mBuyButton.setOnClickListener(new View.OnClickListener() {
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

    public void addItem(PremiumPlan item) {
        dataList.add(item);
    }

    public void addItemList(ArrayList<PremiumPlan> item) {
        dataList.addAll(item);
    }

    public void removeAll() {
        dataList.clear();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mBuyButton;
        TextView mPremiumTitle, mPremiumSubtitle;
        private Context mContext;


        public FilterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mBuyButton = itemView.findViewById(R.id.ll_premium_plan_buy_button);
            mPremiumTitle = itemView.findViewById(R.id.tv_premium_plan_title);
            mPremiumSubtitle = itemView.findViewById(R.id.tv_premium_plan_subtitle);
        }


    }
}

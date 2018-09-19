package com.vexanium.vexgift.module.deposit.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.bean.model.DepositOption;
import com.vexanium.vexgift.module.deposit.ui.helper.AdapterOptionOnClick;
import com.vexanium.vexgift.module.premium.ui.helper.AdapterBuyOnClick;

import java.util.ArrayList;

public class DepositOptionAdapter extends RecyclerView.Adapter<DepositOptionAdapter.FilterViewHolder> {

    private Context context;
    private ArrayList<DepositOption> dataList = new ArrayList<>();
    private AdapterOptionOnClick listener;

    public DepositOptionAdapter(Context context, AdapterOptionOnClick listener) {
        this.context = context;
        this.listener = listener;
    }

    public DepositOptionAdapter(Context context, AdapterOptionOnClick listener, ArrayList<DepositOption> dataList) {
        this.context = context;
        this.listener = listener;
        this.dataList = dataList;
    }

    @Override
    public DepositOptionAdapter.FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deposit_list, parent, false);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int pos) {
        final DepositOption data = dataList.get(pos);

        holder.mDepositTitle.setText(data.getName());

        String subtitle = data.getQuantityLeft() + " out of "+ data.getQuantityAvailable();
        holder.mDepositSubtitle.setText(subtitle);

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onItemClick(data);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItem(DepositOption item) {
        dataList.add(item);
    }

    public void addItemList(ArrayList<DepositOption> item) {
        dataList.addAll(item);
    }

    public void removeAll() {
        dataList.clear();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mContainer;
        TextView mDepositTitle, mDepositSubtitle;
        private Context mContext;


        public FilterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mContainer = itemView.findViewById(R.id.rl_deposit_list_container);
            mDepositTitle = itemView.findViewById(R.id.tv_deposit_title);
            mDepositSubtitle = itemView.findViewById(R.id.tv_deposit_subtitle);
        }


    }
}

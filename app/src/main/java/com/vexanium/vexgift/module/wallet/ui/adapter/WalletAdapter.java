package com.vexanium.vexgift.module.wallet.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.bean.model.WalletRecord;

import java.util.ArrayList;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.FilterViewHolder> {

    private Context context;
    private ArrayList<WalletRecord> dataList = new ArrayList<>();

    public WalletAdapter(Context context){
        this.context = context;
    }

    public WalletAdapter(Context context, ArrayList<WalletRecord> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public WalletAdapter.FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_record_list, parent, false);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int pos) {
        WalletRecord data = dataList.get(pos);
        holder.mWalletMainText.setText(data.getTitle());
        holder.mWalletSubText.setText(data.getDescription());

        if(data.getType() == 0) {
            holder.mWalletType.setImageResource(R.drawable.record_receive);
            holder.mWalletType.setRotation(90);
            holder.mAmountText.setText("+ "+data.getAmount()+ " VEX");
        }else{
            holder.mWalletType.setImageResource(R.drawable.record_send);
            holder.mWalletType.setRotation(-90);
            holder.mAmountText.setText("- "+data.getAmount()+ " VEX");
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItem(WalletRecord item){
        dataList.add(item);
    }

    public void addItemList(ArrayList<WalletRecord> item){
        dataList.addAll(item);
    }

    public void removeAll(){
        dataList.clear();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout mContainer;
        TextView mWalletMainText, mWalletSubText, mAmountText;
        ImageView mWalletType;
        private Context mContext;


        public FilterViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mContainer = itemView.findViewById(R.id.rl_wallet_record_item);
            mWalletMainText = itemView.findViewById(R.id.tv_wallet_record_title);
            mWalletSubText = itemView.findViewById(R.id.tv_wallet_record_subtitle);
            mAmountText = itemView.findViewById(R.id.tv_wallet_record_vex);
            mWalletType = itemView.findViewById(R.id.iv_wallet_record_type);
        }


    }
}

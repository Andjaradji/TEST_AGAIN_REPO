package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Voucher;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VouchersResponse implements Serializable {

    @JsonProperty("vouchers")
    private ArrayList<Voucher> vouchers;

    @JsonProperty("tokens")
    private ArrayList<Voucher> tokens;

    public ArrayList<Voucher> getVouchers() {
        if (vouchers == null) vouchers = new ArrayList<>();
        ArrayList<Voucher> rVoucher = new ArrayList<>();
        for(Voucher voucher : vouchers){
            if(!voucher.isToken()){
                rVoucher.add(voucher);
            }
        }
        return rVoucher;
    }

    public void setVouchers(ArrayList<Voucher> vouchers) {
        this.vouchers = vouchers;
    }

    public void updateVoucherState(int id, boolean isRedeemed) {
        if (vouchers == null) vouchers = new ArrayList<>();

        for (Voucher voucher : vouchers) {
            if (voucher.getId() == id) {
//                voucher.isRedeemed = isRedeemed;
            }
        }

    }

    public ArrayList<Voucher> getTokens() {
        if (vouchers == null) vouchers = new ArrayList<>();
        ArrayList<Voucher> rVoucher = new ArrayList<>();
        for(Voucher voucher : vouchers){
            if(voucher.isToken()){
                rVoucher.add(voucher);
            }
        }
        return rVoucher;
    }

    public void setTokens(ArrayList<Voucher> tokens) {
        this.tokens = tokens;
    }
}

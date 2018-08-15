package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.model.VoucherCode;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVouchersResponse implements Serializable{
    @JsonProperty("voucher_codes")
    private ArrayList<VoucherCode> voucherCodes;

    public ArrayList<VoucherCode> getVoucherCodes() {
        return voucherCodes;
    }

    public void setVoucherCodes(ArrayList<VoucherCode> voucherCodes) {
        this.voucherCodes = voucherCodes;
    }

    public ArrayList<VoucherCode> getActiveVoucher(){
        ArrayList<VoucherCode> activeVouchers = new ArrayList<>();
        if(voucherCodes == null) voucherCodes = new ArrayList<>();

        for (VoucherCode voucherCode : voucherCodes){
            if(!(voucherCode.getIsClaimed() == 1 || voucherCode.getVoucher().getValidUntil() < System.currentTimeMillis())){
                activeVouchers.add(voucherCode);
            }
        }

        return activeVouchers;
    }

    public ArrayList<VoucherCode> getInactiveVoucher(){
        ArrayList<VoucherCode> activeVouchers = new ArrayList<>();
        if(voucherCodes == null) voucherCodes = new ArrayList<>();

        for (VoucherCode voucherCode : voucherCodes){
            if(!(voucherCode.getIsClaimed() == 1 || voucherCode.getVoucher().getValidUntil() < System.currentTimeMillis())){
                activeVouchers.add(voucherCode);
            }
        }

        return activeVouchers;
    }
}

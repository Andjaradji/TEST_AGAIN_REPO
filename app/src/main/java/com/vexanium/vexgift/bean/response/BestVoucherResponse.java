package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.BestVoucher;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BestVoucherResponse implements Serializable{
    @JsonProperty("best_vouchers")
    private ArrayList<BestVoucher> bestVouchers;

    public ArrayList<BestVoucher> getBestVouchers() {
        return bestVouchers;
    }

    public void setBestVouchers(ArrayList<BestVoucher> bestVouchers) {
        this.bestVouchers = bestVouchers;
    }
}

package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.BestVoucher;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeaturedVoucherResponse implements Serializable {
    @JsonProperty("featured_vouchers")
    private ArrayList<BestVoucher> featuredVoucher;

    public ArrayList<BestVoucher> getFeaturedVoucher() {
        return featuredVoucher;
    }

    public void setFeaturedVoucher(ArrayList<BestVoucher> featuredVoucher) {
        this.featuredVoucher = featuredVoucher;
    }
}

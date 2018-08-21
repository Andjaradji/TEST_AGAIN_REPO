package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.VoucherType;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherTypeResponse implements Serializable {
    @JsonProperty("voucher_types")
    private ArrayList<VoucherType> voucherTypes;

    public ArrayList<VoucherType> getVoucherTypes() {
        return voucherTypes;
    }

    public void setVoucherTypes(ArrayList<VoucherType> voucherTypes) {
        this.voucherTypes = voucherTypes;
    }
}

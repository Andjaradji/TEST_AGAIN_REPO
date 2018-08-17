package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.VoucherCode;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown =  true)
public class VoucherCodeResponse implements Serializable{
    @JsonProperty("voucher_code")
    private VoucherCode voucherCode;

    public VoucherCode getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(VoucherCode voucherCode) {
        this.voucherCode = voucherCode;
    }
}

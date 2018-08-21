package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.VoucherGiftCode;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherGiftCodeResponse implements Serializable {
    @JsonProperty("voucher_gift_code")
    private VoucherGiftCode voucherGiftCode;

    public VoucherGiftCode getVoucherGiftCode() {
        return voucherGiftCode;
    }

    public void setVoucherGiftCode(VoucherGiftCode voucherGiftCode) {
        this.voucherGiftCode = voucherGiftCode;
    }
}

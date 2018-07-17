package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Voucher;

import java.io.Serializable;

public class VoucherResponse implements Serializable {

    @JsonProperty("voucher")
    private Voucher voucher;
    @JsonProperty("stock")
    private int stock;
    @JsonProperty("avail")
    private int avail;

    public int type;

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getAvail() {
        return avail;
    }

    public void setAvail(int avail) {
        this.avail = avail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public VoucherResponse(Voucher voucher, int stock, int avail, int type) {
        this.voucher = voucher;
        this.stock = stock;
        this.avail = avail;
        this.type = type;
    }

    public VoucherResponse() {

    }

    public VoucherResponse(int type) {
        this.type = type;
    }
}

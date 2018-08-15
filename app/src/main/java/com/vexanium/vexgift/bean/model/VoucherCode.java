package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherCode implements Serializable{

    @JsonProperty("id")
    private int id;
    @JsonProperty("voucher_id")
    private int voucherId;
    @JsonProperty("claimed_by")
    private int claimedBy;
    @JsonProperty("is_claimed")
    private int isClaimed;
    @JsonProperty("vendor_code_id")
    private int vendorCodeId;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("voucher")
    private Voucher voucher;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public int getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(int claimedBy) {
        this.claimedBy = claimedBy;
    }

    public int getIsClaimed() {
        return isClaimed;
    }

    public void setIsClaimed(int isClaimed) {
        this.isClaimed = isClaimed;
    }

    public int getVendorCodeId() {
        return vendorCodeId;
    }

    public void setVendorCodeId(int vendorCodeId) {
        this.vendorCodeId = vendorCodeId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}

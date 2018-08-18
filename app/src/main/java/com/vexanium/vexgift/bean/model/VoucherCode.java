package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherCode implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("voucher_id")
    private int voucherId;
    @JsonProperty("voucher_code")
    private String voucherCode;
    @JsonProperty("claimed_by")
    private int claimedBy;
    @JsonProperty("is_claimed")
    private boolean isClaimed;
    @JsonProperty("is_deactivated")
    private boolean isDeactivated;
    @JsonProperty("is_being_gifted")
    private boolean isBeingGifted;
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

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
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

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public boolean isDeactivated() {
        return isDeactivated;
    }

    public void setDeactivated(boolean deactivated) {
        isDeactivated = deactivated;
    }

    public boolean isBeingGifted() {
        return isBeingGifted;
    }

    public void setBeingGifted(boolean beingGifted) {
        isBeingGifted = beingGifted;
    }

    public String getRedeemedDate() {
        Calendar calendar = Calendar.getInstance();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date;
        try {
            date = format.parse(updatedAt);
        } catch (Exception e) {
            date = new Date();
        }
        calendar.setTime(date);

        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMMM dd, yyyy") + "  hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());

        return dateFormat.format(calendar.getTime());
    }
}

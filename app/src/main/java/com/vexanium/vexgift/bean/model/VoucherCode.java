package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    @JsonProperty("is_archived")
    private boolean isArchived;
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
    @JsonProperty("address")
    private String address;
    @JsonProperty("expired_at")
    private long expiredAt;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public String getRedeemedDate() {
//        if(TextUtils.isEmpty(updatedAt)) return "";
        return StaticGroup.getDate(updatedAt);
    }

    public long getExpiredAt() {
        return TimeUnit.SECONDS.toMillis(expiredAt);
    }

    public void setExpiredAt(long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getStrExpiredAt() {
        long l = TimeUnit.SECONDS.toMillis(expiredAt);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMMM dd, yyyy") + "  HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}

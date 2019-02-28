package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BestVoucher implements Serializable {

    public static final int BEST_VOUCHER = 1;
    public static final int BIG_BANNER = 2;

    public int type = BEST_VOUCHER;
    private BigBanner bigBanner;

    @JsonProperty("id")
    private int id;
    @JsonProperty("voucher_id")
    private int voucherId;
    @JsonProperty("priority")
    private int priority;
    @JsonProperty("created_at")
    private String createdAt;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public BigBanner getBigBanner() {
        return bigBanner;
    }

    public void setBigBanner(BigBanner bigBanner) {
        this.bigBanner = bigBanner;
    }

    public BestVoucher(int type, BigBanner bigBanner) {
        this.type = type;
        this.bigBanner = bigBanner;
    }

    public BestVoucher() {
    }
}

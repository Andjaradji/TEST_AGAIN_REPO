package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherGiftCode implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("voucher_code_id")
    private int voucherCodeId;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("code")
    private String code;
    @JsonProperty("is_claimed")
    private boolean isClaimed;
    @JsonProperty("created_at")
    private String createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoucherCodeId() {
        return voucherCodeId;
    }

    public void setVoucherCodeId(int voucherCodeId) {
        this.voucherCodeId = voucherCodeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }
}

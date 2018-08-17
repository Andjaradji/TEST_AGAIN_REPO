package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PremiumPlan implements Serializable{
    @JsonProperty("id")
    private int id;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("paid_amount")
    private int paidAmount;

    @JsonProperty("paid_currency")
    private String paidCurrency;

    @JsonProperty("paid_to")
    private String paidTo;

    @JsonProperty("paid_before")
    private int paidBefore;

    @JsonProperty("status")
    private int status;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(int paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaidCurrency() {
        return paidCurrency;
    }

    public void setPaidCurrency(String paidCurrency) {
        this.paidCurrency = paidCurrency;
    }

    public String getPaidTo() {
        return paidTo;
    }

    public void setPaidTo(String paidTo) {
        this.paidTo = paidTo;
    }

    public int getPaidBefore() {
        return paidBefore;
    }

    public void setPaidBefore(int paidBefore) {
        this.paidBefore = paidBefore;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
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
}

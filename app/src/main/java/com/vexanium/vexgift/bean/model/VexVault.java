package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VexVault implements Serializable {
    @JsonProperty("id")
    private int id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("coin_amount")
    private float coinAmount;
    @JsonProperty("coin_name")
    private String coinName;
    @JsonProperty("freeze_from")
    private long freezeFrom;
    @JsonProperty("freeze_until")
    private long freezeUntil;
    @JsonProperty("address")
    private String address;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(float coinAmount) {
        this.coinAmount = coinAmount;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public long getFreezeFrom() {
        return freezeFrom;
    }

    public void setFreezeFrom(long freezeFrom) {
        this.freezeFrom = freezeFrom;
    }

    public long getFreezeUntil() {
        return freezeUntil;
    }

    public void setFreezeUntil(long freezeUntil) {
        this.freezeUntil = freezeUntil;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

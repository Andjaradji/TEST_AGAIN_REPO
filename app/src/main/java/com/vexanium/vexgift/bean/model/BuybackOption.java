package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuybackOption implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("buy_back_id")
    private int buybackId;

    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("coin_name")
    private String coinName;
    @JsonProperty("coin_type")
    private String coinType;
    @JsonProperty("price")
    private float price;
    @JsonProperty("min_sell")
    private float minSell;
    @JsonProperty("max_sell")
    private float maxSell;
    @JsonProperty("limit_sell_per_user")
    private int limitSellPerUser;
    @JsonProperty("distribution_address_type")
    private String distributionAddressType;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getBuybackId() {
        return buybackId;
    }

    public void setBuybackId(int buybackId) {
        this.buybackId = buybackId;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getMinSell() {
        return minSell;
    }

    public void setMinSell(float minSell) {
        this.minSell = minSell;
    }

    public float getMaxSell() {
        return maxSell;
    }

    public void setMaxSell(float maxSell) {
        this.maxSell = maxSell;
    }

    public int getLimitSellPerUser() {
        return limitSellPerUser;
    }

    public void setLimitSellPerUser(int limitSellPerUser) {
        this.limitSellPerUser = limitSellPerUser;
    }

    public String getDistributionAddressType() {
        return distributionAddressType;
    }

    public void setDistributionAddressType(String distributionAddressType) {
        this.distributionAddressType = distributionAddressType;
    }
}

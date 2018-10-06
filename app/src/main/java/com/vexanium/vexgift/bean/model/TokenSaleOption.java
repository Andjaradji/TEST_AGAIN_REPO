package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenSaleOption implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("token_sale_id")
    private int tokenSaleId;
    @JsonProperty("payment_coin")
    private String paymentCoin;
    @JsonProperty("price_per_coin")
    private float pricePerCoin;
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

    public int getTokenSaleId() {
        return tokenSaleId;
    }

    public void setTokenSaleId(int tokenSaleId) {
        this.tokenSaleId = tokenSaleId;
    }

    public String getPaymentCoin() {
        return paymentCoin;
    }

    public void setPaymentCoin(String paymentCoin) {
        this.paymentCoin = paymentCoin;
    }

    public float getPricePerCoin() {
        return pricePerCoin;
    }

    public void setPricePerCoin(float pricePerCoin) {
        this.pricePerCoin = pricePerCoin;
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

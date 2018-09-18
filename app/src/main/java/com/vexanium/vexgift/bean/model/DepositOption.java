package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositOption extends BaseType {

    @JsonProperty("id")
    private int id;
    @JsonProperty("deposit_id")
    private int depositId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("coin_type")
    private String coinType;
    @JsonProperty("coin_bonus")
    private float coinBonus;
    @JsonProperty("vex_point_bonus")
    private float vexPointBonus;
    @JsonProperty("amount")
    private int amount;
    @JsonProperty("quantity_available")
    private int quantityAvailable;
    @JsonProperty("quantity_left")
    private int quantityLeft;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getDepositId() {
        return depositId;
    }

    public void setDepositId(int depositId) {
        this.depositId = depositId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public float getCoinBonus() {
        return coinBonus;
    }

    public void setCoinBonus(float coinBonus) {
        this.coinBonus = coinBonus;
    }

    public float getVexPointBonus() {
        return vexPointBonus;
    }

    public void setVexPointBonus(float vexPointBonus) {
        this.vexPointBonus = vexPointBonus;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public int getQuantityLeft() {
        return quantityLeft;
    }

    public void setQuantityLeft(int quantityLeft) {
        this.quantityLeft = quantityLeft;
    }

    @Override
    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;


}

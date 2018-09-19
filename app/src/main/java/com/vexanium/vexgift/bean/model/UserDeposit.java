package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDeposit implements Serializable {
    @JsonProperty("id")
    private int id;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("deposit_id")
    private int depositId;
    @JsonProperty("deposit_option_id")
    private int depositOptionId;
    @JsonProperty("deposit_to")
    private String depositTo;
    @JsonProperty("deposit_tx_id")
    private String depositTxId;
    @JsonProperty("transfer_before")
    private long transferBefore;
    @JsonProperty("status")
    private int status;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("deposit_option")
    private DepositOption depositOption;
    @JsonProperty("deposit")
    private Deposit deposit;

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

    public int getDepositId() {
        return depositId;
    }

    public void setDepositId(int depositId) {
        this.depositId = depositId;
    }

    public int getDepositOptionId() {
        return depositOptionId;
    }

    public void setDepositOptionId(int depositOptionId) {
        this.depositOptionId = depositOptionId;
    }

    public String getDepositTo() {
        return depositTo;
    }

    public void setDepositTo(String depositTo) {
        this.depositTo = depositTo;
    }

    public String getDepositTxId() {
        return depositTxId;
    }

    public void setDepositTxId(String depositTxId) {
        this.depositTxId = depositTxId;
    }

    public long getTransferBefore() {
        return transferBefore;
    }

    public void setTransferBefore(long transferBefore) {
        this.transferBefore = transferBefore;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public DepositOption getDepositOption() {
        return depositOption;
    }

    public void setDepositOption(DepositOption depositOption) {
        this.depositOption = depositOption;
    }

    public Deposit getDeposit() {
        return deposit;
    }

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
    }

    public String getCreatedAtDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat dateOutput = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(createdAt);
            return dateOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return createdAt;
        }
    }

}

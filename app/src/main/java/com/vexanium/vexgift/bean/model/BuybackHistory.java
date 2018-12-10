package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuybackHistory implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("buy_back_id")
    private int buybackId;
    @JsonProperty("buy_back_option_id")
    private int buybackOptionId;
    @JsonProperty("amount")
    private float amount;
    @JsonProperty("payment_to")
    private String paymentAddress;
    @JsonProperty("transfer_before")
    private int paymentDeadline;
    @JsonProperty("payment_tx_id")
    private String trxId;
    @JsonProperty("status")
    private int status;
    @JsonProperty("distribution_address")
    private String distributionAddress;
    @JsonProperty("is_distributed")
    private int isDistributed;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("buy_back_option")
    private BuybackOption buybackOption;
    @JsonProperty("buy_back")
    private Buyback buyback;

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

    public int getBuybackId() {
        return buybackId;
    }

    public void setBuybackId(int buybackId) {
        this.buybackId = buybackId;
    }

    public int getBuybackOptionId() {
        return buybackOptionId;
    }

    public void setBuybackOptionId(int buybackOptionId) {
        this.buybackOptionId = buybackOptionId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getPaymentAddress() {
        return paymentAddress;
    }

    public void setPaymentAddress(String paymentAddress) {
        this.paymentAddress = paymentAddress;
    }

    public int getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(int paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDistributionAddress() {
        return distributionAddress;
    }

    public void setDistributionAddress(String distributionAddress) {
        this.distributionAddress = distributionAddress;
    }

    public int getIsDistributed() {
        return isDistributed;
    }

    public void setIsDistributed(int isDistributed) {
        this.isDistributed = isDistributed;
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

    public BuybackOption getBuybackOption() {
        return buybackOption;
    }

    public void setBuybackOption(BuybackOption buybackOption) {
        this.buybackOption = buybackOption;
    }

    public Buyback getBuyback() {
        return buyback;
    }

    public void setBuyback(Buyback buyback) {
        this.buyback = buyback;
    }

    public String getTimeStampDate(long timeStamp) {
        long l = TimeUnit.SECONDS.toMillis(timeStamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
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

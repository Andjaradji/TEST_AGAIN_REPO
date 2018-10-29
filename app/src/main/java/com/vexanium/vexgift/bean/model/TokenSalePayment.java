package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenSalePayment implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("token_sale_id")
    private int tokenSaleId;
    @JsonProperty("token_sale_payment_option_id")
    private int tokenSalePaymentOptionId;
    @JsonProperty("amount")
    private float amount;
    @JsonProperty("payment_address")
    private String paymentAddress;
    @JsonProperty("payment_deadline")
    private int paymentDeadline;
    @JsonProperty("status")
    private int status;
    @JsonProperty("is_distributed")
    private int isDistributed;
    @JsonProperty("created_at")
    private String createdAt;

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

    public int getTokenSaleId() {
        return tokenSaleId;
    }

    public void setTokenSaleId(int tokenSaleId) {
        this.tokenSaleId = tokenSaleId;
    }

    public int getTokenSalePaymentOptionId() {
        return tokenSalePaymentOptionId;
    }

    public void setTokenSalePaymentOptionId(int tokenSalePaymentOptionId) {
        this.tokenSalePaymentOptionId = tokenSalePaymentOptionId;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getTimeStampDate(long timeStamp) {
        long l = TimeUnit.SECONDS.toMillis(timeStamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

}

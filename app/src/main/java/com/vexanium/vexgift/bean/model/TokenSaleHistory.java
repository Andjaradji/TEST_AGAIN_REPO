package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenSaleHistory implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("token_sale_id")
    private int tokenSaleId;
    @JsonProperty("token_sale_payment_option_id")
    private int tokenSalePaymentOptionId;
    @JsonProperty("amount")
    private int amount;
    @JsonProperty("payment_address")
    private String paymentAddress;
    @JsonProperty("payment_deadline")
    private int paymentDeadline;
    @JsonProperty("trx_id")
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
    @JsonProperty("token_sale_payment_option")
    private TokenSaleOption tokenSalePaymentOption;
    @JsonProperty("token_sale")
    private TokenSale tokenSale;

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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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

    public TokenSaleOption getTokenSalePaymentOption() {
        return tokenSalePaymentOption;
    }

    public void setTokenSalePaymentOption(TokenSaleOption tokenSalePaymentOption) {
        this.tokenSalePaymentOption = tokenSalePaymentOption;
    }

    public TokenSale getTokenSale() {
        return tokenSale;
    }

    public void setTokenSale(TokenSale tokenSale) {
        this.tokenSale = tokenSale;
    }

    public String getTimeStampDate(long timeStamp) {
        long l = TimeUnit.SECONDS.toMillis(timeStamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

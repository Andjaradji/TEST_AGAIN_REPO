package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenSale implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("token_name")
    private String tokenName;
    @JsonProperty("token_available")
    private float tokenAvailable;
    @JsonProperty("token_left")
    private float tokenLeft;
    @JsonProperty("start_time")
    private int startTime;
    @JsonProperty("end_time")
    private int endTime;
    @JsonProperty("base_payment_address")
    private String basePaymentAddress;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("token_sale_payment_options")
    private ArrayList<TokenSaleOption> tokenSalePaymentOptions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public float getTokenAvailable() {
        return tokenAvailable;
    }

    public void setTokenAvailable(float tokenAvailable) {
        this.tokenAvailable = tokenAvailable;
    }

    public float getTokenLeft() {
        return tokenLeft;
    }

    public void setTokenLeft(float tokenLeft) {
        this.tokenLeft = tokenLeft;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getBasePaymentAddress() {
        return basePaymentAddress;
    }

    public void setBasePaymentAddress(String basePaymentAddress) {
        this.basePaymentAddress = basePaymentAddress;
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

    public ArrayList<TokenSaleOption> getTokenSalePaymentOptions() {
        return tokenSalePaymentOptions;
    }

    public void setTokenSalePaymentOptions(ArrayList<TokenSaleOption> tokenSalePaymentOptions) {
        this.tokenSalePaymentOptions = tokenSalePaymentOptions;
    }

    public String getTimeStampDate(long timeStamp) {
        long l = TimeUnit.SECONDS.toMillis(timeStamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

}

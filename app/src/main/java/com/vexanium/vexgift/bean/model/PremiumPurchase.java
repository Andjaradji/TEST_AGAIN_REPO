package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown =  true)
public class PremiumPurchase implements Serializable{

    @JsonProperty("created_at")
    private String createdAt;

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

    @JsonProperty("status")
    private int status;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("paid_before")
    private int paidBefore;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

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

    public int getPaidBefore() {
        return paidBefore;
    }

    public void setPaidBefore(int paidBefore) {
        this.paidBefore = paidBefore;
    }

    public String getPaidBeforeDate(){
        return getDate(paidBefore);
    }

    public String getCreatedAtDate(){
        try {
            SimpleDateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat  dateOutput = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(createdAt);
            return dateOutput.format(date);
        }catch (ParseException e){
            e.printStackTrace();
            return  createdAt;
        }
    }

    public String getDate(int timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp*1000L);
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMM dd yyyy") + "  hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}

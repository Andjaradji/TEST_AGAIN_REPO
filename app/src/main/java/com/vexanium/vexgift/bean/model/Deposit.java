package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Deposit implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("duration")
    private int duration;
    @JsonProperty("coin_type")
    private String coinType;
    @JsonProperty("max_coin")
    private int maxCoin;
    @JsonProperty("coin_deposited")
    private int coinDeposited;
    @JsonProperty("limit_per_user")
    private int limitPerUser;

    @JsonProperty("note_pending")
    private String notePending;

    @JsonProperty("note_accepted")
    private String noteAccepted;

    @JsonProperty("note_rejected")
    private String noteRejected;

    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("deposit_option")
    private ArrayList<DepositOption> depositOptions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public int getMaxCoin() {
        return maxCoin;
    }

    public void setMaxCoin(int maxCoin) {
        this.maxCoin = maxCoin;
    }

    public int getCoinDeposited() {
        return coinDeposited;
    }

    public void setCoinDeposited(int coinDeposited) {
        this.coinDeposited = coinDeposited;
    }

    public int getLimitPerUser() {
        return limitPerUser;
    }

    public void setLimitPerUser(int limitPerUser) {
        this.limitPerUser = limitPerUser;
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

    public ArrayList<DepositOption> getDepositOptions() {
        return depositOptions;
    }

    public void setDepositOptions(ArrayList<DepositOption> depositOptions) {
        this.depositOptions = depositOptions;
    }

    public String getNotePending() {
        return notePending;
    }

    public void setNotePending(String notePending) {
        this.notePending = notePending;
    }

    public String getNoteAccepted() {
        return noteAccepted;
    }

    public void setNoteAccepted(String noteAccepted) {
        this.noteAccepted = noteAccepted;
    }

    public String getNoteRejected() {
        return noteRejected;
    }

    public void setNoteRejected(String noteRejected) {
        this.noteRejected = noteRejected;
    }

    public boolean isAvailable() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        long start = StaticGroup.getDateFromString(startTime);
        long end = StaticGroup.getDateFromString(endTime);

        long time = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        return (time >= start && time <= end) && (maxCoin != coinDeposited);
    }

}

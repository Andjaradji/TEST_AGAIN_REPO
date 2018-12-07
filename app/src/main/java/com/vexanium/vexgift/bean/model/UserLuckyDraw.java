package com.vexanium.vexgift.bean.model;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLuckyDraw implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("lucky_draw_id")
    private int luckyDrawId;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("is_chosen")
    private boolean isChosen;
    @JsonProperty("address")
    private String address;
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("lucky_draw")
    private LuckyDraw luckyDraw;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLuckyDrawId() {
        return luckyDrawId;
    }

    public void setLuckyDrawId(int luckyDrawId) {
        this.luckyDrawId = luckyDrawId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public LuckyDraw getLuckyDraw() {
        return luckyDraw;
    }

    public void setLuckyDraw(LuckyDraw luckyDraw) {
        this.luckyDraw = luckyDraw;
    }
}

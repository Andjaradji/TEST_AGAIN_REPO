package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Referral implements Serializable {

    @JsonProperty("id")
    private int id;

    @JsonProperty("user_id_referring")
    private int userIdReferring;

    @JsonProperty("user_id_referred")
    private int userIdReferred;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("user")
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserIdReferring() {
        return userIdReferring;
    }

    public void setUserIdReferring(int userIdReferring) {
        this.userIdReferring = userIdReferring;
    }

    public int getUserIdReferred() {
        return userIdReferred;
    }

    public void setUserIdReferred(int userIdReferred) {
        this.userIdReferred = userIdReferred;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

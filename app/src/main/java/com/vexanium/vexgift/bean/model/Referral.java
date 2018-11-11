package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

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

    @JsonProperty("is_kyc")
    private boolean isKyc;
    
    @JsonProperty("is_premium_member")
    private boolean isPremiumMember;

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

    public boolean isKyc() {
        return isKyc;
    }

    public void setKyc(boolean kyc) {
        isKyc = kyc;
    }

    public boolean isPremiumMember() {
        return isPremiumMember;
    }

    public void setPremiumMember(boolean premiumMember) {
        isPremiumMember = premiumMember;
    }
}

package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Voucher  implements Serializable {
    @JsonProperty("id")
    private String id;
    @JsonProperty("photo")
    private String photo;
    @JsonProperty("title")
    private String title;
    @JsonProperty("desc")
    private String decription;
    @JsonProperty("exp")
    private String expiredDate;

    public boolean active = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Voucher(String photo, String title, String expiredDate) {
        this.photo = photo;
        this.title = title;
        this.expiredDate = expiredDate;
    }

    public Voucher(String photo, String title, String expiredDate, boolean active) {
        this.photo = photo;
        this.title = title;
        this.expiredDate = expiredDate;
        this.active = active;
    }
}

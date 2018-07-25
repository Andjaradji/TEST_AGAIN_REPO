package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Kyc implements Serializable{

    @JsonProperty("id")
    private int id;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("identity_type")
    private String idType;
    @JsonProperty("identity_name")
    private String idName;
    @JsonProperty("identity_id")
    private String idNumber;
    @JsonProperty("identity_image_front")
    private String idImageFront;
    @JsonProperty("identity_image_back")
    private String idImageBack;
    @JsonProperty("identitiy_image_selfie")
    private String idImageSelfie;
    @JsonProperty("status")
    private String status;
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

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdImageFront() {
        return idImageFront;
    }

    public void setIdImageFront(String idImageFront) {
        this.idImageFront = idImageFront;
    }

    public String getIdImageBack() {
        return idImageBack;
    }

    public void setIdImageBack(String idImageBack) {
        this.idImageBack = idImageBack;
    }

    public String getIdImageSelfie() {
        return idImageSelfie;
    }

    public void setIdImageSelfie(String idImageSelfie) {
        this.idImageSelfie = idImageSelfie;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Banner extends BaseType {
    @JsonProperty("link")
    private String link;
    @JsonProperty("image")
    private String image;
    @JsonProperty("priority")
    private int priority;
    @JsonProperty("is_active")
    private boolean isActive;
    @JsonProperty("is_required_kyc")
    private boolean isRequiredKyc;
    @JsonProperty("is_required_verification_address")
    private boolean isRequiredVerificationAddress;
    @JsonProperty("is_required_premium")
    private boolean isRequiredPremium;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isRequiredKyc() {
        return isRequiredKyc;
    }

    public void setRequiredKyc(boolean requiredKyc) {
        isRequiredKyc = requiredKyc;
    }

    public boolean isRequiredVerificationAddress() {
        return isRequiredVerificationAddress;
    }

    public void setRequiredVerificationAddress(boolean requiredVerificationAddress) {
        isRequiredVerificationAddress = requiredVerificationAddress;
    }

    public boolean isRequiredPremium() {
        return isRequiredPremium;
    }

    public void setRequiredPremium(boolean requiredPremium) {
        isRequiredPremium = requiredPremium;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

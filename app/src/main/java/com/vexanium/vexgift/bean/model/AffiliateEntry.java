package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AffiliateEntry extends BaseType{
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("affiliate_program_id")
    private int affiliateProgramId;
    @JsonProperty("json")
    private String json;

    @JsonProperty("email")
    private String email;
    @JsonProperty("address")
    private String address;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAffiliateProgramId() {
        return affiliateProgramId;
    }

    public void setAffiliateProgramId(int affiliateProgramId) {
        this.affiliateProgramId = affiliateProgramId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

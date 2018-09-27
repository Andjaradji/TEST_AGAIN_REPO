package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.UserDeposit;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDepositSingleResponse implements Serializable {

    @JsonProperty("user_deposit")
    private UserDeposit userDeposit;

    public UserDeposit getUserDeposit() {
        return userDeposit;
    }

    public void setUserDeposit(UserDeposit userDeposit) {
        this.userDeposit = userDeposit;
    }
}

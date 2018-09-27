package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.UserDeposit;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDepositResponse implements Serializable {

    @JsonProperty("user_deposits")
    private ArrayList<UserDeposit> userDeposits;

    public ArrayList<UserDeposit> getUserDeposits() {
        return userDeposits;
    }

    public void setUserDeposits(ArrayList<UserDeposit> userDeposits) {
        this.userDeposits = userDeposits;
    }

    public UserDeposit findUserDepositById(int id) {
        for (UserDeposit userDeposit : userDeposits) {
            if (userDeposit.getId() == id) {
                return userDeposit;
            }
        }
        return null;
    }
}

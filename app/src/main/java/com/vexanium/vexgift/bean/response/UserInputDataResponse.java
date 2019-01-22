package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.UserInputData;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInputDataResponse implements Serializable{
    @JsonProperty("digifinex_referral")
    private UserInputData userInputData;

    @JsonProperty("digifinex_referrals")
    private ArrayList<UserInputData> userInputDatas;

    public UserInputData getUserInputData() {
        return userInputData;
    }

    public void setUserInputData(UserInputData userInputData) {
        this.userInputData = userInputData;
    }

    public ArrayList<UserInputData> getUserInputDatas() {
        return userInputDatas;
    }

    public void setUserInputDatas(ArrayList<UserInputData> userInputDatas) {
        this.userInputDatas = userInputDatas;
    }
}

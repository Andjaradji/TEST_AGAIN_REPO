package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.UserLuckyDraw;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLuckyDrawListResponse implements Serializable {
    @JsonProperty("user_lucky_draws")
    private ArrayList<UserLuckyDraw> userLuckyDraws;

    public ArrayList<UserLuckyDraw> getUserLuckyDraws() {
        return userLuckyDraws;
    }

    public void setUserLuckyDraws(ArrayList<UserLuckyDraw> userLuckyDraws) {
        this.userLuckyDraws = userLuckyDraws;
    }
}

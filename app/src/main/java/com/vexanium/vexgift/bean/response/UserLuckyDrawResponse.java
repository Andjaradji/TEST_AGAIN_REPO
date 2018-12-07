package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.UserLuckyDraw;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLuckyDrawResponse implements Serializable {
    @JsonProperty("user_lucky_draw")
    private UserLuckyDraw userLuckyDraw;

    public UserLuckyDraw getUserLuckyDraw() {
        return userLuckyDraw;
    }

    public void setUserLuckyDraw(UserLuckyDraw userLuckyDraw) {
        this.userLuckyDraw = userLuckyDraw;
    }
}

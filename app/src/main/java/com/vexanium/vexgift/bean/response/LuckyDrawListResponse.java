package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.BestVoucher;
import com.vexanium.vexgift.bean.model.LuckyDraw;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LuckyDrawListResponse implements Serializable {
    @JsonProperty("lucky_draws")
    private ArrayList<LuckyDraw> luckyDraws;

    public ArrayList<LuckyDraw> getLuckyDraws() {
        return luckyDraws;
    }

    public void setLuckyDraws(ArrayList<LuckyDraw> luckyDraws) {
        this.luckyDraws = luckyDraws;
    }
}

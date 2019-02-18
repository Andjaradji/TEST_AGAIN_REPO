package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.LuckyDraw;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LuckyDrawResponse implements Serializable {
    @JsonProperty("lucky_draw")
    private LuckyDraw luckyDraw;

    public LuckyDraw getLuckyDraw() {
        return luckyDraw;
    }

    public void setLuckyDraw(LuckyDraw luckyDraw) {
        this.luckyDraw = luckyDraw;
    }
}

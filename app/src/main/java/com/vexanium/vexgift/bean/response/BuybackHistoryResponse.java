package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.BuybackHistory;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuybackHistoryResponse implements Serializable {

    @JsonProperty("user_buy_backs")
    private ArrayList<BuybackHistory> buybackHistories;

    public ArrayList<BuybackHistory> getBuybackHistories() {
        return buybackHistories;
    }

    public void setBuybackHistories(ArrayList<BuybackHistory> buybackHistories) {
        this.buybackHistories = buybackHistories;
    }

    public BuybackHistory getBuybackHistoryById(int id) {
        for (BuybackHistory buybackHistory : buybackHistories) {
            if (buybackHistory.getId() == id) {
                return buybackHistory;
            }
        }
        return null;
    }
}

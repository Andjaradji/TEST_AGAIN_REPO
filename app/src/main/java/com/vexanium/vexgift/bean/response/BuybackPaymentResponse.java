package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.BuybackHistory;
import com.vexanium.vexgift.bean.model.TokenSalePayment;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuybackPaymentResponse implements Serializable {

    @JsonProperty("user_buy_back")
    private BuybackHistory buybackHistory;

    public BuybackHistory getBuybackHistory() {
        return buybackHistory;
    }

    public void setBuybackHistory(BuybackHistory buybackHistory) {
        this.buybackHistory = buybackHistory;
    }
}

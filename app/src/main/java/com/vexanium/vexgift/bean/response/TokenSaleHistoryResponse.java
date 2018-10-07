package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.TokenSale;
import com.vexanium.vexgift.bean.model.TokenSaleHistory;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenSaleHistoryResponse implements Serializable {

    @JsonProperty("token_sale_payments")
    private ArrayList<TokenSaleHistory> tokenSaleHistories;

    public ArrayList<TokenSaleHistory> getTokenSaleHistories() {
        return tokenSaleHistories;
    }

    public void setTokenSaleHistories(ArrayList<TokenSaleHistory> tokenSaleHistories) {
        this.tokenSaleHistories = tokenSaleHistories;
    }
}

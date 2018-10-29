package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.TokenSaleHistory;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenSaleHistoryDetailResponse implements Serializable {

    @JsonProperty("token_sale_payment")
    TokenSaleHistory tokenSaleHistory;

    public TokenSaleHistory getTokenSaleHistory() {
        return tokenSaleHistory;
    }

    public void setTokenSaleHistory(TokenSaleHistory tokenSaleHistory) {
        this.tokenSaleHistory = tokenSaleHistory;
    }
}

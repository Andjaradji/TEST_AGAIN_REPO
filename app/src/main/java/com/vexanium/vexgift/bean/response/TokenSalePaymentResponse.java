package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.TokenSaleHistory;
import com.vexanium.vexgift.bean.model.TokenSalePayment;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenSalePaymentResponse implements Serializable {

    @JsonProperty("token_sale_payment")
    private TokenSalePayment tokenSalePayment;

    public TokenSalePayment getTokenSalePayment() {
        return tokenSalePayment;
    }

    public void setTokenSalePayment(TokenSalePayment tokenSalePayment) {
        this.tokenSalePayment = tokenSalePayment;
    }
}

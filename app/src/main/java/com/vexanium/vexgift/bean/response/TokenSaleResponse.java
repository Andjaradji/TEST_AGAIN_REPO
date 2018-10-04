package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.TokenSale;
import com.vexanium.vexgift.bean.model.UserDeposit;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenSaleResponse implements Serializable {

    @JsonProperty("token_sales")
    private ArrayList<TokenSale> tokenSales;

    public ArrayList<TokenSale> getTokenSales() {
        return tokenSales;
    }

    public void setTokenSales(ArrayList<TokenSale> tokenSales) {
        this.tokenSales = tokenSales;
    }
}

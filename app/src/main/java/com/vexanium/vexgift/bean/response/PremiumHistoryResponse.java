package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.PremiumPurchase;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremiumHistoryResponse implements Serializable {

    @JsonProperty("premium_members")
    private ArrayList<PremiumPurchase> premiumPurchase;

    public ArrayList<PremiumPurchase> getPremiumPurchase() {
        return premiumPurchase;
    }

    public void setPremiumPurchase(ArrayList<PremiumPurchase> premiumPurchase) {
        this.premiumPurchase = premiumPurchase;
    }
}

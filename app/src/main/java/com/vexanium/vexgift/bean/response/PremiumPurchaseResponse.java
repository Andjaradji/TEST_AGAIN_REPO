package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.PremiumPurchase;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremiumPurchaseResponse implements Serializable {

    @JsonProperty("premium_member")
    private PremiumPurchase premiumPurchase;

    public PremiumPurchase getPremiumPurchase() {
        return premiumPurchase;
    }

    public void setPremiumPurchase(PremiumPurchase premiumPurchase) {
        this.premiumPurchase = premiumPurchase;
    }
}

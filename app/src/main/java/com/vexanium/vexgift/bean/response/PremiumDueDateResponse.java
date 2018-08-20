package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.PremiumPurchase;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremiumDueDateResponse implements Serializable {

    @JsonProperty("premium_until")
    private int premiumUntil;

    public int getPremiumUntil() {
        return premiumUntil;
    }

    public void setPremiumUntil(int premiumUntil) {
        this.premiumUntil = premiumUntil;
    }
}

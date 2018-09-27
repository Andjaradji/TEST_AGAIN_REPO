package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.PremiumPurchase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremiumHistoryResponse implements Serializable {

    @JsonProperty("premium_members")
    private ArrayList<PremiumPurchase> premiumPurchase;

    public ArrayList<PremiumPurchase> getPremiumPurchase() {
        return premiumPurchase;
    }

    public int getLoyaltyPoint(){
        int lp = 0;
        for(PremiumPurchase pp : premiumPurchase){
            if(pp.getStatus() == 1){
                lp += TimeUnit.SECONDS.toDays(pp.getDuration());
            }
        }
        return lp;
    }

    public void setPremiumPurchase(ArrayList<PremiumPurchase> premiumPurchase) {
        this.premiumPurchase = premiumPurchase;
    }
}

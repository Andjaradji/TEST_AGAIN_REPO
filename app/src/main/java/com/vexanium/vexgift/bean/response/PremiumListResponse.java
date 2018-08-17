package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.PremiumPlan;
import com.vexanium.vexgift.bean.model.Voucher;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremiumListResponse implements Serializable {

    @JsonProperty("premium_members")
    private ArrayList<PremiumPlan> premiumPlans;

    public ArrayList<PremiumPlan> getPremiumPlans() {
        return premiumPlans;
    }

    public void setPremiumPlans(ArrayList<PremiumPlan> premiumPlans) {
        this.premiumPlans = premiumPlans;
    }
}

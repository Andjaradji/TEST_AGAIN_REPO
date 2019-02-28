package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.ReferralSpecialEvent;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletReferralResponse implements Serializable {
    @JsonProperty("counted_referrals")
    private ArrayList<ReferralSpecialEvent> countedReferrals;

    @JsonProperty("uncounted_referrals")
    private ArrayList<ReferralSpecialEvent> uncountedReferrals;

    public ArrayList<ReferralSpecialEvent> getCountedReferrals() {
        return countedReferrals;
    }

    public void setCountedReferrals(ArrayList<ReferralSpecialEvent> countedReferrals) {
        this.countedReferrals = countedReferrals;
    }

    public ArrayList<ReferralSpecialEvent> getUncountedReferrals() {
        return uncountedReferrals;
    }

    public void setUncountedReferrals(ArrayList<ReferralSpecialEvent> uncountedReferrals) {
        this.uncountedReferrals = uncountedReferrals;
    }

    public int getCountedReferralsCount(){
        if(countedReferrals != null) return countedReferrals.size();
        return 0;
    }

    public int getUncountedReferralsCount(){
        if(uncountedReferrals != null) return uncountedReferrals.size();
        return 0;
    }

    public int getReferralsCount(){
        return  getCountedReferralsCount() + getUncountedReferralsCount();
    }
}

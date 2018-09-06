package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Referral;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserReferralResponse implements Serializable {

    @JsonProperty("referrals")
    private ArrayList<Referral> referrals;

    public ArrayList<Referral> getReferrals() {
        return referrals;
    }

    public void setReferrals(ArrayList<Referral> referrals) {
        this.referrals = referrals;
    }
}
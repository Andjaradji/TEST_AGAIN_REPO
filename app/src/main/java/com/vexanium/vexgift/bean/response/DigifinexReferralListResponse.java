package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.DigifinexReferral;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DigifinexReferralListResponse implements Serializable {

    @JsonProperty("digifinex_referrals")
    private ArrayList<DigifinexReferral> digifinexReferrals;

    public ArrayList<DigifinexReferral> getDigifinexReferrals() {
        return digifinexReferrals;
    }

    public void setDigifinexReferrals(ArrayList<DigifinexReferral> digifinexReferrals) {
        this.digifinexReferrals = digifinexReferrals;
    }
}

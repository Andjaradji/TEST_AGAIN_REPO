package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.DigifinexReferral;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DigifinexReferralResponse implements Serializable {

    @JsonProperty("digifinex_referral")
    private DigifinexReferral digifinexReferral;

    public DigifinexReferral getDigifinexReferral() {
        return digifinexReferral;
    }

    public void setDigifinexReferral(DigifinexReferral digifinexReferral) {
        this.digifinexReferral = digifinexReferral;
    }
}

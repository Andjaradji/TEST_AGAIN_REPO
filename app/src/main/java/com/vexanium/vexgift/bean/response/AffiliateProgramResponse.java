package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.AffiliateProgram;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AffiliateProgramResponse implements Serializable {
    @JsonProperty("affiliate_programs")
    private ArrayList<AffiliateProgram> affiliatePrograms;

    public ArrayList<AffiliateProgram> getAffiliatePrograms() {
        return affiliatePrograms;
    }

    public void setAffiliatePrograms(ArrayList<AffiliateProgram> affiliatePrograms) {
        this.affiliatePrograms = affiliatePrograms;
    }

    public AffiliateProgram getAffiliateProgramById(int id) {
        for (AffiliateProgram affiliateProgram : affiliatePrograms) {
            if (affiliateProgram.getId() == id) {
                return affiliateProgram;
            }
        }
        return null;
    }
}

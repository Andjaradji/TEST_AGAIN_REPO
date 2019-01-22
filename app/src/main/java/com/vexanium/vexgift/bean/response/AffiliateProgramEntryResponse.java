package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.AffiliateEntry;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AffiliateProgramEntryResponse implements Serializable {

    @JsonProperty("affiliate_entries")
    private ArrayList<AffiliateEntry> affiliateEntries;

    public ArrayList<AffiliateEntry> getAffiliateEntries() {
        return affiliateEntries;
    }

    public void setAffiliateEntries(ArrayList<AffiliateEntry> affiliateEntries) {
        this.affiliateEntries = affiliateEntries;
    }
}

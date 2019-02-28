package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.BigBanner;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BigBannerResponse implements Serializable {
    @JsonProperty("big_banners")
    private ArrayList<BigBanner> bigBanners;

    public ArrayList<BigBanner> getBigBanners() {
        return bigBanners;
    }

    public void setBigBanners(ArrayList<BigBanner> bigBanners) {
        this.bigBanners = bigBanners;
    }
}

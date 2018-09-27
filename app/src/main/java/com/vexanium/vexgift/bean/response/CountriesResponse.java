package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Country;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountriesResponse implements Serializable {
    @JsonProperty("countries")
    private ArrayList<Country> countryArrayList;

    public ArrayList<Country> getCountryArrayList() {
        return countryArrayList;
    }

    public void setCountryArrayList(ArrayList<Country> countryArrayList) {
        this.countryArrayList = countryArrayList;
    }
}

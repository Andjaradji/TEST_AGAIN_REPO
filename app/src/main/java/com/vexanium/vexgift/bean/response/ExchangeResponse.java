package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Exchange;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeResponse implements Serializable{
    @JsonProperty("exchanges")
    private ArrayList<Exchange> exchanges;

    public ArrayList<Exchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(ArrayList<Exchange> exchanges) {
        this.exchanges = exchanges;
    }
}

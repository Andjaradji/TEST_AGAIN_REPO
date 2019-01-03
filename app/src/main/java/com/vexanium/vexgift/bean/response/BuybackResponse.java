package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Buyback;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuybackResponse implements Serializable {

    @JsonProperty("buy_backs")
    private ArrayList<Buyback> buybacks;

    public ArrayList<Buyback> getBuybacks() {
        return buybacks;
    }

    public Buyback getBuybackById(int id){
        for(Buyback buyback : buybacks){
            if(buyback.getId() == id){
                return  buyback;
            }
        }
        return null;
    }

    public void setBuybacks(ArrayList<Buyback> buybacks) {
        this.buybacks = buybacks;
    }
}

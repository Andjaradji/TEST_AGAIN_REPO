package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Deposit;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositListResponse implements Serializable {

    @JsonProperty("deposits")
    private ArrayList<Deposit> deposits;

    public ArrayList<Deposit> getDeposits() {
        return deposits;
    }

    public void setDeposits(ArrayList<Deposit> deposits) {
        this.deposits = deposits;
    }

    public Deposit getDepositById(int id) {
        for (Deposit deposit : deposits) {
            if (deposit.getId() == id) return deposit;
        }
        return null;
    }
}

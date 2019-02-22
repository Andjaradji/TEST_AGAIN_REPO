package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Wallet;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletResponse implements Serializable{
    @JsonProperty("wallet")
    private Wallet wallet;

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}

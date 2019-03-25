package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.WalletWithdrawal;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WithdrawCancelResponse implements Serializable {
    @JsonProperty("wallet_withdrawal")
    private WalletWithdrawal withdrawal;

    public WalletWithdrawal getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(WalletWithdrawal withdrawal) {
        this.withdrawal = withdrawal;
    }
}

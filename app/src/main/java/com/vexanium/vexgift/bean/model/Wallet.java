package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wallet extends BaseType{
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("balance")
    private long balanca;
    @JsonProperty("address")
    private String address;

    @JsonProperty("wallet_balances")
    private ArrayList<WalletBalance> walletBalances;

    @JsonProperty("wallet_logs")
    private ArrayList<WalletLog> walletLogs;

    @JsonProperty("wallet_bonuses")
    private ArrayList<WalletBonus> walletBonuses;

    @JsonProperty("wallet_withdrawals")
    private ArrayList<WalletWithdrawal> walletWithdrawals;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getBalanca() {
        return balanca;
    }

    public void setBalanca(long balanca) {
        this.balanca = balanca;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<WalletBalance> getWalletBalances() {
        return walletBalances;
    }

    public void setWalletBalances(ArrayList<WalletBalance> walletBalances) {
        this.walletBalances = walletBalances;
    }

    public ArrayList<WalletLog> getWalletLogs() {
        return walletLogs;
    }

    public void setWalletLogs(ArrayList<WalletLog> walletLogs) {
        this.walletLogs = walletLogs;
    }

    public ArrayList<WalletBonus> getWalletBonuses() {
        return walletBonuses;
    }

    public void setWalletBonuses(ArrayList<WalletBonus> walletBonuses) {
        this.walletBonuses = walletBonuses;
    }

    public ArrayList<WalletWithdrawal> getWalletWithdrawals() {
        return walletWithdrawals;
    }

    public void setWalletWithdrawals(ArrayList<WalletWithdrawal> walletWithdrawals) {
        this.walletWithdrawals = walletWithdrawals;
    }
}

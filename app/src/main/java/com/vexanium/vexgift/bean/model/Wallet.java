package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wallet extends BaseType{
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("balance")
    private long balance;
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

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
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

    public long getWalletBalance(String type){
        if(walletBalances!=null && walletBalances.size() > 0){
            for(WalletBalance walletBalance : walletBalances){
                if(walletBalance.getType().equalsIgnoreCase(type)) return  walletBalance.getBalance();
            }
        }
        return 0;
    }

    public long getPersonalWalletBalance(){
        return  getWalletBalance("personal");
    }

    public long getExpenseWalletBalance(){
        return getWalletBalance("expense");
    }
}

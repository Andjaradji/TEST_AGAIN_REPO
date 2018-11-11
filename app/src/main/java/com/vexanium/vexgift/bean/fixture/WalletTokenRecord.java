package com.vexanium.vexgift.bean.fixture;

import java.io.Serializable;

public class WalletTokenRecord implements Serializable {

    public static int RECEIVE = 0;
    public static int SEND = 1;

    private int id;
    private int tokenId;
    private String title;
    private int type;
    private float amount;
    private String date;

    public WalletTokenRecord(int id, int tokenId, String title, int type, float amount, String date) {
        this.id = id;
        this.tokenId = tokenId;
        this.title = title;
        this.type = type;
        this.amount = amount;
        this.date = date;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
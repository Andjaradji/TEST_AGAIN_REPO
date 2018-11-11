package com.vexanium.vexgift.bean.fixture;

import java.io.Serializable;

public class WalletToken implements Serializable {
    private int id;
    private String name;
    private int resIcon;
    private float amount;
    private float estPriceInIDR;

    public WalletToken(int id, String name, int resIcon, float amount, float estPriceInIDR) {
        this.id = id;
        this.name = name;
        this.resIcon = resIcon;
        this.amount = amount;
        this.estPriceInIDR = estPriceInIDR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getEstPriceInIDR() {
        return estPriceInIDR;
    }

    public void setEstPriceInIDR(float estPriceInIDR) {
        this.estPriceInIDR = estPriceInIDR;
    }
}
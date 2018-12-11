package com.vexanium.vexgift.bean.fixture;

import java.io.Serializable;

public class Exchanger implements Serializable {
    private String name;
    private String logo;
    private String url;

    public Exchanger(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
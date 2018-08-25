package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.VexPointRecord;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VexPointResponse implements Serializable {
    @JsonProperty("vex_point")
    private float vexPoint;

    public float getVexPoint() {
        return vexPoint;
    }

    public void setVexPoint(float vexPoint) {
        this.vexPoint = vexPoint;
    }
}

package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.VexPointRecord;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VexPointRecordResponse implements Serializable {

    @JsonProperty("vex_point_logs")
    private ArrayList<VexPointRecord> vexPointLogs = new ArrayList<>();

    public ArrayList<VexPointRecord> getVexPointLogs() {
        return vexPointLogs;
    }

    public void setVexPointLogs(ArrayList<VexPointRecord> vexPointLogs) {
        this.vexPointLogs = vexPointLogs;
    }
}

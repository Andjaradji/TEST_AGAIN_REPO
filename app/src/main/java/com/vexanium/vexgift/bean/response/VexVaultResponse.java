package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.VexVault;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VexVaultResponse implements Serializable{
    @JsonProperty("vex_vaults")
    private ArrayList<VexVault> vexVaults;

    public ArrayList<VexVault> getVexVaults() {
        return vexVaults;
    }

    public void setVexVaults(ArrayList<VexVault> vexVaults) {
        this.vexVaults = vexVaults;
    }

    public float getTotalFrozen(){
        float total = 0;
        for(VexVault vexVault : vexVaults){
            total += vexVault.getCoinAmount();
        }
        return total;
    }
}

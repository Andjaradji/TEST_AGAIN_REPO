package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Setting;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingResponse implements Serializable {

    @JsonProperty("settings")
    private ArrayList<Setting> settings;

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public void setSettings(ArrayList<Setting> settings) {
        this.settings = settings;
    }

    public long getMinimumVersion(){
        if(settings != null && settings.size() > 0){
            for(Setting setting : settings){
                if(setting.getName().equalsIgnoreCase("minimum_app_version")){
                    return setting.getValue();
                }
            }
        }
        return -1;
    }

    public long getSettingValByKey(String key){
        if(settings != null && settings.size() > 0){
            for(Setting setting : settings){
                if(setting.getName().equalsIgnoreCase(key)){
                    return setting.getValue();
                }
            }
        }
        return -1;
    }
}

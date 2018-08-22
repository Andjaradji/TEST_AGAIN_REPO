package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.NotificationModel;
import com.vexanium.vexgift.bean.model.Referral;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResponse implements Serializable {

    @JsonProperty("notifications")
    private ArrayList<NotificationModel> notifications;

    public ArrayList<NotificationModel> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<NotificationModel> notifications) {
        this.notifications = notifications;
    }
}
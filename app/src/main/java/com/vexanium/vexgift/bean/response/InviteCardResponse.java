package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InviteCardResponse implements Serializable {
    @JsonProperty("list")
    public List<InviteCard> cardList;

    public static class InviteCard {
        @JsonProperty("h1")
        public String h1;

        @JsonProperty("img")
        public String imageUrl;

        @JsonProperty("h2")
        public String h2;

        @JsonProperty("h3")
        public String h3;

        @JsonProperty("h4")
        public String h4;

        public InviteCard(String h1, String imageUrl, String h2, String h3, String h4) {
            this.h1 = h1;
            this.imageUrl = imageUrl;
            this.h2 = h2;
            this.h3 = h3;
            this.h4 = h4;
        }
    }
}
package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.MemberType;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberTypeResponse implements Serializable{
    @JsonProperty("member_types")
    private ArrayList<MemberType> memberTypes;

    public ArrayList<MemberType> getMemberTypes() {
        return memberTypes;
    }

    public void setMemberTypes(ArrayList<MemberType> memberTypes) {
        this.memberTypes = memberTypes;
    }
}

package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SortFilterCondition implements Serializable {

    public static final int SORT_BY_PRICE_ASC = R.string.sort_by_price_asc;
    public static final int SORT_BY_PRICE_DESC = R.string.sort_by_price_desc;
    public static final int SORT_BY_EXPIRED_DATE_ASC = R.string.sort_by_exp_date_asc;
    public static final int SORT_BY_EXPIRED_DATE_DESC = R.string.sort_by_exp_date_desc;
    public static final int SORT_BY_RELEASE_DATE_ASC = R.string.sort_by_rel_date_asc;
    public static final int SORT_BY_RELEASE_DATE_DESC = R.string.sort_by_rel_date_desc;
    public static final int SORT_DEFAULT = SORT_BY_RELEASE_DATE_DESC;

    @JsonProperty("sort")
    private int sort;
    @JsonProperty("payment")
    private List<String> paymentTypes;
    @JsonProperty("member")
    private List<String> memberTypes;
    @JsonProperty("voucher")
    private List<String> voucherTypes;
    @JsonProperty("category")
    private List<String> categories;
    @JsonProperty("location")
    private List<String> location;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<String> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<String> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public List<String> getMemberTypes() {
        return memberTypes;
    }

    public void setMemberTypes(List<String> memberTypes) {
        this.memberTypes = memberTypes;
    }

    public List<String> getVoucherTypes() {
        return voucherTypes;
    }

    public void setVoucherTypes(List<String> voucherTypes) {
        this.voucherTypes = voucherTypes;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void resetSort() {
        sort = SORT_DEFAULT;
    }

    public void resetFilter() {
        categories = paymentTypes = memberTypes = voucherTypes = location = new ArrayList<>();
    }

    public boolean isEquallsToCondition(SortFilterCondition sortFilterCondition) {
        return getSort() == sortFilterCondition.getSort() &&
                getPaymentTypes() == sortFilterCondition.getPaymentTypes() &&
                getMemberTypes() == sortFilterCondition.getMemberTypes() &&
                getVoucherTypes() == sortFilterCondition.getVoucherTypes() &&
                getLocation() == sortFilterCondition.getLocation() &&
                getCategories() == sortFilterCondition.getCategories();
    }
}

package com.vexanium.vexgift.module.voucher.ui.select;

public class SingleSelectItem {
    public String key;
    public String title;
    public boolean isSelected;

    public SingleSelectItem(String title, boolean isSelected) {
        this.title = title;
        this.isSelected = isSelected;
    }

    public SingleSelectItem(String key, String title, boolean isSelected) {
        this.key = key;
        this.title = title;
        this.isSelected = isSelected;
    }

    public SingleSelectItem() {
    }
}

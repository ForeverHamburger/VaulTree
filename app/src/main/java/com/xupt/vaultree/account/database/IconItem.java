package com.xupt.vaultree.account.database;

public class IconItem {
    private int iconResId;
    private String iconName;

    public IconItem(int iconResId, String iconName) {
        this.iconResId = iconResId;
        this.iconName = iconName;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getIconName() {
        return iconName;
    }
}
package com.xupt.vaultree.account.database;

public class PaymentMethod {
    private final int iconResId;
    private final String name;

    public PaymentMethod(int iconResId, String name) {
        this.iconResId = iconResId;
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
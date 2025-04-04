package com.xupt.vaultree;

public class Bill {
    private Long id;
    private float amount;
    private String remark;
    private String payName;

    public int getPayIconResId() {
        return payIconResId;
    }

    public void setPayIconResId(int payIconResId) {
        this.payIconResId = payIconResId;
    }

    private int payIconResId;
    private long dateMillis;
    private boolean isIncome;
    private String categoryIconName;
    private int categoryIconResId;

    public Bill(Long id, float amount, String remark, String payName,int payIconResId,  long dateMillis, boolean isIncome, String categoryIconName, int categoryIconResId) {
        this.id = id;
        this.amount = amount;
        this.remark = remark;
        this.payName = payName;
        this.payIconResId = payIconResId;
        this.dateMillis = dateMillis;
        this.isIncome = isIncome;
        this.categoryIconName = categoryIconName;
        this.categoryIconResId = categoryIconResId;
    }


    // getter方法
    public Long getId() { return id; }
    public float getAmount() { return amount; }
    public String getRemark() { return remark; }
    public String getPayName() { return payName; }
    public long getDateMillis() { return dateMillis; }
    public boolean isIncome() { return isIncome; }
    public String getCategoryIconName() {
        return categoryIconName;
    }

    public int getCategoryIconResId() {
        return categoryIconResId;
    }
}
package com.xupt.vaultree;

import java.io.Serializable;

public class Bill implements Serializable {
    // 账单唯一标识
    private Long id;
    // 账单金额
    private float amount;
    // 账单备注
    private String remark;
    // 支付方式名称（如：现金、支付宝等）
    private String payName;
    // 支付方式图标资源ID
    private int payIconResId;
    // 账单日期（毫秒时间戳）
    private long dateMillis;
    // 是否为收入类型（true=收入，false=支出）
    private boolean isIncome;
    // 分类名称（如：餐饮、交通等）
    private String categoryIconName;
    // 分类图标资源ID
    private int categoryIconResId;

    /* 完整构造函数
     * @param id 账单ID
     * @param amount 金额
     * @param remark 备注
     * @param payName 支付方式名称
     * @param payIconResId 支付方式图标资源ID
     * @param dateMillis 日期时间戳
     * @param isIncome 是否收入
     * @param categoryIconName 分类名称
     * @param categoryIconResId 分类图标资源ID
     */
    public Bill(Long id, float amount, String remark, String payName,
                int payIconResId, long dateMillis, boolean isIncome,
                String categoryIconName, int categoryIconResId) {
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

    // ==================== Getter方法 ====================

    public Long getId() {
        return id;
    }

    public float getAmount() {
        return amount;
    }

    public String getRemark() {
        return remark;
    }

    public String getPayName() {
        return payName;
    }

    public int getPayIconResId() {
        return payIconResId;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public String getCategoryIconName() {
        return categoryIconName;
    }

    public int getCategoryIconResId() {
        return categoryIconResId;
    }

    // ==================== Setter方法 ====================


    public void setPayIconResId(int payIconResId) {
        this.payIconResId = payIconResId;
    }
}
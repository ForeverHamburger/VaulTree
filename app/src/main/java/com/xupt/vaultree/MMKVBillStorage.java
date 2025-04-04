package com.xupt.vaultree;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MMKVBillStorage {
    private static final String BILLS_KEY = "bills_list";
    private final MMKV mmkv;

    public MMKVBillStorage() {
        mmkv = MMKV.defaultMMKV();
    }

    public void saveBill(Bill bill) {
        List<Bill> bills = getAllBills();
        bills.add(bill);
        String json = new Gson().toJson(bills);
        mmkv.encode(BILLS_KEY, json);
    }

    public void updateBill(Bill bill) {
        List<Bill> bills = getAllBills();
        for (int i = 0; i < bills.size(); i++) {
            if (bills.get(i).getId().equals(bill.getId())) {
                bills.set(i, bill);
                break;
            }
        }
        String json = new Gson().toJson(bills);
        mmkv.encode(BILLS_KEY, json);
    }

    public List<Bill> getAllBills() {
        String json = mmkv.decodeString(BILLS_KEY, "[]");
        Type type = new TypeToken<List<Bill>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    public Bill getBillById(long id) {
        List<Bill> bills = getAllBills();
        for (Bill bill : bills) {
            if (bill.getId() == id) {
                return bill;
            }
        }
        return null;
    }
    public List<Bill> getBillsByDateRange(long startDate, long endDate) {
        List<Bill> allBills = getAllBills();
        List<Bill> result = new ArrayList<>();

        for (Bill bill : allBills) {
            if (bill.getDateMillis() >= startDate && bill.getDateMillis() <= endDate) {
                result.add(bill);
            }
        }
        return result;
    }
    public List<Bill> getBillsByType(boolean isIncome) {
        List<Bill> allBills = getAllBills();
        List<Bill> result = new ArrayList<>();

        for (Bill bill : allBills) {
            if (bill.isIncome() == isIncome) {
                result.add(bill);
            }
        }
        return result;
    }

    // 统计所有账单总金额
    public float getTotalAmount() {
        List<Bill> bills = getAllBills();
        float total = 0;
        for (Bill bill : bills) {
            total += bill.getAmount();
        }
        return total;
    }

    // 按类型统计总金额
    public float getTotalAmountByType(boolean isIncome) {
        List<Bill> bills = getBillsByType(isIncome);
        float total = 0;
        for (Bill bill : bills) {
            total += bill.getAmount();
        }
        return total;
    }
    public void deleteBill(long billId) {
        List<Bill> bills = getAllBills();
        for (int i = 0; i < bills.size(); i++) {
            if (bills.get(i).getId() == billId) {
                bills.remove(i);
                break;
            }
        }
        String json = new Gson().toJson(bills);
        mmkv.encode(BILLS_KEY, json);
    }

    // 清除 MMKV 所有信息的方法
    public void clearAllData() {
        mmkv.clear();
    }
}
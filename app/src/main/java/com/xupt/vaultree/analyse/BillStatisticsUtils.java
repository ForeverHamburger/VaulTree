package com.xupt.vaultree.analyse;
import com.xupt.vaultree.Bill;

import java.text.SimpleDateFormat;
import java.util.*;

public class BillStatisticsUtils {

    /**
     * 统计每个分类的资金，可以选择统计收入或支出
     * @param billList 账单列表
     * @param isIncomeOnly 是否只统计收入，true 表示只统计收入，false 表示只统计支出
     * @return 分类名称到资金总额的映射
     */
    public static Map<String, Float> calculateCategoryTotal(List<Bill> billList, boolean isIncomeOnly) {
        Map<String, Float> categoryTotalMap = new HashMap<>();
        for (Bill bill : billList) {
            // 根据 isIncomeOnly 参数过滤账单
            if ((isIncomeOnly && bill.isIncome()) || (!isIncomeOnly && !bill.isIncome())) {
                String category = bill.getCategoryIconName();
                float amount = bill.getAmount();
                categoryTotalMap.put(category, categoryTotalMap.getOrDefault(category, 0f) + amount);
            }
        }
        return categoryTotalMap;
    }

    /**
     * 获取某一天的所有账单
     * @param billList 账单列表
     * @param targetDateMillis 目标日期的毫秒时间戳
     * @return 该天的账单列表
     */
    public static List<Bill> getBillsByDate(List<Bill> billList, long targetDateMillis) {
        List<Bill> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String targetDateStr = sdf.format(new Date(targetDateMillis));
        for (Bill bill : billList) {
            String billDateStr = sdf.format(new Date(bill.getDateMillis()));
            if (billDateStr.equals(targetDateStr)) {
                result.add(bill);
            }
        }
        return result;
    }

    /**
     * 获取近七天的所有账单
     * @param billList 账单列表
     * @return 近七天的账单列表
     */
    public static List<Bill> getBillsInLastSevenDays(List<Bill> billList) {
        List<Bill> result = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        long sevenDaysAgoMillis = calendar.getTimeInMillis();
        long currentTimeMillis = System.currentTimeMillis();
        for (Bill bill : billList) {
            long billDateMillis = bill.getDateMillis();
            if (billDateMillis >= sevenDaysAgoMillis && billDateMillis <= currentTimeMillis) {
                result.add(bill);
            }
        }
        return result;
    }


    /**
     * 统计总的盈亏
     * @param billList 账单列表
     * @return 总的盈亏金额
     */
    public static float calculateTotalProfitLoss(List<Bill> billList) {
        float totalProfitLoss = 0;
        for (Bill bill : billList) {
            float amount = bill.getAmount();
            if (bill.isIncome()) {
                totalProfitLoss += amount;
            } else {
                totalProfitLoss -= amount;
            }
        }
        return totalProfitLoss;
    }

    /**
     * 统计累计记账天数
     * @param billList 账单列表
     * @return 累计记账天数
     */
    public static int calculateRecordedDays(List<Bill> billList) {
        Set<String> recordedDates = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Bill bill : billList) {
            String dateStr = sdf.format(new Date(bill.getDateMillis()));
            recordedDates.add(dateStr);
        }
        return recordedDates.size();
    }
}
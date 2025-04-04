package com.xupt.vaultree.analyse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimestampConverter {
    // 账单日期（毫秒时间戳）
    private long dateMillis;

    public TimestampConverter(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    /**
     * 将时间戳转换为指定格式的字符串
     * @param pattern 日期格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    public String convertTimestampToString(String pattern) {
        Instant instant = Instant.ofEpochMilli(dateMillis);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }
}
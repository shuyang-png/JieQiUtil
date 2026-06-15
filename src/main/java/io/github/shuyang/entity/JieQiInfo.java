package io.github.shuyang.entity;

import java.util.Arrays;
import java.util.Objects;

/**
 * 节气信息。
 * <p>
 * 包含节气的太阳黄经度数和公历日期范围。
 * 日期数组 [公历月, 最早发生日, 最晚发生日]。
 * </p>
 */
public class JieQiInfo {
    private final double longitude;
    private final int[] date;

    /**
     * @param longitude 太阳黄经（度）
     * @param date      日期范围 [月, 最早日, 最晚日]
     */
    public JieQiInfo(double longitude, int[] date) {
        this.longitude = longitude;
        this.date = date.clone();
    }

    public double getLongitude() {
        return this.longitude;
    }

    public int[] getDate() {
        return date.clone();
    }

    @Override
    public String toString() {
        return "JieQiInfo{longitude=" + longitude + ", date=" + Arrays.toString(date) + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JieQiInfo)) return false;
        JieQiInfo jieQiInfo = (JieQiInfo) o;
        return Double.compare(jieQiInfo.longitude, longitude) == 0
            && Arrays.equals(date, jieQiInfo.date);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(longitude);
        result = 31 * result + Arrays.hashCode(date);
        return result;
    }
}

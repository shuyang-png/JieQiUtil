package io.github.shuyang.entity;

import java.util.Objects;

/**
 * 三元九运计算结果。
 * <p>
 * 包含年份对应的元（上元/中元/下元）、全局运数（1-9）和元内运数（1-3）。
 * </p>
 */
public class SanYuanJiuYunResult {
    private final int year;
    private final String yuan;
    private final int yun;
    private final int yunInCurrentYuan;

    /**
     * @param year            公历年份
     * @param yuan            元名称：上元/中元/下元
     * @param yun             全局运数 (1-9)
     * @param yunInCurrentYuan 当前元内的运序号 (1-3)
     */
    public SanYuanJiuYunResult(int year, String yuan, int yun, int yunInCurrentYuan) {
        this.year = year;
        this.yuan = yuan;
        this.yun = yun;
        this.yunInCurrentYuan = yunInCurrentYuan;
    }

    public int getYear() { return year; }
    public String getYuan() { return yuan; }
    public int getYun() { return yun; }
    public int getYunInCurrentYuan() { return yunInCurrentYuan; }

    @Override
    public String toString() {
        return String.format("年份: %d, 元: %s, 运: %d, 元内运: %d", year, yuan, yun, yunInCurrentYuan);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SanYuanJiuYunResult)) return false;
        SanYuanJiuYunResult that = (SanYuanJiuYunResult) o;
        return year == that.year && yun == that.yun
            && yunInCurrentYuan == that.yunInCurrentYuan
            && Objects.equals(yuan, that.yuan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, yuan, yun, yunInCurrentYuan);
    }
}

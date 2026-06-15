package io.github.shuyang.entity;

import java.util.Objects;

/**
 * 四柱干支。
 * <p>
 * 包含年、月、日、时四个柱的天干地支，
 * 每个柱由天干（甲～癸）和地支（子～亥）组成。
 * </p>
 *
 * <p>示例：丙午年 庚寅月 庚午日 丁亥时</p>
 */
public class GanZhi {
    private String ganZhiYear;
    private String ganZhiMonth;
    private String ganZhiDay;
    private String ganZhiHour;

    /**
     * @param ganZhiYear  年柱，如 "丙午"
     * @param ganZhiMonth 月柱，如 "庚寅"
     * @param ganZhiDay   日柱，如 "庚午"
     * @param ganZhiHour  时柱，如 "丁亥"
     */
    public GanZhi(String ganZhiYear, String ganZhiMonth, String ganZhiDay, String ganZhiHour) {
        this.ganZhiYear = ganZhiYear;
        this.ganZhiMonth = ganZhiMonth;
        this.ganZhiDay = ganZhiDay;
        this.ganZhiHour = ganZhiHour;
    }

    public String getGanZhiYear() { return ganZhiYear; }
    public void setGanZhiYear(String ganZhiYear) { this.ganZhiYear = ganZhiYear; }

    public String getGanZhiMonth() { return ganZhiMonth; }
    public void setGanZhiMonth(String ganZhiMonth) { this.ganZhiMonth = ganZhiMonth; }

    public String getGanZhiDay() { return ganZhiDay; }
    public void setGanZhiDay(String ganZhiDay) { this.ganZhiDay = ganZhiDay; }

    public String getGanZhiHour() { return ganZhiHour; }
    public void setGanZhiHour(String ganZhiHour) { this.ganZhiHour = ganZhiHour; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GanZhi)) return false;
        GanZhi ganZhi = (GanZhi) o;
        return Objects.equals(ganZhiYear, ganZhi.ganZhiYear)
            && Objects.equals(ganZhiMonth, ganZhi.ganZhiMonth)
            && Objects.equals(ganZhiDay, ganZhi.ganZhiDay)
            && Objects.equals(ganZhiHour, ganZhi.ganZhiHour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ganZhiYear, ganZhiMonth, ganZhiDay, ganZhiHour);
    }

    @Override
    public String toString() {
        return ganZhiYear + "年 " + ganZhiMonth + "月 " + ganZhiDay + "日 " + ganZhiHour + "时";
    }
}

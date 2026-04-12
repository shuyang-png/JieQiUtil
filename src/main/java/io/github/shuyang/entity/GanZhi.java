package io.github.shuyang.entity;

import io.github.shuyang.util.GanZhiUtil;

public class GanZhi {
    private String ganZhiYear;
    private String ganZhiMonth;
    private String ganZhiDay;
    private String ganZhiHour;

    public GanZhi(int year, int month, int day, int hour) {
        this.ganZhiYear = GanZhiUtil.getGanZhiYear(year,month,day,hour);
        this.ganZhiMonth = GanZhiUtil.getGanZhiMonth(this.ganZhiYear,year,month,day,hour);
        this.ganZhiDay = GanZhiUtil.getGanZhiDay(year,month,day);
        this.ganZhiHour = GanZhiUtil.getGanZhiHour(this.ganZhiDay,hour);
    }

    public GanZhi(){}

    public String getGanZhiYear() {
        return ganZhiYear;
    }

    public void setGanZhiYear(String ganZhiYear) {
        this.ganZhiYear = ganZhiYear;
    }

    public String getGanZhiMonth() {
        return ganZhiMonth;
    }

    public void setGanZhiMonth(String ganZhiMonth) {
        this.ganZhiMonth = ganZhiMonth;
    }

    public String getGanZhiDay() {
        return ganZhiDay;
    }

    public void setGanZhiDay(String ganZhiDay) {
        this.ganZhiDay = ganZhiDay;
    }

    public String getGanZhiHour() {
        return ganZhiHour;
    }

    public void setGanZhiHour(String ganZhiHour) {
        this.ganZhiHour = ganZhiHour;
    }
}

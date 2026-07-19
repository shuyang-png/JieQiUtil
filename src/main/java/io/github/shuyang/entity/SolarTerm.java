package io.github.shuyang.entity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum SolarTerm {
    XIAOHAN("小寒", 285.0, 1, 5, 8, 2, Group.WINTER_TO_SUMMER),
    DAHAN("大寒", 300.0, 1, 20, 23, 2, Group.WINTER_TO_SUMMER),
    LICHUN("立春", 315.0, 2, 3, 6, 3, Group.WINTER_TO_SUMMER),
    YUSHUI("雨水", 330.0, 2, 18, 21, 3, Group.WINTER_TO_SUMMER),
    JINGZHE("惊蛰", 345.0, 3, 5, 8, 4, Group.WINTER_TO_SUMMER),
    CHUNFEN("春分", 0.0, 3, 19, 22, 4, Group.WINTER_TO_SUMMER),
    QINGMING("清明", 15.0, 4, 4, 7, 5, Group.WINTER_TO_SUMMER),
    GUYU("谷雨", 30.0, 4, 19, 22, 5, Group.WINTER_TO_SUMMER),
    LIXIA("立夏", 45.0, 5, 4, 7, 6, Group.WINTER_TO_SUMMER),
    XIAOMAN("小满", 60.0, 5, 19, 22, 6, Group.WINTER_TO_SUMMER),
    MANGZHONG("芒种", 75.0, 6, 4, 7, 7, Group.WINTER_TO_SUMMER),
    XIAZHI("夏至", 90.0, 6, 20, 23, 7, Group.SUMMER_TO_WINTER),
    XIAOSHU("小暑", 105.0, 7, 6, 9, 8, Group.SUMMER_TO_WINTER),
    DASHU("大暑", 120.0, 7, 22, 25, 8, Group.SUMMER_TO_WINTER),
    LIQIU("立秋", 135.0, 8, 7, 10, 9, Group.SUMMER_TO_WINTER),
    CHUSHU("处暑", 150.0, 8, 22, 25, 9, Group.SUMMER_TO_WINTER),
    BAILU("白露", 165.0, 9, 7, 10, 10, Group.SUMMER_TO_WINTER),
    QIUFEN("秋分", 180.0, 9, 22, 25, 10, Group.SUMMER_TO_WINTER),
    HANLU("寒露", 195.0, 10, 7, 10, 11, Group.SUMMER_TO_WINTER),
    SHUANGJIANG("霜降", 210.0, 10, 23, 25, 11, Group.SUMMER_TO_WINTER),
    LIDONG("立冬", 225.0, 11, 7, 9, 12, Group.SUMMER_TO_WINTER),
    XIAOXUE("小雪", 240.0, 11, 22, 24, 12, Group.SUMMER_TO_WINTER),
    DAXUE("大雪", 255.0, 12, 6, 9, 1, Group.SUMMER_TO_WINTER),
    DONGZHI("冬至", 270.0, 12, 21, 24, 1, Group.WINTER_TO_SUMMER);

    public enum Group {
        WINTER_TO_SUMMER, //"冬至后，夏至前"
        SUMMER_TO_WINTER  //"夏至后，冬至前"
    }

    private final String chineseName; // 节气中文名
    private final double longitude; //
    private final int gregorianMonth; // 节气所在公历月份
    private final int earliestDay; // 节气所在最早的日期
    private final int latestDay; // 节气所在最晚的日期
    private final int stemBranchMonth;
    private final Group group; // 处于哪个范围

    SolarTerm(String chineseName, double longitude, int gregorianMonth,
              int earliestDay, int latestDay, int stemBranchMonth, Group group) {
        this.chineseName = chineseName;
        this.longitude = longitude;
        this.gregorianMonth = gregorianMonth;
        this.earliestDay = earliestDay;
        this.latestDay = latestDay;
        this.stemBranchMonth = stemBranchMonth;
        this.group = group;
    }

    public String getChineseName() { return chineseName; }
    public double getLongitude() { return longitude; }
    public int getGregorianMonth() { return gregorianMonth; }
    public int getEarliestDay() { return earliestDay; }
    public int getLatestDay() { return latestDay; }
    public int getStemBranchMonth() { return stemBranchMonth; }
    public Group getGroup() { return group; }

    private static final Map<String, SolarTerm> NAME_MAP;
    private static final List<SolarTerm> WINTER_TO_SUMMER_LIST;
    private static final List<SolarTerm> SUMMER_TO_WINTER_LIST;

    static {
        Map<String, SolarTerm> map = new LinkedHashMap<>();
        for (SolarTerm term : values()) {
            map.put(term.chineseName, term);
        }
        NAME_MAP = Collections.unmodifiableMap(map);

        WINTER_TO_SUMMER_LIST = List.of(
            DONGZHI, XIAOHAN, DAHAN, LICHUN, YUSHUI, JINGZHE,
            CHUNFEN, QINGMING, GUYU, LIXIA, XIAOMAN, MANGZHONG
        );

        SUMMER_TO_WINTER_LIST = List.of(
            XIAZHI, XIAOSHU, DASHU, LIQIU, CHUSHU, BAILU,
            QIUFEN, HANLU, SHUANGJIANG, LIDONG, XIAOXUE, DAXUE
        );
    }

    public static SolarTerm fromChineseName(String name) {
        SolarTerm term = NAME_MAP.get(name);
        if (term == null) {
            throw new IllegalArgumentException("未知节气: " + name);
        }
        return term;
    }
    // 获取下一个节气
    public SolarTerm next() {
        SolarTerm[] terms = values();
        return terms[(this.ordinal() + 1) % terms.length];
    }

    // 获取上一个节气
    public SolarTerm previous() {
        SolarTerm[] terms = values();
        return terms[(this.ordinal() - 1 + terms.length) % terms.length];
    }
    public static List<SolarTerm> getWinterToSummerTerms() {
        return WINTER_TO_SUMMER_LIST;
    }

    public static List<SolarTerm> getSummerToWinterTerms() {
        return SUMMER_TO_WINTER_LIST;
    }

    public static Map<String, SolarTerm> getNameMap() {
        return NAME_MAP;
    }
}

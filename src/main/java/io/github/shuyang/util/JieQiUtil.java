package io.github.shuyang.util;

import io.github.shuyang.entity.JieQiInfo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 二十四节气工具类。
 */
public final class JieQiUtil {
    private static final ZoneOffset CHINA_ZONE_OFFSET = ZoneOffset.ofHours(8);

    public static final Map<String, JieQiInfo> jieqiAndLongitude;
    public static final List<String> WINTER_TO_SUMMER;
    public static final List<String> SUMMER_TO_WINTER;
    public static final String[][] JIEQINAME = new String[12][2];
    public static final Map<String, Integer> jieQi2month;

    static {
        Map<String, JieQiInfo> terms = new LinkedHashMap<>();
        putTerm(terms, "立春", 315.0, 2, 3, 6);
        putTerm(terms, "雨水", 330.0, 2, 18, 21);
        putTerm(terms, "惊蛰", 345.0, 3, 5, 8);
        putTerm(terms, "春分", 0.0, 3, 19, 22);
        putTerm(terms, "清明", 15.0, 4, 4, 7);
        putTerm(terms, "谷雨", 30.0, 4, 19, 22);
        putTerm(terms, "立夏", 45.0, 5, 4, 7);
        putTerm(terms, "小满", 60.0, 5, 19, 22);
        putTerm(terms, "芒种", 75.0, 6, 4, 7);
        putTerm(terms, "夏至", 90.0, 6, 20, 23);
        putTerm(terms, "小暑", 105.0, 7, 6, 9);
        putTerm(terms, "大暑", 120.0, 7, 22, 25);
        putTerm(terms, "立秋", 135.0, 8, 7, 10);
        putTerm(terms, "处暑", 150.0, 8, 22, 25);
        putTerm(terms, "白露", 165.0, 9, 7, 10);
        putTerm(terms, "秋分", 180.0, 9, 22, 25);
        putTerm(terms, "寒露", 195.0, 10, 7, 10);
        putTerm(terms, "霜降", 210.0, 10, 23, 25);
        putTerm(terms, "立冬", 225.0, 11, 7, 9);
        putTerm(terms, "小雪", 240.0, 11, 22, 24);
        putTerm(terms, "大雪", 255.0, 12, 6, 9);
        putTerm(terms, "冬至", 270.0, 12, 21, 24);
        putTerm(terms, "小寒", 285.0, 1, 5, 8);
        putTerm(terms, "大寒", 300.0, 1, 20, 23);
        jieqiAndLongitude = Collections.unmodifiableMap(terms);

        fillMonthTerms();

        Map<String, Integer> termToMonth = new HashMap<>();
        putMonth(termToMonth, "立春", 3, "雨水");
        putMonth(termToMonth, "惊蛰", 4, "春分");
        putMonth(termToMonth, "清明", 5, "谷雨");
        putMonth(termToMonth, "立夏", 6, "小满");
        putMonth(termToMonth, "芒种", 7, "夏至");
        putMonth(termToMonth, "小暑", 8, "大暑");
        putMonth(termToMonth, "立秋", 9, "处暑");
        putMonth(termToMonth, "白露", 10, "秋分");
        putMonth(termToMonth, "寒露", 11, "霜降");
        putMonth(termToMonth, "立冬", 12, "小雪");
        putMonth(termToMonth, "大雪", 1, "冬至");
        putMonth(termToMonth, "小寒", 2, "大寒");
        jieQi2month = Collections.unmodifiableMap(termToMonth);

        WINTER_TO_SUMMER = Collections.unmodifiableList(asList(
            "冬至", "小寒", "大寒", "立春", "雨水", "惊蛰",
            "春分", "清明", "谷雨", "立夏", "小满", "芒种"
        ));
        SUMMER_TO_WINTER = Collections.unmodifiableList(asList(
            "夏至", "小暑", "大暑", "立秋", "处暑", "白露",
            "秋分", "寒露", "霜降", "立冬", "小雪", "大雪"
        ));
    }

    private JieQiUtil() {
    }

    /**
     * 获取指定年份的某个节气交接时刻。
     *
     * @param year      公历年份
     * @param jieQiName 节气名称
     * @return 北京时间（UTC+8）的节气交接时刻，精确到秒
     */
    public static LocalDateTime getJieQi(int year, String jieQiName) {
        return getJieQi(year, jieQiName, CHINA_ZONE_OFFSET);
    }

    /**
     * 获取指定年份的某个节气交接时刻，并转换到指定时区偏移。
     *
     * @param year       公历年份
     * @param jieQiName  节气名称
     * @param zoneOffset 输出时区偏移
     * @return 指定时区的节气交接时刻，精确到秒
     */
    public static LocalDateTime getJieQi(int year, String jieQiName, ZoneOffset zoneOffset) {
        long utcSecond = getJieQiEpochSecond(year, jieQiName);
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(utcSecond), zoneOffset);
    }

    /**
     * 获取指定年份某节气交接时刻的 UTC 秒级时间戳。
     */
    public static long getJieQiEpochSecond(int year, String jieQiName) {
        JieQiInfo info = getJieQiInfo(jieQiName);
        int[] date = info.getDate();

        LocalDateTime startBeijing = LocalDate.of(year, date[0], date[1]).atStartOfDay();
        LocalDateTime endBeijing = LocalDate.of(year, date[0], date[2]).plusDays(1).atStartOfDay();

        long startUtc = startBeijing.toEpochSecond(CHINA_ZONE_OFFSET);
        long endUtc = endBeijing.toEpochSecond(CHINA_ZONE_OFFSET);
        return SolarCalculationEngine.binarySearchForSolarLongitude(startUtc, endUtc, info.getLongitude());
    }

    public static JieQiInfo getJieQiInfo(String jieQiName) {
        if (jieQiName == null) {
            throw new IllegalArgumentException("jieQiName must not be null");
        }
        JieQiInfo info = jieqiAndLongitude.get(jieQiName.trim());
        if (info == null) {
            throw new IllegalArgumentException("unknown solar term: " + jieQiName);
        }
        return info;
    }

    public static List<String> getJieQiNames() {
        return new ArrayList<>(jieqiAndLongitude.keySet());
    }

    /**
     * 判断节气位于冬至后夏至前，还是夏至后冬至前。
     *
     * @return 0 表示冬至后夏至前，1 表示夏至后冬至前，2 表示非法节气
     */
    public static int checkPeriod(String solarTerm) {
        if (solarTerm == null) {
            return 2;
        }
        String term = solarTerm.trim();
        if (WINTER_TO_SUMMER.contains(term)) {
            return 0;
        }
        if (SUMMER_TO_WINTER.contains(term)) {
            return 1;
        }
        return 2;
    }

    private static void putTerm(
        Map<String, JieQiInfo> terms,
        String name,
        double longitude,
        int month,
        int firstDay,
        int lastDay
    ) {
        terms.put(name, new JieQiInfo(longitude, new int[]{month, firstDay, lastDay}));
    }

    private static void fillMonthTerms() {
        JIEQINAME[0][0] = "小寒";
        JIEQINAME[0][1] = "大寒";
        JIEQINAME[1][0] = "立春";
        JIEQINAME[1][1] = "雨水";
        JIEQINAME[2][0] = "惊蛰";
        JIEQINAME[2][1] = "春分";
        JIEQINAME[3][0] = "清明";
        JIEQINAME[3][1] = "谷雨";
        JIEQINAME[4][0] = "立夏";
        JIEQINAME[4][1] = "小满";
        JIEQINAME[5][0] = "芒种";
        JIEQINAME[5][1] = "夏至";
        JIEQINAME[6][0] = "小暑";
        JIEQINAME[6][1] = "大暑";
        JIEQINAME[7][0] = "立秋";
        JIEQINAME[7][1] = "处暑";
        JIEQINAME[8][0] = "白露";
        JIEQINAME[8][1] = "秋分";
        JIEQINAME[9][0] = "寒露";
        JIEQINAME[9][1] = "霜降";
        JIEQINAME[10][0] = "立冬";
        JIEQINAME[10][1] = "小雪";
        JIEQINAME[11][0] = "大雪";
        JIEQINAME[11][1] = "冬至";
    }

    private static void putMonth(Map<String, Integer> termToMonth, String firstTerm, int month, String secondTerm) {
        termToMonth.put(firstTerm, month);
        termToMonth.put(secondTerm, month);
    }

    private static List<String> asList(String... values) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, values);
        return list;
    }
}

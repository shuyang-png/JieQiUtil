package io.github.shuyang.util;

import io.github.shuyang.entity.JieQiInfo;
import io.github.shuyang.entity.SolarCalculationEngine;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JieQiUtil {
    public static HashMap<String, JieQiInfo> jieqiAndLongitude = new HashMap<String,JieQiInfo>();
    // 预设：冬至前 -> 夏至后的节气列表（不含夏至，含冬至）1
    public static final List<String> WINTER_TO_SUMMER;
    // 夏至后 -> 冬至前的节气列表（含夏至，不含冬至）
    public static final List<String> SUMMER_TO_WINTER;
    public static String[][] JIEQINAME = new String[12][2];
    public static final HashMap<String,Integer> jieQi2month = new HashMap<String, Integer>();
    static {
        //月 最早 最晚
        jieqiAndLongitude.put("立春",new JieQiInfo(315.0,new int[]{2,3,6}));
        jieqiAndLongitude.put("雨水",new JieQiInfo(330.0,new int[]{2,18,21}));
        jieqiAndLongitude.put("惊蛰",new JieQiInfo(345.0,new int[]{3,5,8}));
        jieqiAndLongitude.put("春分",new JieQiInfo(0.0,new int[]{3,19,22}));
        jieqiAndLongitude.put("清明",new JieQiInfo(15.0,new int[]{4,4,7}));
        jieqiAndLongitude.put("谷雨",new JieQiInfo(30.0,new int[]{4,19,22}));
        jieqiAndLongitude.put("立夏",new JieQiInfo(45.0,new int[]{5,4,7}));
        jieqiAndLongitude.put("小满",new JieQiInfo(60.0,new int[]{5,19,22}));
        jieqiAndLongitude.put("芒种",new JieQiInfo(75.0,new int[]{6,4,7}));
        jieqiAndLongitude.put("夏至",new JieQiInfo(90.0,new int[]{6,20,23}));
        jieqiAndLongitude.put("小暑",new JieQiInfo(105.0,new int[]{7,6,9}));
        jieqiAndLongitude.put("大暑",new JieQiInfo(120.0,new int[]{7,22,25}));
        jieqiAndLongitude.put("立秋",new JieQiInfo(135.0,new int[]{8,7,10}));
        jieqiAndLongitude.put("处暑",new JieQiInfo(150.0,new int[]{8,22,25}));
        jieqiAndLongitude.put("白露",new JieQiInfo(165.0,new int[]{9,7,10}));
        jieqiAndLongitude.put("秋分",new JieQiInfo(180.0,new int[]{9,22,25}));
        jieqiAndLongitude.put("寒露",new JieQiInfo(195.0,new int[]{10,7,10}));
        jieqiAndLongitude.put("霜降",new JieQiInfo(210.0,new int[]{10,23,25}));
        jieqiAndLongitude.put("立冬",new JieQiInfo(225.0,new int[]{11,7,9}));
        jieqiAndLongitude.put("小雪",new JieQiInfo(240.0,new int[]{11,22,24}));
        jieqiAndLongitude.put("大雪",new JieQiInfo(255.0,new int[]{12,6,9}));
        jieqiAndLongitude.put("冬至",new JieQiInfo(270.0,new int[]{12,21,24}));
        jieqiAndLongitude.put("小寒",new JieQiInfo(285.0,new int[]{1,5,8}));
        jieqiAndLongitude.put("大寒",new JieQiInfo(300.0,new int[]{1,20,23}));
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
        jieQi2month.put("立春",3);
        jieQi2month.put("雨水",3);
        jieQi2month.put("惊蛰",4);
        jieQi2month.put("春分",4);
        jieQi2month.put("清明",5);
        jieQi2month.put("谷雨",5);
        jieQi2month.put("立夏",6);
        jieQi2month.put("小满",6);
        jieQi2month.put("芒种",7);
        jieQi2month.put("夏至",7);
        jieQi2month.put("小暑",8);
        jieQi2month.put("大暑",8);
        jieQi2month.put("立秋",9);
        jieQi2month.put("处暑",9);
        jieQi2month.put("白露",10);
        jieQi2month.put("秋分",10);
        jieQi2month.put("寒露",11);
        jieQi2month.put("霜降",11);
        jieQi2month.put("立冬",12);
        jieQi2month.put("小雪",12);
        jieQi2month.put("大雪",1);
        jieQi2month.put("冬至",1);
        jieQi2month.put("小寒",2);
        jieQi2month.put("大寒",2);

        // 初始化冬至后夏至前的节气列表
        WINTER_TO_SUMMER = new ArrayList<>();
        WINTER_TO_SUMMER.add("冬至");
        WINTER_TO_SUMMER.add("小寒");
        WINTER_TO_SUMMER.add("大寒");
        WINTER_TO_SUMMER.add("立春");
        WINTER_TO_SUMMER.add("雨水");
        WINTER_TO_SUMMER.add("惊蛰");
        WINTER_TO_SUMMER.add("春分");
        WINTER_TO_SUMMER.add("清明");
        WINTER_TO_SUMMER.add("谷雨");
        WINTER_TO_SUMMER.add("立夏");
        WINTER_TO_SUMMER.add("小满");
        WINTER_TO_SUMMER.add("芒种");

        // 初始化夏至后冬至前的节气列表
        SUMMER_TO_WINTER = new ArrayList<>();
        SUMMER_TO_WINTER.add("夏至");
        SUMMER_TO_WINTER.add("小暑");
        SUMMER_TO_WINTER.add("大暑");
        SUMMER_TO_WINTER.add("立秋");
        SUMMER_TO_WINTER.add("处暑");
        SUMMER_TO_WINTER.add("白露");
        SUMMER_TO_WINTER.add("秋分");
        SUMMER_TO_WINTER.add("寒露");
        SUMMER_TO_WINTER.add("霜降");
        SUMMER_TO_WINTER.add("立冬");
        SUMMER_TO_WINTER.add("小雪");
        SUMMER_TO_WINTER.add("大雪");
    }

    /**
     * 检查节气在夏至后还是冬至后
     * @param solarTerm
     * @return
     */
    public static int checkPeriod(String solarTerm) {
        //冬至后，夏至前 = 0，夏至后，冬至前 = 1
        // 去除首尾空格，避免输入多余空格导致判断错误
        String term = solarTerm.trim();

        // 检查是否在冬至后夏至前区间
        if (WINTER_TO_SUMMER.contains(term)) {
            System.out.println("「" + term + "」属于：冬至后，夏至前");
            return 0;
        }
        // 检查是否在夏至后冬至前区间
        else if (SUMMER_TO_WINTER.contains(term)) {
            System.out.println("「" + term + "」属于：夏至后，冬至前");
            return 1;
        }
        // 输入错误的情况
        else {
            List<String> allTerms = new ArrayList<>();
            allTerms.addAll(WINTER_TO_SUMMER);
            allTerms.addAll(SUMMER_TO_WINTER);
            System.out.println("输入错误！\"" + term + "\" 不是24节气。\n24节气完整列表：" + allTerms);
            return 2;
        }
    }
    /**
     * 获取指定年份的某个节气日期（误差大概10分钟左右，北京时间）
     *
     * @param year 年份（如 2024）
     * @return 立春的日期时间（北京时间）
     */
    public static LocalDateTime getJieQi(int year, String jieQiName) {
        // 立春对应的太阳黄经（度）
        double TARGET_LONGITUDE = jieqiAndLongitude.get(jieQiName).getLongitude();

        JieQiInfo TARGET_INFO = jieqiAndLongitude.get(jieQiName);
        // 立春通常发生在2月3日或4日，我们以这个范围作为搜索起点
        // 定义一个足够大的范围，确保包含立春时刻
        LocalDateTime startSearch = LocalDate.of(year, TARGET_INFO.getDate()[0], TARGET_INFO.getDate()[1]).atStartOfDay();
        LocalDateTime endSearch = LocalDate.of(year, TARGET_INFO.getDate()[0], TARGET_INFO.getDate()[2]).atStartOfDay();


        // 将时间转换为秒级时间戳进行二分搜索
        long startSecs = startSearch.atZone(ZoneOffset.UTC).toEpochSecond();
        long endSecs = endSearch.atZone(ZoneOffset.UTC).toEpochSecond();

        // 调用函数
        long targetSecs = SolarCalculationEngine.binarySearchForSolarLongitude(startSecs, endSecs, TARGET_LONGITUDE);

        // 将找到的UTC时间戳转换回 LocalDateTime (UTC)
        LocalDateTime utcTime = Instant.ofEpochSecond(targetSecs).atZone(ZoneOffset.UTC).toLocalDateTime();

        // 转换为北京时间 (UTC+8)
        return utcTime.plusHours(8);
    }
}


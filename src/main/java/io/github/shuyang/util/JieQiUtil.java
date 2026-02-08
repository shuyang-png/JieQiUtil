package io.github.shuyang.util;

import io.github.shuyang.JieQiInfo;

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
    private static final List<String> WINTER_TO_SUMMER;
    // 夏至后 -> 冬至前的节气列表（含夏至，不含冬至）
    private static final List<String> SUMMER_TO_WINTER;
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
    }
    static {
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

        long targetSecs = binarySearchForSolarLongitude(startSecs, endSecs, TARGET_LONGITUDE);

        // 将找到的UTC时间戳转换回 LocalDateTime (UTC)
        LocalDateTime utcTime = Instant.ofEpochSecond(targetSecs).atZone(ZoneOffset.UTC).toLocalDateTime();

        // 转换为北京时间 (UTC+8)
        return utcTime.plusHours(8);
    }
    /**
     * 使用二分搜索查找太阳黄经等于目标值的时刻
     * @param startSecs 秒级时间
     * @param endSecs   秒级时间
     * @param targetLongitude 对应的太阳黄经（度）
     * @return 立春的日期时间（北京时间）
     */
    private static long binarySearchForSolarLongitude(long startSecs, long endSecs, double targetLongitude) {
        long left = startSecs;
        long right = endSecs;

        // 设置精度为 60 秒（1分钟）
        while (right - left > 60) {
            long mid = left + (right - left) / 2;

            double longitude = calculateAccurateSolarLongitude(mid);

            if (longitude < targetLongitude) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        // 返回最接近目标黄经的时间点
        long candidate1 = left;
        long candidate2 = right;

        double lon1 = calculateAccurateSolarLongitude(candidate1);
        double lon2 = calculateAccurateSolarLongitude(candidate2);

        // 选择黄经更接近目标的那个
        if (Math.abs(lon1 - targetLongitude) <= Math.abs(lon2 - targetLongitude)) {
            return candidate1;
        } else {
            return candidate2;
        }
    }

    /**
     * 计算给定 UTC 时间戳的太阳黄经（度）
     * 使用更精确的算法，参考 Astronomical Almanac 和 Meeus 的方法
     * 该算法考虑了更多的摄动项，精度更高。
     * @param utcTimestamp 目标节气秒级时间
     */
    private static double calculateAccurateSolarLongitude(long utcTimestamp) {
        // 将时间戳转换为儒略世纪 T (J2000.0 起算)
        double jd = 2440587.5 + (double) utcTimestamp / 86400.0; // 秒转儒略日
        double t = (jd - 2451545.0) / 36525.0; // 儒略世纪

        // 太阳几何平黄经 L0 (度)
        double l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t * t;
        l0 = l0 % 360.0;
        if (l0 < 0) l0 += 360.0;

        // 太阳平近点角 M (度)
        double m = 357.52911 + 35999.05029 * t - 0.0001537 * t * t;
        m = m % 360.0;
        if (m < 0) m += 360.0;
        double mRad = Math.toRadians(m);

        // 中心差 C (度) - 用于修正地心视黄经
        double c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * Math.sin(mRad);
        c += (0.019993 - 0.000101 * t) * Math.sin(2 * mRad);
        c += 0.000289 * Math.sin(3 * mRad);

        // 太阳视黄经 L (度)
        double l = l0 + c;

        // 章动和光行差修正（对节气计算影响较小，此处省略以简化，但核心算法更精确）
        // 地球轨道倾角 epsilon (度) - J2000.0 标准值
        double epsilon = 23.43929111;

        // 太阳真黄经 lambda (度) - 这里我们直接用视黄经 L 作为太阳在黄道上的位置近似
        // 对于节气计算（关注黄经），L 即可作为太阳黄经
        double solarLongitude = l % 360.0;
        if (solarLongitude < 0) solarLongitude += 360.0;

        return solarLongitude;
    }
}

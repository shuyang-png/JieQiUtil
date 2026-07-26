package io.github.shuyang.service;

import io.github.shuyang.entity.SolarCalculator;
import io.github.shuyang.entity.SolarCalculationEngine;
import io.github.shuyang.entity.SolarTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 节气计算服务。
 * <p>
 * 用于计算 24 节气发生的精确时间（北京时间），
 * 以及判断节气所属的季节分组（冬至→夏至 / 夏至→冬至）。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * JieQiService service = new JieQiService();
 * LocalDateTime lichun = service.getJieQi(2026, "立春");
 * LocalDateTime lichun2 = service.getJieQi(2026, SolarTerm.LICHUN);
 * }</pre>
 */
public class JieQiService {
    private static final Logger log = LoggerFactory.getLogger(JieQiService.class);

    private final SolarCalculator solarCalculator;

    /**
     * 使用默认的太阳黄经算法（{@link SolarCalculationEngine}）创建服务实例。
     */
    public JieQiService() {
        this(new SolarCalculator() {
            @Override
            public long binarySearchForSolarLongitude(long startSecs, long endSecs, double targetLongitude) {
                return SolarCalculationEngine.binarySearchForSolarLongitude(startSecs, endSecs, targetLongitude);
            }

            @Override
            public double calculateAccurateSolarLongitude(long utcTimestamp) {
                return SolarCalculationEngine.calculateAccurateSolarLongitude(utcTimestamp);
            }
        });
    }

    /**
     * 使用自定义的太阳黄经算法创建服务实例，便于测试时注入 Mock。
     */
    public JieQiService(SolarCalculator solarCalculator) {
        this.solarCalculator = solarCalculator;
    }

    /**
     * 使用高精度 VSOP87 引擎（{@link io.github.shuyang.util.SolarCalculationEngine}）创建服务实例。
     * <p>
     * 相比默认引擎（{@link SolarCalculationEngine}），额外包含章动、光行差、FK5 参考系修正，
     * 节气时刻精度从约 1 分钟提升到约 1 秒。
     * </p>
     *
     * @return 使用高精度算法引擎的服务实例
     */
    public static JieQiService createHighPrecision() {
        return new JieQiService(new SolarCalculator() {
            @Override
            public long binarySearchForSolarLongitude(long startSecs, long endSecs, double targetLongitude) {
                return io.github.shuyang.util.SolarCalculationEngine.binarySearchForSolarLongitude(
                    startSecs, endSecs, targetLongitude);
            }

            @Override
            public double calculateAccurateSolarLongitude(long utcTimestamp) {
                return io.github.shuyang.util.SolarCalculationEngine.apparentSolarLongitude(utcTimestamp);
            }
        });
    }

    /**
     * 获取指定年份某个节气的精确时间（北京时间）。
     *
     * @param year      公历年份
     * @param jieQiName 节气中文名称，如 "立春"
     * @return 节气发生的本地时间（北京时间，UTC+8）
     */
    public LocalDateTime getJieQi(int year, String jieQiName) {
        SolarTerm term = SolarTerm.fromChineseName(jieQiName);
        return getJieQi(year, term);
    }

    /**
     * 获取指定年份某个节气的精确时间（北京时间）。
     * <p>
     * 算法：
     * 1. 根据节气公历日期范围确定搜索区间
     * 2. 使用二分搜索在时间戳范围内查找目标太阳黄经
     * 3. 将找到的 UTC 时间加 8 小时转换为北京时间
     * </p>
     *
     * @param year 公历年份
     * @param term 节气枚举
     * @return 节气发生的本地时间（北京时间，UTC+8）
     */
    public LocalDateTime getJieQi(int year, SolarTerm term) {
        double targetLongitude = term.getLongitude();
        /* 根据节气的历史最早/最晚发生日期确定搜索区间 */
        LocalDateTime startSearch = LocalDate.of(year, term.getGregorianMonth(), term.getEarliestDay()).atStartOfDay();
        LocalDateTime endSearch = LocalDate.of(year, term.getGregorianMonth(), term.getLatestDay()).atStartOfDay();
        /* 转为 UTC 时间戳进行数值计算 */
        long startSecs = startSearch.atZone(ZoneOffset.UTC).toEpochSecond();
        long endSecs = endSearch.atZone(ZoneOffset.UTC).toEpochSecond();
        /* 二分搜索太阳黄经等于目标值的时刻，精度 60 秒 */
        long targetSecs = solarCalculator.binarySearchForSolarLongitude(startSecs, endSecs, targetLongitude);
        /* 将 UTC 时间戳转回北京时间 (UTC+8) */
        LocalDateTime utcTime = Instant.ofEpochSecond(targetSecs).atZone(ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime beijingTime = utcTime.plusHours(8);
        log.debug("节气计算: {} {} -> {}", year, term.getChineseName(), beijingTime);
        return beijingTime;
    }

    /**
     * 判断指定节气所属的季节分组。
     * <p>
     * 24 节气按季节分为两组：
     * <ul>
     *   <li>冬至→夏至：冬至、小寒、大寒、立春、雨水、惊蛰、春分、清明、谷雨、立夏、小满、芒种</li>
     *   <li>夏至→冬至：夏至、小暑、大暑、立秋、处暑、白露、秋分、寒露、霜降、立冬、小雪、大雪</li>
     * </ul>
     * </p>
     *
     * @param solarTermName 节气中文名称
     * @return 季节分组枚举
     */
    public SolarTerm.Group checkPeriod(String solarTermName) {
        SolarTerm term = SolarTerm.fromChineseName(solarTermName);
        log.info("「{}」属于：{}", term.getChineseName(),
            term.getGroup() == SolarTerm.Group.WINTER_TO_SUMMER ? "冬至后，夏至前" : "夏至后，冬至前");
        return term.getGroup();
    }

    /**
     * 查询指定公历时间所处的节气（便捷重载，分钟按 0 处理）。
     *
     * @see #getCurrentJieQi(int, int, int, int, int)
     */
    public SolarTerm getCurrentJieQi(int year, int month, int day, int hour) {
        return getCurrentJieQi(year, month, day, hour, 0);
    }

    /**
     * 查询指定公历时间所处的节气（便捷重载，接受 LocalDateTime）。
     *
     * @see #getCurrentJieQi(int, int, int, int, int)
     */
    public SolarTerm getCurrentJieQi(LocalDateTime dateTime) {
        return getCurrentJieQi(
            dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(),
            dateTime.getHour(), dateTime.getMinute()
        );
    }

    /**
     * 查询指定公历时间所处的节气。
     * <p>
     * 算法：
     * 1. 通过公历月份锁定当月的 2 个节气
     * 2. 比较日期与节气的 earliestDay / latestDay 确定所属节气
     * 3. 仅在日期落入 earliestDay～latestDay 模糊窗口时才进行天文计算
     * </p>
     *
     * @param year   公历年份
     * @param month  公历月份 (1-12)
     * @param day    公历日期
     * @param hour   小时 (0-23)
     * @param minute 分钟 (0-59)
     * @return 该时刻所处的节气枚举
     */
    public SolarTerm getCurrentJieQi(int year, int month, int day, int hour, int minute) {
        /* 获取当前月份的两个节气（按月内日期升序排列） */
        List<SolarTerm> terms = getTermsByMonth(month);
        SolarTerm term1 = terms.get(0);
        SolarTerm term2 = terms.get(1);

        /*
         * 日期的四种情况：
         * A) day < term1.earliestDay  → 还差几天才到 term1 → 返回上一个节气
         * B) day 在 term1 和 term2 之间 → 返回 term1
         * C) day > term2.latestDay    → term2 已过 → 返回 term2
         * D) day 在 earliestDay～latestDay 模糊窗口内 → 精确计算后判断
         */
        if (day < term1.getEarliestDay()) {
            /* A: 尚未到本月第一个节气，属于上一个节气 */
            return term1.previous();
        }
        if (day <= term1.getLatestDay()) {
            /* D: 在 term1 模糊窗口内，需要精确判断 */
            return resolveByPreciseTime(year, month, day, hour, minute, term1);
        }
        if (day < term2.getEarliestDay()) {
            /* B: 在 term1 和 term2 之间，明确属于 term1 */
            return term1;
        }
        if (day <= term2.getLatestDay()) {
            /* D: 在 term2 模糊窗口内，需要精确判断 */
            return resolveByPreciseTime(year, month, day, hour, minute, term2);
        }
        /* C: term2 已过，属于 term2 */
        return term2;
    }

    /**
     * 在日期落入节气最早/最晚日的模糊窗口时，通过天文计算精确判断。
     * <p>
     * 若给定时间在目标节气发生之前，则属于上一个节气；
     * 否则属于目标节气本身。
     * </p>
     */
    private SolarTerm resolveByPreciseTime(int year, int month, int day, int hour, int minute,
                                           SolarTerm targetTerm) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute);
        LocalDateTime termTime = getJieQi(year, targetTerm);
        if (dateTime.isBefore(termTime)) {
            return targetTerm.previous();
        }
        return targetTerm;
    }

    /**
     * 获取指定公历月份内的两个节气，按日期升序排列。
     */
    private List<SolarTerm> getTermsByMonth(int gregorianMonth) {
        List<SolarTerm> result = new ArrayList<>();
        for (SolarTerm term : SolarTerm.values()) {
            if (term.getGregorianMonth() == gregorianMonth) {
                result.add(term);
            }
        }
        result.sort(Comparator.comparingInt(SolarTerm::getEarliestDay));
        return result;
    }

    public static List<SolarTerm> getWinterToSummerTerms() {
        return SolarTerm.getWinterToSummerTerms();
    }

    public static List<SolarTerm> getSummerToWinterTerms() {
        return SolarTerm.getSummerToWinterTerms();
    }
}

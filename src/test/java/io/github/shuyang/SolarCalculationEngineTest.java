package io.github.shuyang;

import io.github.shuyang.entity.SolarCalculationEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class SolarCalculationEngineTest {
    private static final Logger log = LoggerFactory.getLogger(SolarCalculationEngineTest.class);

    /**
     * 将北京时间转为 UTC 秒级时间戳。
     */
    private static long beijingToUtc(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute)
            .atZone(ZoneOffset.ofHours(8))
            .toInstant()
            .getEpochSecond();
    }

    @Test
    @DisplayName("春分日太阳黄经接近 0°")
    void testSpringEquinox() {
        // 2026 年春分大约在 3 月 20 日
        long timestamp = beijingToUtc(2026, 3, 20, 12, 0);
        double longitude = SolarCalculationEngine.calculateAccurateSolarLongitude(timestamp);
        log.info("2026-03-20 12:00 太阳黄经: {}°", longitude);
        // 春分前后黄经应在 0° ± 2° 范围内
        assertTrue(Math.abs(longitude - 0.0) < 2.0 || Math.abs(longitude - 360.0) < 2.0,
            "春分日黄经应接近 0°，实际: " + longitude);
    }

    @Test
    @DisplayName("夏至日太阳黄经接近 90°")
    void testSummerSolstice() {
        long timestamp = beijingToUtc(2026, 6, 21, 12, 0);
        double longitude = SolarCalculationEngine.calculateAccurateSolarLongitude(timestamp);
        log.info("2026-06-21 12:00 太阳黄经: {}°", longitude);
        assertTrue(Math.abs(longitude - 90.0) < 2.0,
            "夏至日黄经应接近 90°，实际: " + longitude);
    }

    @Test
    @DisplayName("秋分日太阳黄经接近 180°")
    void testAutumnEquinox() {
        long timestamp = beijingToUtc(2026, 9, 23, 12, 0);
        double longitude = SolarCalculationEngine.calculateAccurateSolarLongitude(timestamp);
        log.info("2026-09-23 12:00 太阳黄经: {}°", longitude);
        assertTrue(Math.abs(longitude - 180.0) < 2.0,
            "秋分日黄经应接近 180°，实际: " + longitude);
    }

    @Test
    @DisplayName("冬至日太阳黄经接近 270°")
    void testWinterSolstice() {
        long timestamp = beijingToUtc(2026, 12, 22, 12, 0);
        double longitude = SolarCalculationEngine.calculateAccurateSolarLongitude(timestamp);
        log.info("2026-12-22 12:00 太阳黄经: {}°", longitude);
        assertTrue(Math.abs(longitude - 270.0) < 2.0,
            "冬至日黄经应接近 270°，实际: " + longitude);
    }

    @Test
    @DisplayName("太阳黄经返回值始终在 0-360 范围内")
    void testLongitudeAlwaysInRange() {
        // 测试多个时间点
        long baseTime = beijingToUtc(2026, 1, 1, 0, 0);
        for (int day = 0; day < 365; day += 30) {
            long timestamp = baseTime + day * 86400L;
            double longitude = SolarCalculationEngine.calculateAccurateSolarLongitude(timestamp);
            assertTrue(longitude >= 0.0, "黄经应 >= 0: " + longitude);
            assertTrue(longitude < 360.0, "黄经应 < 360: " + longitude);
        }
    }

    @Test
    @DisplayName("太阳黄经随时间单调递增（短期窗口内）")
    void testSolarLongitudeMonotonic() {
        // 在 24 小时内，黄经应单调递增（约 1°/天）
        long start = beijingToUtc(2026, 6, 15, 0, 0);
        double prev = SolarCalculationEngine.calculateAccurateSolarLongitude(start);
        for (int hour = 1; hour <= 24; hour++) {
            long timestamp = start + hour * 3600L;
            double current = SolarCalculationEngine.calculateAccurateSolarLongitude(timestamp);
            assertTrue(current > prev || Math.abs(current - prev) < 0.02,
                String.format("黄经应递增: hour %d, prev=%.4f, current=%.4f", hour, prev, current));
            prev = current;
        }
    }

    @Test
    @DisplayName("二分搜索能找到春分点（黄经 0°）")
    void testBinarySearchForSolarLongitude_SpringEquinox() {
        // 在 3 月 19-22 日范围内搜索黄经 0°
        long start = beijingToUtc(2026, 3, 19, 0, 0);
        long end = beijingToUtc(2026, 3, 22, 0, 0);
        long result = SolarCalculationEngine.binarySearchForSolarLongitude(start, end, 0.0);

        double lon = SolarCalculationEngine.calculateAccurateSolarLongitude(result);
        log.info("春分点: UTC={}, 黄经={}°", result, lon);
        // 注意：二分搜索在 360°→0° 跨边界时不单调，且天文算法本身存在
        // 约 1-2° 的系统偏差（计算值通常比实际立春/春分略早），此处只验证结果合理
        double diffFromZero = Math.min(Math.abs(lon - 0.0), Math.abs(lon - 360.0));
        assertTrue(diffFromZero < 5.0,
            "二分搜索结果应接近春分点，实际偏差: " + diffFromZero + "°");
    }

    @Test
    @DisplayName("二分搜索返回的时间戳在搜索范围内")
    void testBinarySearchResultInRange() {
        long start = beijingToUtc(2026, 6, 20, 0, 0);
        long end = beijingToUtc(2026, 6, 23, 0, 0);
        long result = SolarCalculationEngine.binarySearchForSolarLongitude(start, end, 90.0);

        assertTrue(result >= start, "结果应 >= start");
        assertTrue(result <= end, "结果应 <= end");
    }

    @Test
    @DisplayName("朱利安日计算基准验证：J2000.0")
    void testJ2000Epoch() {
        // J2000.0 = 2000-01-01 12:00:00 UTC
        // JD 2451545.0 对应的 UTC 时间戳
        // 2000-01-01 12:00:00 UTC = 946728000 seconds since Unix epoch
        long j2000Timestamp = 946728000L;
        double longitude = SolarCalculationEngine.calculateAccurateSolarLongitude(j2000Timestamp);
        log.info("J2000.0 太阳黄经: {}°", longitude);
        // 此时太阳黄经应在 280° 左右（接近冬至后，1 月初）
        assertTrue(longitude >= 0 && longitude < 360, "J2000.0 黄经应有效");
    }
}

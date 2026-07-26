package io.github.shuyang;

import io.github.shuyang.entity.GanZhi;
import io.github.shuyang.entity.SanYuanJiuYunResult;
import io.github.shuyang.entity.SolarTerm;
import io.github.shuyang.service.GanZhiService;
import io.github.shuyang.service.JieQiService;
import io.github.shuyang.service.SanYuanJiuYunService;
import io.github.shuyang.util.SolarCalculationEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private static final Logger log = LoggerFactory.getLogger(AppTest.class);

    @Test
    void testYun() {
        SanYuanJiuYunService service = new SanYuanJiuYunService();
        log.info("--- 验证周期性 (180年) ---");
        int baseYear = 1864;
        log.info("基准: {}", service.calculate(baseYear));
        log.info("周期+1: {}", service.calculate(baseYear + 180));
        log.info("周期-1: {}", service.calculate(baseYear - 180));
        log.info("周期-2: {}", service.calculate(baseYear - 360));
    }

    @Test
    @DisplayName("Should pass trivially")
    void testApp() {
        assertTrue(true, "Basic sanity check");
    }

    @Test
    @DisplayName("Addition Test")
    void verifyAddition() {
        assertEquals(3, 1 + 2, "1 + 2 should equal 3");
        assertNotNull(Integer.valueOf(3));
        assertNotEquals(5, 1 + 2);
    }

    @Test
    void testGanzhiTime() {
        JieQiService jieQiService = new JieQiService();
        GanZhiService ganZhiService = new GanZhiService(jieQiService);

        int year = 2026;
        int month = 6;
        int day = 14;
        int hour = 23;
        int minutes = 27;
        GanZhi ganZhi = ganZhiService.calculateGanZhi(year, month, day, hour,minutes);
        log.info("{}年{}月{}日{}时的干支: {} {} {} {}",
            year, month, day, hour,
            ganZhi.getGanZhiYear(), ganZhi.getGanZhiMonth(),
            ganZhi.getGanZhiDay(), ganZhi.getGanZhiHour());

        assertNotNull(ganZhi.getGanZhiYear());
        assertNotNull(ganZhi.getGanZhiMonth());
        assertNotNull(ganZhi.getGanZhiDay());
        assertNotNull(ganZhi.getGanZhiHour());
    }
    @Test
    void testJieQi(){
        SolarTerm lichun = SolarTerm.LICHUN;
        SolarTerm next = lichun.next();
        System.out.println(next.getChineseName());
    }

    @Test
    @DisplayName("查询公历时间所处节气")
    void testGetCurrentJieQi() {
        JieQiService jieQiService = new JieQiService();

        // 分钟精确版：2月25日 21:30
        SolarTerm result1 = jieQiService.getCurrentJieQi(2026, 2, 25, 21, 30);
        assertEquals(SolarTerm.YUSHUI, result1, "2月25日应在雨水之后、惊蛰之前");
        log.info("2026-02-25 21:30 → {}", result1.getChineseName());

        // hour版（重载）：1月1日，小寒(5-8日)之前 → 冬至
        SolarTerm result2 = jieQiService.getCurrentJieQi(2026, 1, 1, 12);
        assertEquals(SolarTerm.DONGZHI, result2, "1月1日应在小寒之前，属冬至");
        log.info("2026-01-01 12:00 → {}", result2.getChineseName());

        // LocalDateTime版（重载）：12月31日
        SolarTerm result3 = jieQiService.getCurrentJieQi(
            java.time.LocalDateTime.of(2026, 12, 31, 15, 45));
        assertEquals(SolarTerm.DONGZHI, result3, "12月31日应在冬至之后");
        log.info("2026-12-31 15:45 → {}", result3.getChineseName());

        // 7月15日：小暑(6-9日)之后，大暑(22-25日)之前
        SolarTerm result4 = jieQiService.getCurrentJieQi(2026, 7, 15, 0);
        assertEquals(SolarTerm.XIAOSHU, result4, "7月15日应在小暑之后、大暑之前");
        log.info("2026-07-15 00:00 → {}", result4.getChineseName());

        // 4月20日：谷雨(19-22日)模糊窗口 → 精确计算
        SolarTerm result5 = jieQiService.getCurrentJieQi(2026, 4, 20, 12);
        log.info("2026-04-20 12:00 → {}", result5.getChineseName());
    }
    @Test
    void testTerm(){
        JieQiService jieQiService = new JieQiService();
        SolarTerm li = SolarTerm.fromChineseName("立春");
        SolarTerm.Group group = li.getGroup();
        if (group == SolarTerm.Group.WINTER_TO_SUMMER){
            System.out.println("阳令");
        }
    }

    // ──────────────────────────────────────────────
    // util/SolarCalculationEngine 使用示例
    // ──────────────────────────────────────────────

    /**
     * 方法一：给定一个时刻，计算当时的太阳视黄经。
     */
    @Test
    @DisplayName("util.SolarCalculationEngine — 计算太阳视黄经")
    void testApparentSolarLongitude() {
        // 2026-03-20 12:00 UTC 的秒级时间戳
        long timestamp = java.time.Instant
            .parse("2026-03-20T12:00:00Z")
            .getEpochSecond();

        double longitude = SolarCalculationEngine.apparentSolarLongitude(timestamp);

        // 春分前后黄经应接近 0°（或 360°）
        double diff = Math.min(Math.abs(longitude - 0.0), Math.abs(longitude - 360.0));
        assertTrue(diff < 2.0,
            "春分日黄经应接近 0°，实际: " + longitude);

        log.info("2026-03-20 12:00 UTC 太阳视黄经: {}°", String.format("%.4f", longitude));
    }

    /**
     * 方法二：二分搜索太阳黄经到达目标角度的精确时刻（节气交接时刻）。
     * <p>
     * 24 节气的目标黄经：春分 0°, 清明 15°, …, 立春 315°, 雨水 330°, 惊蛰 345°。
     * </p>
     */
    @Test
    @DisplayName("util.SolarCalculationEngine — 二分搜索节气时刻")
    void testBinarySearchSolarTerm() {
        // 搜索 2026 年立春（黄经 315°）的精确时刻
        // 立春通常在 2 月 3-6 日，UTC 搜索范围放宽到 2月1日~2月8日
        long start = java.time.ZonedDateTime
            .of(2026, 2, 1, 0, 0, 0, 0, java.time.ZoneOffset.UTC)
            .toEpochSecond();
        long end = java.time.ZonedDateTime
            .of(2026, 2, 8, 0, 0, 0, 0, java.time.ZoneOffset.UTC)
            .toEpochSecond();

        // 二分搜索黄经到达 315° 的时刻（精度 1 秒）
        long resultSecs = SolarCalculationEngine.binarySearchForSolarLongitude(
            start, end, 315.0);

        // 验证结果黄经确实接近 315°
        double actualLongitude = SolarCalculationEngine.apparentSolarLongitude(resultSecs);
        double diff = Math.abs(actualLongitude - 315.0);
        // 允许 0.1° 误差（若差值接近 360° 则是归一化边界，取小值）
        diff = Math.min(diff, 360.0 - diff);
        assertTrue(diff < 0.1,
            "搜索结果黄经应接近 315°，实际: " + actualLongitude + "（偏差 " + diff + "°）");

        // 转为北京时间查看
        java.time.ZonedDateTime beijingTime = java.time.Instant
            .ofEpochSecond(resultSecs)
            .atZone(java.time.ZoneId.of("Asia/Shanghai"));

        log.info("2026 年立春精确时刻（北京时间）: {}", beijingTime);
        log.info("该时刻太阳视黄经: {}°", String.format("%.6f", actualLongitude));
    }

    /**
     * 遍历 2026 年全部 24 个节气并打印精确时间。
     */
    @Test
    @DisplayName("util.SolarCalculationEngine — 计算全年 24 节气")
    void testAllSolarTermsIn2026() {
        // 24 节气名称，从立春开始（黄经 315°），每步 +15°
        String[] names = {
            "立春", "雨水", "惊蛰", "春分", "清明", "谷雨",
            "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
            "立秋", "处暑", "白露", "秋分", "寒露", "霜降",
            "立冬", "小雪", "大雪", "冬至", "小寒", "大寒"
        };

        java.time.ZoneId beijingZone = java.time.ZoneId.of("Asia/Shanghai");

        // 搜索窗口递增：每个节气间隔约 15 天，留 ±4 天余量
        long windowStart = java.time.ZonedDateTime
            .of(2026, 1, 20, 0, 0, 0, 0, java.time.ZoneOffset.UTC)
            .toEpochSecond();
        long stepSeconds = 15 * 86400;  // 15 天

        log.info("─── 2026 年 24 节气（北京时间）───");

        for (int i = 0; i < 24; i++) {
            double targetLon = (315.0 + i * 15.0) % 360.0;
            long searchEnd = windowStart + stepSeconds + 8 * 86400; // 窗口 23 天

            long resultSecs = SolarCalculationEngine.binarySearchForSolarLongitude(
                windowStart, searchEnd, targetLon);

            java.time.ZonedDateTime bj = java.time.Instant
                .ofEpochSecond(resultSecs)
                .atZone(beijingZone);

            double actualLon = SolarCalculationEngine.apparentSolarLongitude(resultSecs);
            log.info("  {}  黄经 {}°  {}", bj, String.format("%6.3f", actualLon), names[i]);

            // 窗口向前滑动
            windowStart = resultSecs + 10 * 86400; // 从当前节气往后 10 天开始搜下一个
        }
    }

    @Test
    void newSolar(){
        int year = 2026; int month = 7; int day = 26; int hour = 15; int minutes = 21;
        JieQiService jieQiService = JieQiService.createHighPrecision();
        SolarTerm currentJieQi = jieQiService.getCurrentJieQi(year, month, day, hour, minutes);
        String nextJieqi = currentJieQi.next().getChineseName();
        GanZhiService ganZhiService = new GanZhiService(jieQiService);
        String ganZhiYear = ganZhiService.getGanZhiYear(year,month,day,hour,minutes);
        String ganZhiMonth = ganZhiService.getGanZhiMonth(ganZhiYear,year,month,day,hour);
        String ganZhiDay = ganZhiService.getGanZhiDay(year, month, day);
        String ganZhiHour = ganZhiService.getGanZhiHour(ganZhiDay, hour);

        SanYuanJiuYunService sanYuanJiuYunService = new SanYuanJiuYunService();
        SanYuanJiuYunResult sanYuanJiuYunResult = sanYuanJiuYunService.calculate(year);

        System.out.println(nextJieqi + " " + ganZhiYear + " " + ganZhiMonth + " " + ganZhiDay + " " + ganZhiHour + " " + sanYuanJiuYunResult);
    }
}

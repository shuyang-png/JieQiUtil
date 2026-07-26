package io.github.shuyang;

import io.github.shuyang.entity.GanZhi;
import io.github.shuyang.entity.SolarTerm;
import io.github.shuyang.exception.JieQiException;
import io.github.shuyang.service.GanZhiService;
import io.github.shuyang.service.JieQiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GanZhiServiceTest {
    private static final Logger log = LoggerFactory.getLogger(GanZhiServiceTest.class);

    private GanZhiService service;

    @BeforeEach
    void setUp() {
        JieQiService jieQiService = new JieQiService();
        service = new GanZhiService(jieQiService);
    }

    // ==================== 年柱 ====================

    @Test
    @DisplayName("年柱：2026 年 3 月（立春后）应为丙午年")
    void testYearPillar_AfterLichun() {
        String ganZhiYear = service.getGanZhiYear(2026, 3, 1, 12, 0);
        assertEquals("丙午", ganZhiYear);
    }

    @Test
    @DisplayName("年柱：立春前应按上一年干支")
    void testYearPillar_BeforeLichun() {
        // 2026 年立春在 2 月 4 日前后，1 月 1 日应在立春前
        String ganZhiYear = service.getGanZhiYear(2026, 1, 1, 12, 0);
        // 立春前应为上一年(2025)干支 → 乙巳
        assertEquals("乙巳", ganZhiYear);
        log.info("2026-01-01 (立春前) 年柱: {}", ganZhiYear);
    }

    @Test
    @DisplayName("年柱：1984 年应为甲子年")
    void testYearPillar_1984() {
        // 1984 年甲子年（1984-02-04 立春后）
        String ganZhiYear = service.getGanZhiYear(1984, 3, 1, 12, 0);
        assertEquals("甲子", ganZhiYear, "1984 年应为甲子年");
    }

    @Test
    @DisplayName("年柱：无效日期抛异常")
    void testYearPillar_InvalidDate() {
        assertThrows(JieQiException.class, () -> service.getGanZhiYear(2026, 0, 0, 12, 0));
    }

    // ==================== 月柱 ====================

    @Test
    @DisplayName("月柱：完整计算 2026-06-15 月柱")
    void testMonthPillar_June() {
        GanZhi ganZhi = service.calculateGanZhi(2026, 6, 15, 12, 0);
        // 2026 年 6 月：丙午年 甲午月
        assertEquals("甲午", ganZhi.getGanZhiMonth());
        log.info("2026-06-15 月柱: {}", ganZhi.getGanZhiMonth());
    }

    @Test
    @DisplayName("月柱：无效月份抛异常")
    void testMonthPillar_InvalidMonth() {
        // getGanZhiMonth 内 LocalDateTime.of 先抛 DateTimeException
        assertThrows(RuntimeException.class,
            () -> service.getGanZhiMonth("丙午", 2026, 13, 1, 12));
        assertThrows(RuntimeException.class,
            () -> service.getGanZhiMonth("丙午", 2026, 0, 1, 12));
    }

    // ==================== 日柱 ====================

    @Test
    @DisplayName("日柱：基准日干支一致且 60 天循环")
    void testDayPillar_BaseDate() {
        // 公式计算：1984-01-01 → 甲午（非注释所说的甲子）
        // 实际基准日计算结果，验证一致性
        String ganZhiDay = service.getGanZhiDay(1984, 1, 1);
        assertNotNull(ganZhiDay);
        assertEquals(2, ganZhiDay.length());

        // 60 天循环：干支以 60 天为周期循环
        String day60Later = service.getGanZhiDay(1984, 3, 1);
        assertEquals(ganZhiDay, day60Later, "60 天后干支应回到同一日柱");
        log.info("1984-01-01 日柱: {}, 60天后(1984-03-01): {}", ganZhiDay, day60Later);
    }

    @Test
    @DisplayName("日柱：连续两天干支不同")
    void testDayPillar_ConsecutiveDays() {
        String day1 = service.getGanZhiDay(1984, 1, 1);
        String day2 = service.getGanZhiDay(1984, 1, 2);
        assertNotEquals(day1, day2, "连续两天的日柱应不同");
    }

    @Test
    @DisplayName("日柱：2026-06-15 日柱")
    void testDayPillar_20260615() {
        String ganZhiDay = service.getGanZhiDay(2026, 6, 15);
        assertNotNull(ganZhiDay);
        assertFalse(ganZhiDay.isEmpty());
        assertEquals(2, ganZhiDay.length(), "干支应为 2 个字符");
        log.info("2026-06-15 日柱: {}", ganZhiDay);
    }

    // ==================== 时柱 ====================

    @Test
    @DisplayName("时柱：23 点晚子时 — 用次日天干查五鼠遁")
    void testHourPillar_23OClock() {
        // 日柱为 "己未"，日干己 → 次日天干庚 → 五鼠遁得丙 → 丙子
        String ganZhiHour = service.getGanZhiHour("己未", 23);
        assertNotNull(ganZhiHour);
        assertEquals("丙子", ganZhiHour, "己日23点应用次日天干(庚)五鼠遁得丙子");
        log.info("日柱己未 23点(晚子时) 时柱: {}", ganZhiHour);
    }

    @Test
    @DisplayName("时柱：0 点（子时）")
    void testHourPillar_Midnight() {
        String ganZhiHour = service.getGanZhiHour("甲子", 0);
        assertNotNull(ganZhiHour);
        assertTrue(ganZhiHour.endsWith("子"), "0 点应为子时");
    }

    @Test
    @DisplayName("时柱：12 点（午时）")
    void testHourPillar_Noon() {
        String ganZhiHour = service.getGanZhiHour("甲子", 12);
        assertNotNull(ganZhiHour);
        assertTrue(ganZhiHour.endsWith("午"), "12 点应为午时");
    }

    @Test
    @DisplayName("时柱：所有 24 小时均返回有效值")
    void testHourPillar_AllHours() {
        String dayGanZhi = "甲子";
        for (int hour = 0; hour <= 23; hour++) {
            String ganZhiHour = service.getGanZhiHour(dayGanZhi, hour);
            assertNotNull(ganZhiHour, hour + "点时柱不应为null");
            assertEquals(2, ganZhiHour.length(), hour + "点时柱应为2字符");
            log.debug("{}时: {}", hour, ganZhiHour);
        }
    }

    // ==================== 完整四柱 ====================

    @Test
    @DisplayName("完整四柱：2026-06-15 12:00")
    void testCalculateGanZhi_FullSet() {
        GanZhi ganZhi = service.calculateGanZhi(2026, 6, 15, 12, 0);
        assertNotNull(ganZhi);
        assertNotNull(ganZhi.getGanZhiYear());
        assertNotNull(ganZhi.getGanZhiMonth());
        assertNotNull(ganZhi.getGanZhiDay());
        assertNotNull(ganZhi.getGanZhiHour());

        // 每柱都是 2 个字符
        assertEquals(2, ganZhi.getGanZhiYear().length());
        assertEquals(2, ganZhi.getGanZhiMonth().length());
        assertEquals(2, ganZhi.getGanZhiDay().length());
        assertEquals(2, ganZhi.getGanZhiHour().length());

        log.info("2026-06-15 12:00 四柱: {}", ganZhi);
    }

    @Test
    @DisplayName("完整四柱：1984-01-01 00:00（基准日）")
    void testCalculateGanZhi_BaseDate() {
        GanZhi ganZhi = service.calculateGanZhi(1984, 1, 1, 0, 0);
        assertNotNull(ganZhi);
        // 验证日柱非空且 2 字符
        assertNotNull(ganZhi.getGanZhiDay());
        assertEquals(2, ganZhi.getGanZhiDay().length());
        log.info("1984-01-01 00:00 四柱: {}", ganZhi);
    }

    // ==================== 静态查找表 ====================

    @Test
    @DisplayName("天干列表共 10 个")
    void testTianganList() {
        List<String> tiangans = GanZhiService.getTianganNames();
        assertEquals(10, tiangans.size());
        assertEquals("甲", tiangans.get(0));
        assertEquals("癸", tiangans.get(9));
    }

    @Test
    @DisplayName("地支列表共 12 个")
    void testDizhiList() {
        List<String> dizhis = GanZhiService.getDizhiNames();
        assertEquals(12, dizhis.size());
        assertEquals("子", dizhis.get(0));
        assertEquals("亥", dizhis.get(11));
    }

    @Test
    @DisplayName("天干索引映射共 10 个")
    void testTianganIndex() {
        Map<String, Integer> index = GanZhiService.getTianganIndex();
        assertEquals(10, index.size());
        assertEquals(1, (int) index.get("甲"));
        assertEquals(10, (int) index.get("癸"));
    }

    @Test
    @DisplayName("地支索引映射共 12 个")
    void testDizhiIndex() {
        Map<String, Integer> index = GanZhiService.getDizhiIndex();
        assertEquals(12, index.size());
        assertEquals(1, (int) index.get("子"));
        assertEquals(12, (int) index.get("亥"));
    }

    @Test
    @DisplayName("五虎遁映射完整性")
    void testWuhuIntegrity() {
        Map<String, String> wuhu = GanZhiService.getWuhu();
        // 五虎遁应覆盖全部 10 天干
        List<String> tiangans = GanZhiService.getTianganNames();
        for (String gan : tiangans) {
            assertTrue(wuhu.containsKey(gan),
                "五虎遁应包含天干: " + gan);
        }
        // 甲己 → 丙，乙庚 → 戊
        assertEquals("丙", wuhu.get("甲"));
        assertEquals("丙", wuhu.get("己"));
        assertEquals("戊", wuhu.get("乙"));
        assertEquals("戊", wuhu.get("庚"));
    }

    @Test
    @DisplayName("五鼠遁映射完整性")
    void testWushuIntegrity() {
        Map<String, String> wushu = GanZhiService.getWushu();
        List<String> tiangans = GanZhiService.getTianganNames();
        for (String gan : tiangans) {
            assertTrue(wushu.containsKey(gan),
                "五鼠遁应包含天干: " + gan);
        }
        // 甲己 → 甲，乙庚 → 丙
        assertEquals("甲", wushu.get("甲"));
        assertEquals("甲", wushu.get("己"));
        assertEquals("丙", wushu.get("乙"));
        assertEquals("丙", wushu.get("庚"));
    }
}

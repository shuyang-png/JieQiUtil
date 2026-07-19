package io.github.shuyang;

import io.github.shuyang.entity.SolarTerm;
import io.github.shuyang.service.JieQiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class JieQiServiceTest {
    private static final Logger log = LoggerFactory.getLogger(JieQiServiceTest.class);

    private JieQiService service;

    @BeforeEach
    void setUp() {
        service = new JieQiService();
    }

    // ==================== getJieQi ====================

    @Test
    @DisplayName("getJieQi 按中文名称查询 2026 年立春")
    void testGetJieQi_ByName() {
        LocalDateTime lichun = service.getJieQi(2026, "立春");
        assertNotNull(lichun);
        assertEquals(2026, lichun.getYear());
        // 立春在 2 月 3-6 日
        assertEquals(2, lichun.getMonthValue());
        assertTrue(lichun.getDayOfMonth() >= 3 && lichun.getDayOfMonth() <= 6,
            "立春应在 2 月 3-6 日，实际: " + lichun);
        log.info("2026 立春: {}", lichun);
    }

    @Test
    @DisplayName("getJieQi 按枚举查询 2026 年夏至")
    void testGetJieQi_ByEnum() {
        LocalDateTime xiazhi = service.getJieQi(2026, SolarTerm.XIAZHI);
        assertNotNull(xiazhi);
        assertEquals(2026, xiazhi.getYear());
        // 夏至在 6 月 20-23 日
        assertEquals(6, xiazhi.getMonthValue());
        assertTrue(xiazhi.getDayOfMonth() >= 20 && xiazhi.getDayOfMonth() <= 23,
            "夏至应在 6 月 20-23 日，实际: " + xiazhi);
        log.info("2026 夏至: {}", xiazhi);
    }

    @Test
    @DisplayName("getJieQi 2026 年全部 24 节气均能正常计算")
    void testGetJieQi_All24Terms() {
        for (SolarTerm term : SolarTerm.values()) {
            LocalDateTime time = service.getJieQi(2026, term);
            assertNotNull(time, term.getChineseName() + " 不应为 null");
            assertEquals(2026, time.getYear(),
                term.getChineseName() + " 应在本年内: " + time);
            log.debug("2026 {}: {}", term.getChineseName(), time);
        }
    }

    @Test
    @DisplayName("getJieQi 不同年份节气时间不同")
    void testGetJieQi_DifferentYears() {
        LocalDateTime lichun2025 = service.getJieQi(2025, SolarTerm.LICHUN);
        LocalDateTime lichun2026 = service.getJieQi(2026, SolarTerm.LICHUN);
        assertNotEquals(lichun2025, lichun2026, "不同年份的立春时间应不同");
    }

    // ==================== checkPeriod ====================

    @Test
    @DisplayName("checkPeriod 冬至属冬至→夏至组")
    void testCheckPeriod_DongZhi() {
        SolarTerm.Group group = service.checkPeriod("冬至");
        assertEquals(SolarTerm.Group.WINTER_TO_SUMMER, group);
    }

    @Test
    @DisplayName("checkPeriod 夏至属夏至→冬至组")
    void testCheckPeriod_XiaZhi() {
        SolarTerm.Group group = service.checkPeriod("夏至");
        assertEquals(SolarTerm.Group.SUMMER_TO_WINTER, group);
    }

    @Test
    @DisplayName("checkPeriod 立春属冬至→夏至组")
    void testCheckPeriod_LiChun() {
        SolarTerm.Group group = service.checkPeriod("立春");
        assertEquals(SolarTerm.Group.WINTER_TO_SUMMER, group);
    }

    @Test
    @DisplayName("checkPeriod 立秋属夏至→冬至组")
    void testCheckPeriod_LiQiu() {
        SolarTerm.Group group = service.checkPeriod("立秋");
        assertEquals(SolarTerm.Group.SUMMER_TO_WINTER, group);
    }

    // ==================== getCurrentJieQi 边界 ====================

    @Test
    @DisplayName("getCurrentJieQi 月初第一天（1月1日）")
    void testGetCurrentJieQi_NewYear() {
        SolarTerm term = service.getCurrentJieQi(2026, 1, 1, 12);
        assertNotNull(term);
        // 1月1日在小寒(1月5-8日)之前 → 冬至
        assertEquals(SolarTerm.DONGZHI, term);
    }

    @Test
    @DisplayName("getCurrentJieQi 月末最后一天（12月31日）")
    void testGetCurrentJieQi_YearEnd() {
        SolarTerm term = service.getCurrentJieQi(2026, 12, 31, 12);
        assertNotNull(term);
        // 12月31日在大雪(12月6-9日)之后，冬至(12月21-24日)之后
        assertEquals(SolarTerm.DONGZHI, term);
    }

    @Test
    @DisplayName("getCurrentJieQi LocalDateTime 重载")
    void testGetCurrentJieQi_LocalDateTime() {
        LocalDateTime dt = LocalDateTime.of(2026, 6, 15, 12, 0);
        SolarTerm term = service.getCurrentJieQi(dt);
        assertNotNull(term);
        // 6 月 15 日在芒种(6月4-7日)之后，夏至(6月20-23日)之前
        assertEquals(SolarTerm.MANGZHONG, term);
        log.info("2026-06-15 12:00 → {}", term.getChineseName());
    }

    @Test
    @DisplayName("getCurrentJieQi 结果不为 null（全年每天）")
    void testGetCurrentJieQi_AllYear() {
        // 抽查每个月的第 1 天和第 15 天
        for (int month = 1; month <= 12; month++) {
            SolarTerm term1 = service.getCurrentJieQi(2026, month, 1, 12);
            SolarTerm term15 = service.getCurrentJieQi(2026, month, 15, 12);
            assertNotNull(term1, month + "月1日不应为null");
            assertNotNull(term15, month + "月15日不应为null");
        }
    }

    // ==================== 静态方法 ====================

    @Test
    @DisplayName("getWinterToSummerTerms 返回 12 个节气")
    void testStaticGetWinterToSummerTerms() {
        assertEquals(12, JieQiService.getWinterToSummerTerms().size());
    }

    @Test
    @DisplayName("getSummerToWinterTerms 返回 12 个节气")
    void testStaticGetSummerToWinterTerms() {
        assertEquals(12, JieQiService.getSummerToWinterTerms().size());
    }
}

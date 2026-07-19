package io.github.shuyang;

import io.github.shuyang.entity.SanYuanJiuYunResult;
import io.github.shuyang.service.SanYuanJiuYunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class SanYuanJiuYunServiceTest {
    private static final Logger log = LoggerFactory.getLogger(SanYuanJiuYunServiceTest.class);

    private SanYuanJiuYunService service;

    @BeforeEach
    void setUp() {
        service = new SanYuanJiuYunService();
    }

    @Test
    @DisplayName("已知年份验证")
    void testKnownYears() {
        // 1864 = 上元一运（基准年）
        SanYuanJiuYunResult r1 = service.calculate(1864);
        assertEquals("上元", r1.getYuan());
        assertEquals(1, r1.getYun());
        assertEquals(1, r1.getYunInCurrentYuan());

        // 1924 = 中元四运
        SanYuanJiuYunResult r2 = service.calculate(1924);
        assertEquals("中元", r2.getYuan());
        assertEquals(4, r2.getYun());
        assertEquals(1, r2.getYunInCurrentYuan());

        // 1984 = 下元七运
        SanYuanJiuYunResult r3 = service.calculate(1984);
        assertEquals("下元", r3.getYuan());
        assertEquals(7, r3.getYun());
        assertEquals(1, r3.getYunInCurrentYuan());

        // 2004 = 下元八运
        SanYuanJiuYunResult r4 = service.calculate(2004);
        assertEquals("下元", r4.getYuan());
        assertEquals(8, r4.getYun());
        assertEquals(2, r4.getYunInCurrentYuan());

        // 2024 = 下元九运
        SanYuanJiuYunResult r5 = service.calculate(2024);
        assertEquals("下元", r5.getYuan());
        assertEquals(9, r5.getYun());
        assertEquals(3, r5.getYunInCurrentYuan());
    }

    @Test
    @DisplayName("2026 年应为下元九运")
    void testCurrentYear() {
        SanYuanJiuYunResult r = service.calculate(2026);
        assertEquals("下元", r.getYuan());
        assertEquals(9, r.getYun());
        assertEquals(3, r.getYunInCurrentYuan());
        assertEquals(2026, r.getYear());
        log.info("2026: {}", r);
    }

    @Test
    @DisplayName("180 年周期律")
    void test180YearCycle() {
        int baseYear = 1864;
        SanYuanJiuYunResult base = service.calculate(baseYear);
        SanYuanJiuYunResult plus180 = service.calculate(baseYear + 180);
        SanYuanJiuYunResult minus180 = service.calculate(baseYear - 180);

        assertEquals(base.getYuan(), plus180.getYuan());
        assertEquals(base.getYun(), plus180.getYun());
        assertEquals(base.getYuan(), minus180.getYuan());
        assertEquals(base.getYun(), minus180.getYun());
    }

    @Test
    @DisplayName("同一运的 20 年内运数不变")
    void testYunProgression() {
        // 1864-1883 是一运，共 20 年
        SanYuanJiuYunResult start = service.calculate(1864);
        for (int year = 1865; year <= 1883; year++) {
            SanYuanJiuYunResult r = service.calculate(year);
            assertEquals(start.getYun(), r.getYun(),
                year + " 应与 1864 同属一运");
            assertEquals(start.getYuan(), r.getYuan());
        }
    }

    @Test
    @DisplayName("运边界：1883→1884 一运转二运")
    void testYunTransition_1to2() {
        SanYuanJiuYunResult r1883 = service.calculate(1883);
        assertEquals(1, r1883.getYun());
        assertEquals("上元", r1883.getYuan());

        SanYuanJiuYunResult r1884 = service.calculate(1884);
        assertEquals(2, r1884.getYun());
        assertEquals("上元", r1884.getYuan());
    }

    @Test
    @DisplayName("元边界：1923→1924 三运转四运（上元→中元）")
    void testYuanTransition_3to4() {
        SanYuanJiuYunResult r1923 = service.calculate(1923);
        assertEquals(3, r1923.getYun());
        assertEquals("上元", r1923.getYuan());

        SanYuanJiuYunResult r1924 = service.calculate(1924);
        assertEquals(4, r1924.getYun());
        assertEquals("中元", r1924.getYuan());
    }

    @Test
    @DisplayName("元边界：1983→1984 六运转七运（中元→下元）")
    void testYuanTransition_6to7() {
        SanYuanJiuYunResult r1983 = service.calculate(1983);
        assertEquals(6, r1983.getYun());
        assertEquals("中元", r1983.getYuan());

        SanYuanJiuYunResult r1984 = service.calculate(1984);
        assertEquals(7, r1984.getYun());
        assertEquals("下元", r1984.getYuan());
    }

    @Test
    @DisplayName("元边界：2043→2044 九运转一运（下元→上元新周期）")
    void testYuanTransition_9to1() {
        SanYuanJiuYunResult r2043 = service.calculate(2043);
        assertEquals(9, r2043.getYun());
        assertEquals("下元", r2043.getYuan());

        SanYuanJiuYunResult r2044 = service.calculate(2044);
        assertEquals(1, r2044.getYun());
        assertEquals("上元", r2044.getYuan());
    }

    @Test
    @DisplayName("公元前年份也能正确计算")
    void testNegativeYears() {
        // 1864 - 180 = 1684 → 上元一运
        SanYuanJiuYunResult r1684 = service.calculate(1684);
        assertEquals("上元", r1684.getYuan());
        assertEquals(1, r1684.getYun());

        // 再往前 180 年
        SanYuanJiuYunResult r1504 = service.calculate(1504);
        assertEquals("上元", r1504.getYuan());
        assertEquals(1, r1504.getYun());

        // 公元前 1 年也不应崩溃
        SanYuanJiuYunResult rBC = service.calculate(-1);
        assertNotNull(rBC);
        assertNotNull(rBC.getYuan());
        assertTrue(rBC.getYun() >= 1 && rBC.getYun() <= 9);
    }

    @Test
    @DisplayName("运数始终在 1-9 范围内")
    void testYunAlwaysInRange() {
        for (int year = 1500; year <= 2200; year += 17) {
            SanYuanJiuYunResult r = service.calculate(year);
            assertTrue(r.getYun() >= 1 && r.getYun() <= 9,
                year + " 运数应在 1-9 之间");
            assertTrue(r.getYunInCurrentYuan() >= 1 && r.getYunInCurrentYuan() <= 3,
                year + " 元内运应在 1-3 之间");
            assertNotNull(r.getYuan());
        }
    }

    @Test
    @DisplayName("元名称在上元/中元/下元中循环")
    void testYuanNameCycles() {
        String[] expected = {"上元", "中元", "下元", "上元", "中元", "下元"};
        for (int i = 0; i < 6; i++) {
            int year = 1864 + i * 60;
            SanYuanJiuYunResult r = service.calculate(year);
            assertEquals(expected[i], r.getYuan(), year + " 应为 " + expected[i]);
        }
    }
}

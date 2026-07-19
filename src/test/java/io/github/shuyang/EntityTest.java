package io.github.shuyang;

import io.github.shuyang.entity.GanZhi;
import io.github.shuyang.entity.JieQiInfo;
import io.github.shuyang.entity.SanYuanJiuYunResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    // ==================== GanZhi ====================

    @Test
    @DisplayName("GanZhi equals 和 hashCode")
    void testGanZhi_EqualsHashCode() {
        GanZhi g1 = new GanZhi("丙午", "甲午", "己未", "己子");
        GanZhi g2 = new GanZhi("丙午", "甲午", "己未", "己子");
        GanZhi g3 = new GanZhi("甲子", "丙寅", "戊辰", "庚午");

        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
        assertNotEquals(g1, g3);
    }

    @Test
    @DisplayName("GanZhi toString")
    void testGanZhi_ToString() {
        GanZhi g = new GanZhi("丙午", "甲午", "己未", "己子");
        String str = g.toString();
        assertTrue(str.contains("丙午"));
        assertTrue(str.contains("甲午"));
        assertTrue(str.contains("己未"));
        assertTrue(str.contains("己子"));
    }

    @Test
    @DisplayName("GanZhi getter/setter")
    void testGanZhi_GetterSetter() {
        GanZhi g = new GanZhi("丙午", "甲午", "己未", "己子");
        assertEquals("丙午", g.getGanZhiYear());
        assertEquals("甲午", g.getGanZhiMonth());
        assertEquals("己未", g.getGanZhiDay());
        assertEquals("己子", g.getGanZhiHour());

        g.setGanZhiYear("乙巳");
        assertEquals("乙巳", g.getGanZhiYear());
    }

    // ==================== JieQiInfo ====================

    @Test
    @DisplayName("JieQiInfo equals 和 hashCode")
    void testJieQiInfo_EqualsHashCode() {
        JieQiInfo info1 = new JieQiInfo(315.0, new int[]{2, 3, 6});
        JieQiInfo info2 = new JieQiInfo(315.0, new int[]{2, 3, 6});
        JieQiInfo info3 = new JieQiInfo(330.0, new int[]{2, 18, 21});

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
        assertNotEquals(info1, info3);
    }

    @Test
    @DisplayName("JieQiInfo 防御性拷贝：修改传入数组不影响内部状态")
    void testJieQiInfo_DefensiveCopy() {
        int[] date = {2, 3, 6};
        JieQiInfo info = new JieQiInfo(315.0, date);
        date[0] = 99; // 修改外部数组

        assertEquals(2, info.getDate()[0], "内部数组不应受影响");

        // getDate 返回的数组修改也不影响内部
        int[] returned = info.getDate();
        returned[0] = 99;
        assertEquals(2, info.getDate()[0], "getDate 应返回副本");
    }

    @Test
    @DisplayName("JieQiInfo toString")
    void testJieQiInfo_ToString() {
        JieQiInfo info = new JieQiInfo(315.0, new int[]{2, 3, 6});
        String str = info.toString();
        assertTrue(str.contains("315"));
        assertTrue(str.contains("2"));
    }

    // ==================== SanYuanJiuYunResult ====================

    @Test
    @DisplayName("SanYuanJiuYunResult equals 和 hashCode")
    void testSanYuanJiuYunResult_EqualsHashCode() {
        SanYuanJiuYunResult r1 = new SanYuanJiuYunResult(2026, "下元", 9, 3);
        SanYuanJiuYunResult r2 = new SanYuanJiuYunResult(2026, "下元", 9, 3);
        SanYuanJiuYunResult r3 = new SanYuanJiuYunResult(2024, "下元", 9, 3);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    @DisplayName("SanYuanJiuYunResult getter")
    void testSanYuanJiuYunResult_Getter() {
        SanYuanJiuYunResult r = new SanYuanJiuYunResult(2026, "下元", 9, 3);
        assertEquals(2026, r.getYear());
        assertEquals("下元", r.getYuan());
        assertEquals(9, r.getYun());
        assertEquals(3, r.getYunInCurrentYuan());
    }

    @Test
    @DisplayName("SanYuanJiuYunResult toString")
    void testSanYuanJiuYunResult_ToString() {
        SanYuanJiuYunResult r = new SanYuanJiuYunResult(2026, "下元", 9, 3);
        String str = r.toString();
        assertTrue(str.contains("2026"));
        assertTrue(str.contains("下元"));
        assertTrue(str.contains("9"));
    }
}

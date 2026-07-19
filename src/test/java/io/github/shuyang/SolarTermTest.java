package io.github.shuyang;

import io.github.shuyang.entity.SolarTerm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SolarTermTest {

    @Test
    @DisplayName("24 个节气全部存在")
    void testAll24TermsExist() {
        String[] names = {
            "小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
            "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
            "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
            "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
        };
        assertEquals(24, names.length, "应有 24 个节气");
        for (String name : names) {
            assertNotNull(SolarTerm.fromChineseName(name), name + " 应存在");
        }
    }

    @Test
    @DisplayName("fromChineseName 合法名称")
    void testFromChineseName_Valid() {
        SolarTerm term = SolarTerm.fromChineseName("立春");
        assertEquals(SolarTerm.LICHUN, term);
        assertEquals("立春", term.getChineseName());
        assertEquals(315.0, term.getLongitude());
        assertEquals(2, term.getGregorianMonth());
    }

    @Test
    @DisplayName("fromChineseName 非法名称抛异常")
    void testFromChineseName_Invalid() {
        assertThrows(IllegalArgumentException.class, () -> SolarTerm.fromChineseName("不存在的节气"));
        assertThrows(IllegalArgumentException.class, () -> SolarTerm.fromChineseName(""));
        assertThrows(IllegalArgumentException.class, () -> SolarTerm.fromChineseName(null));
    }

    @Test
    @DisplayName("next 循环遍历 24 节气")
    void testNext() {
        // 小寒 → 大寒
        assertEquals(SolarTerm.DAHAN, SolarTerm.XIAOHAN.next());
        // 冬至 → 小寒（循环）
        assertEquals(SolarTerm.XIAOHAN, SolarTerm.DONGZHI.next());
    }

    @Test
    @DisplayName("previous 循环遍历 24 节气")
    void testPrevious() {
        // 大寒 → 小寒
        assertEquals(SolarTerm.XIAOHAN, SolarTerm.DAHAN.previous());
        // 小寒 → 冬至（循环）
        assertEquals(SolarTerm.DONGZHI, SolarTerm.XIAOHAN.previous());
    }

    @Test
    @DisplayName("next/previous 完整循环：24 次 next 回到自身")
    void testNextFullCycle() {
        SolarTerm term = SolarTerm.LICHUN;
        for (int i = 0; i < 24; i++) {
            term = term.next();
        }
        assertEquals(SolarTerm.LICHUN, term, "24 次 next 应回到原位");
    }

    @Test
    @DisplayName("previous 完整循环：24 次 previous 回到自身")
    void testPreviousFullCycle() {
        SolarTerm term = SolarTerm.LICHUN;
        for (int i = 0; i < 24; i++) {
            term = term.previous();
        }
        assertEquals(SolarTerm.LICHUN, term, "24 次 previous 应回到原位");
    }

    @Test
    @DisplayName("冬至→夏至列表共 12 个节气")
    void testWinterToSummerList() {
        List<SolarTerm> list = SolarTerm.getWinterToSummerTerms();
        assertEquals(12, list.size());
        assertEquals(SolarTerm.DONGZHI, list.get(0));
        assertEquals(SolarTerm.MANGZHONG, list.get(11));

        // 所有成员 group 应为 WINTER_TO_SUMMER
        for (SolarTerm t : list) {
            assertEquals(SolarTerm.Group.WINTER_TO_SUMMER, t.getGroup(),
                t.getChineseName() + " 应属冬至→夏至组");
        }
    }

    @Test
    @DisplayName("夏至→冬至列表共 12 个节气")
    void testSummerToWinterList() {
        List<SolarTerm> list = SolarTerm.getSummerToWinterTerms();
        assertEquals(12, list.size());
        assertEquals(SolarTerm.XIAZHI, list.get(0));
        assertEquals(SolarTerm.DAXUE, list.get(11));

        // 所有成员 group 应为 SUMMER_TO_WINTER
        for (SolarTerm t : list) {
            assertEquals(SolarTerm.Group.SUMMER_TO_WINTER, t.getGroup(),
                t.getChineseName() + " 应属夏至→冬至组");
        }
    }

    @Test
    @DisplayName("两组列表不重叠且合计 24 节气")
    void testTwoGroupsCoverAll() {
        Set<SolarTerm> all = new HashSet<>();
        all.addAll(SolarTerm.getWinterToSummerTerms());
        all.addAll(SolarTerm.getSummerToWinterTerms());
        assertEquals(24, all.size(), "两组应不重叠且合计 24 个节气");
    }

    @Test
    @DisplayName("每个节气的属性完整性")
    void testTermProperties() {
        for (SolarTerm term : SolarTerm.values()) {
            // 中文名不为空
            assertNotNull(term.getChineseName());
            assertFalse(term.getChineseName().isEmpty());

            // 黄经在 0-360 范围内
            assertTrue(term.getLongitude() >= 0 && term.getLongitude() < 360,
                term.getChineseName() + " 黄经应在 0-360 之间");

            // 公历月份在 1-12
            assertTrue(term.getGregorianMonth() >= 1 && term.getGregorianMonth() <= 12,
                term.getChineseName() + " 月份应在 1-12 之间");

            // 最早日 <= 最晚日
            assertTrue(term.getEarliestDay() <= term.getLatestDay(),
                term.getChineseName() + " 最早日应 <= 最晚日");

            // 干支月序号 1-12
            assertTrue(term.getStemBranchMonth() >= 1 && term.getStemBranchMonth() <= 12,
                term.getChineseName() + " 干支月序号应在 1-12 之间");

            // group 不为空
            assertNotNull(term.getGroup());
        }
    }

    @Test
    @DisplayName("节气公历月份覆盖所有 12 个月")
    void testAllMonthsCovered() {
        Set<Integer> months = new HashSet<>();
        for (SolarTerm term : SolarTerm.values()) {
            months.add(term.getGregorianMonth());
        }
        assertEquals(12, months.size(), "24 节气应覆盖全部 12 个公历月");
    }
}

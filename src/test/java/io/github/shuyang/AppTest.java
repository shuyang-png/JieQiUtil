package io.github.shuyang;

import io.github.shuyang.entity.GanZhi;
import io.github.shuyang.entity.SolarTerm;
import io.github.shuyang.service.GanZhiService;
import io.github.shuyang.service.JieQiService;
import io.github.shuyang.service.SanYuanJiuYunService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}

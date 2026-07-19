package io.github.shuyang.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class JieQiUtilSmokeTest {
    private JieQiUtilSmokeTest() {
    }

    public static void main(String[] args) {
        if (args.length > 0 && "print".equals(args[0])) {
            printYear(args.length > 1 ? Integer.parseInt(args[1]) : 2024);
            return;
        }

        assertEquals(24, JieQiUtil.getJieQiNames().size(), "solar term count");
        assertEquals(0, JieQiUtil.checkPeriod("立春"), "period before summer solstice");
        assertEquals(1, JieQiUtil.checkPeriod("夏至"), "period before winter solstice");
        assertEquals(2, JieQiUtil.checkPeriod("不存在"), "invalid solar term");

        assertDate(LocalDate.of(2024, 2, 4), JieQiUtil.getJieQi(2024, "立春"), "2024 立春");
        assertDate(LocalDate.of(2024, 3, 20), JieQiUtil.getJieQi(2024, "春分"), "2024 春分");
        assertDate(LocalDate.of(2024, 6, 21), JieQiUtil.getJieQi(2024, "夏至"), "2024 夏至");
        assertDate(LocalDate.of(2024, 12, 21), JieQiUtil.getJieQi(2024, "冬至"), "2024 冬至");

        for (String name : JieQiUtil.getJieQiNames()) {
            long epochSecond = JieQiUtil.getJieQiEpochSecond(2024, name);
            double longitude = SolarCalculationEngine.apparentSolarLongitude(epochSecond);
            double target = JieQiUtil.getJieQiInfo(name).getLongitude();
            double error = Math.abs(SolarCalculationEngine.longitudeDifference(longitude, target));
            if (error > 0.00001) {
                throw new AssertionError(name + " longitude error is too large: " + error);
            }
        }

        System.out.println("JieQiUtil smoke test passed");
    }

    private static void printYear(int year) {
        for (String name : JieQiUtil.getJieQiNames()) {
            System.out.println(name + " " + JieQiUtil.getJieQi(year, name));
        }
    }

    private static void assertDate(LocalDate expected, LocalDateTime actual, String label) {
        if (!expected.equals(actual.toLocalDate())) {
            throw new AssertionError(label + " expected date " + expected + ", actual " + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + ", actual " + actual);
        }
    }
}

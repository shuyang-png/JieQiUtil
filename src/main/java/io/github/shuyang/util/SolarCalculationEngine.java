package io.github.shuyang.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * 太阳视黄经计算引擎。
 *
 * <p>算法采用 Meeus/Astronomical Almanac 常用低阶太阳公式，计算太阳视黄经
 * （真黄经修正光行差与章动主要项），再由二分搜索定位节气交接时刻。</p>
 */
public final class SolarCalculationEngine {
    private static final double JULIAN_DAY_UNIX_EPOCH = 2440587.5;
    private static final double JULIAN_DAY_J2000 = 2451545.0;
    private static final double SECONDS_PER_DAY = 86400.0;
    private static final double VSOP_SCALE = 100000000.0;

    private static final double[][] EARTH_L0 = {
        {175347046.0, 0.0, 0.0},
        {3341656.0, 4.6692568, 6283.07585},
        {34894.0, 4.62610, 12566.15170},
        {3497.0, 2.7441, 5753.3849},
        {3418.0, 2.8289, 3.5231},
        {3136.0, 3.6277, 77713.7715},
        {2676.0, 4.4181, 7860.4194},
        {2343.0, 6.1352, 3930.2097},
        {1324.0, 0.7425, 11506.7698},
        {1273.0, 2.0371, 529.6910},
        {1199.0, 1.1096, 1577.3435},
        {990.0, 5.233, 5884.927},
        {902.0, 2.045, 26.298},
        {857.0, 3.508, 398.149},
        {780.0, 1.179, 5223.694},
        {753.0, 2.533, 5507.553},
        {505.0, 4.583, 18849.228},
        {492.0, 4.205, 775.523},
        {357.0, 2.920, 0.067},
        {317.0, 5.849, 11790.629},
        {284.0, 1.899, 796.298},
        {271.0, 0.315, 10977.079},
        {243.0, 0.345, 5486.778},
        {206.0, 4.806, 2544.314},
        {205.0, 1.869, 5573.143},
        {202.0, 2.458, 6069.777},
        {156.0, 0.833, 213.299},
        {132.0, 3.411, 2942.463},
        {126.0, 1.083, 20.775},
        {115.0, 0.645, 0.980},
        {103.0, 0.636, 4694.003},
        {102.0, 0.976, 15720.839},
        {102.0, 4.267, 7.114},
        {99.0, 6.21, 2146.17},
        {98.0, 0.68, 155.42},
        {86.0, 5.98, 161000.69},
        {85.0, 1.30, 6275.96},
        {85.0, 3.67, 71430.70},
        {80.0, 1.81, 17260.15},
        {79.0, 3.04, 12036.46},
        {75.0, 1.76, 5088.63},
        {74.0, 3.50, 3154.69},
        {74.0, 4.68, 801.82},
        {70.0, 0.83, 9437.76},
        {62.0, 3.98, 8827.39},
        {61.0, 1.82, 7084.90},
        {57.0, 2.78, 6286.60},
        {56.0, 4.39, 14143.50},
        {56.0, 3.47, 6279.55},
        {52.0, 0.19, 12139.55},
        {52.0, 1.33, 1748.02},
        {51.0, 0.28, 5856.48},
        {49.0, 0.49, 1194.45},
        {41.0, 5.37, 8429.24},
        {41.0, 2.40, 19651.05},
        {39.0, 6.17, 10447.39},
        {37.0, 6.04, 10213.29},
        {37.0, 2.57, 1059.38},
        {36.0, 1.71, 2352.87},
        {36.0, 1.78, 6812.77},
        {33.0, 0.59, 17789.85},
        {30.0, 0.44, 83996.85},
        {30.0, 2.74, 1349.87},
        {25.0, 3.16, 4690.48}
    };

    private static final double[][] EARTH_L1 = {
        {628331966747.0, 0.0, 0.0},
        {206059.0, 2.678235, 6283.075850},
        {4303.0, 2.6351, 12566.1517},
        {425.0, 1.590, 3.523},
        {119.0, 5.796, 26.298},
        {109.0, 2.966, 1577.344},
        {93.0, 2.59, 18849.23},
        {72.0, 1.14, 529.69},
        {68.0, 1.87, 398.15},
        {67.0, 4.41, 5507.55},
        {59.0, 2.89, 5223.69},
        {56.0, 2.17, 155.42},
        {45.0, 0.40, 796.30},
        {36.0, 0.47, 775.52},
        {29.0, 2.65, 7.11},
        {21.0, 5.34, 0.98},
        {19.0, 1.85, 5486.78},
        {19.0, 4.97, 213.30},
        {17.0, 2.99, 6275.96},
        {16.0, 0.03, 2544.31},
        {16.0, 1.43, 2146.17},
        {15.0, 1.21, 10977.08},
        {12.0, 2.83, 1748.02},
        {12.0, 3.26, 5088.63},
        {12.0, 5.27, 1194.45},
        {12.0, 2.08, 4694.00},
        {11.0, 0.77, 553.57},
        {10.0, 1.30, 6286.60},
        {10.0, 4.24, 1349.87},
        {9.0, 2.70, 242.73},
        {9.0, 5.64, 951.72},
        {8.0, 5.30, 2352.87},
        {6.0, 2.65, 9437.76},
        {6.0, 4.67, 4690.48}
    };

    private static final double[][] EARTH_L2 = {
        {52919.0, 0.0, 0.0},
        {8720.0, 1.0721, 6283.0758},
        {309.0, 0.867, 12566.152},
        {27.0, 0.05, 3.52},
        {16.0, 5.19, 26.30},
        {16.0, 3.68, 155.42},
        {10.0, 0.76, 18849.23},
        {9.0, 2.06, 77713.77},
        {7.0, 0.83, 775.52},
        {5.0, 4.66, 1577.34},
        {4.0, 1.03, 7.11},
        {4.0, 3.44, 5573.14},
        {3.0, 5.14, 796.30},
        {3.0, 6.05, 5507.55},
        {3.0, 1.19, 242.73},
        {3.0, 6.12, 529.69},
        {3.0, 0.31, 398.15},
        {3.0, 2.28, 553.57},
        {2.0, 4.38, 5223.69},
        {2.0, 3.75, 0.98}
    };

    private static final double[][] EARTH_L3 = {
        {289.0, 5.844, 6283.076},
        {35.0, 0.0, 0.0},
        {17.0, 5.49, 12566.15},
        {3.0, 5.20, 155.42},
        {1.0, 4.72, 3.52},
        {1.0, 5.30, 18849.23},
        {1.0, 5.97, 242.73}
    };

    private static final double[][] EARTH_L4 = {
        {114.0, 3.142, 0.0},
        {8.0, 4.13, 6283.08},
        {1.0, 3.84, 12566.15}
    };

    private static final double[][] EARTH_L5 = {
        {1.0, 3.14, 0.0}
    };

    private SolarCalculationEngine() {
    }

    /**
     * 计算指定 UTC 时间戳对应的太阳视黄经，范围为 [0, 360) 度。
     *
     * @param epochSecond UTC 秒级时间戳
     * @return 太阳视黄经
     */
    public static double apparentSolarLongitude(long epochSecond) {
        double jdUtc = JULIAN_DAY_UNIX_EPOCH + epochSecond / SECONDS_PER_DAY;
        double jdTt = jdUtc + estimateDeltaTSeconds(epochSecond) / SECONDS_PER_DAY;
        double tau = (jdTt - JULIAN_DAY_J2000) / 365250.0;
        double t = tau * 10.0;

        double earthLongitude = earthHeliocentricLongitude(tau);
        double trueGeocentricLongitude = normalizeDegrees(Math.toDegrees(earthLongitude) + 180.0);
        double deltaPsi = nutationInLongitude(t);
        double aberration = -20.4898 / 3600.0 / earthRadiusVector(t);
        double fk5Correction = -0.09033 / 3600.0;

        return normalizeDegrees(trueGeocentricLongitude + deltaPsi + aberration + fk5Correction);
    }

    /**
     * 在 UTC 秒级闭区间内二分搜索太阳视黄经到达目标角度的时刻。
     *
     * @param startEpochSecond 起始 UTC 秒
     * @param endEpochSecond   结束 UTC 秒
     * @param targetLongitude  目标黄经，单位度
     * @return 最接近目标黄经的 UTC 秒
     */
    public static long binarySearchForSolarLongitude(
        long startEpochSecond,
        long endEpochSecond,
        double targetLongitude
    ) {
        if (startEpochSecond >= endEpochSecond) {
            throw new IllegalArgumentException("startEpochSecond must be before endEpochSecond");
        }

        double target = normalizeDegrees(targetLongitude);
        double startDiff = longitudeDifference(apparentSolarLongitude(startEpochSecond), target);
        double endDiff = longitudeDifference(apparentSolarLongitude(endEpochSecond), target);

        if (startDiff > 0.0 || endDiff < 0.0) {
            throw new IllegalArgumentException("target longitude is not bracketed by the search range");
        }

        long low = startEpochSecond;
        long high = endEpochSecond;
        while (high - low > 1) {
            long mid = low + (high - low) / 2;
            double midDiff = longitudeDifference(apparentSolarLongitude(mid), target);
            if (midDiff < 0.0) {
                low = mid;
            } else {
                high = mid;
            }
        }

        double lowError = Math.abs(longitudeDifference(apparentSolarLongitude(low), target));
        double highError = Math.abs(longitudeDifference(apparentSolarLongitude(high), target));
        return lowError <= highError ? low : high;
    }

    static double normalizeDegrees(double degrees) {
        double normalized = degrees % 360.0;
        return normalized < 0.0 ? normalized + 360.0 : normalized;
    }

    static double longitudeDifference(double longitude, double targetLongitude) {
        double diff = normalizeDegrees(longitude - targetLongitude);
        return diff >= 180.0 ? diff - 360.0 : diff;
    }

    private static double earthHeliocentricLongitude(double tau) {
        double longitude = sumVsopTerms(EARTH_L0, tau)
            + sumVsopTerms(EARTH_L1, tau) * tau
            + sumVsopTerms(EARTH_L2, tau) * tau * tau
            + sumVsopTerms(EARTH_L3, tau) * tau * tau * tau
            + sumVsopTerms(EARTH_L4, tau) * tau * tau * tau * tau
            + sumVsopTerms(EARTH_L5, tau) * tau * tau * tau * tau * tau;
        return normalizeRadians(longitude / VSOP_SCALE);
    }

    private static double sumVsopTerms(double[][] terms, double tau) {
        double sum = 0.0;
        for (double[] term : terms) {
            sum += term[0] * Math.cos(term[1] + term[2] * tau);
        }
        return sum;
    }

    private static double earthRadiusVector(double t) {
        double meanAnomaly = normalizeDegrees(357.52911
            + 35999.05029 * t
            - 0.0001537 * t * t
            + t * t * t / 24490000.0);
        double anomalyRad = Math.toRadians(meanAnomaly);
        double equationOfCenter = (1.914602 - 0.004817 * t - 0.000014 * t * t) * Math.sin(anomalyRad)
            + (0.019993 - 0.000101 * t) * Math.sin(2.0 * anomalyRad)
            + 0.000289 * Math.sin(3.0 * anomalyRad);
        double trueAnomaly = Math.toRadians(meanAnomaly + equationOfCenter);
        double eccentricity = 0.016708634 - 0.000042037 * t - 0.0000001267 * t * t;
        return 1.000001018 * (1.0 - eccentricity * eccentricity)
            / (1.0 + eccentricity * Math.cos(trueAnomaly));
    }

    private static double nutationInLongitude(double t) {
        double sunMeanLongitude = normalizeDegrees(280.4665 + 36000.7698 * t);
        double moonMeanLongitude = normalizeDegrees(218.3165 + 481267.8813 * t);
        double omega = normalizeDegrees(125.04452
            - 1934.136261 * t
            + 0.0020708 * t * t
            + t * t * t / 450000.0);

        double deltaPsiArcSeconds = -17.20 * Math.sin(Math.toRadians(omega))
            - 1.32 * Math.sin(Math.toRadians(2.0 * sunMeanLongitude))
            - 0.23 * Math.sin(Math.toRadians(2.0 * moonMeanLongitude))
            + 0.21 * Math.sin(Math.toRadians(2.0 * omega));
        return deltaPsiArcSeconds / 3600.0;
    }

    private static double normalizeRadians(double radians) {
        double normalized = radians % (2.0 * Math.PI);
        return normalized < 0.0 ? normalized + 2.0 * Math.PI : normalized;
    }

    private static double estimateDeltaTSeconds(long epochSecond) {
        ZonedDateTime utc = Instant.ofEpochSecond(epochSecond).atZone(ZoneOffset.UTC);
        int year = utc.getYear();
        int daysInYear = LocalDate.of(year, 12, 31).getDayOfYear();
        double y = year + (utc.getDayOfYear() - 0.5) / daysInYear;
        double u;
        double t;

        if (year < 1900) {
            u = (y - 1820.0) / 100.0;
            return -20.0 + 32.0 * u * u;
        }
        if (year < 1920) {
            t = y - 1900.0;
            return -2.79 + 1.494119 * t - 0.0598939 * t * t
                + 0.0061966 * t * t * t - 0.000197 * t * t * t * t;
        }
        if (year < 1941) {
            t = y - 1920.0;
            return 21.20 + 0.84493 * t - 0.076100 * t * t + 0.0020936 * t * t * t;
        }
        if (year < 1961) {
            t = y - 1950.0;
            return 29.07 + 0.407 * t - t * t / 233.0 + t * t * t / 2547.0;
        }
        if (year < 1986) {
            t = y - 1975.0;
            return 45.45 + 1.067 * t - t * t / 260.0 - t * t * t / 718.0;
        }
        if (year < 2005) {
            t = y - 2000.0;
            return 63.86 + 0.3345 * t - 0.060374 * t * t
                + 0.0017275 * t * t * t + 0.000651814 * t * t * t * t
                + 0.00002373599 * t * t * t * t * t;
        }
        if (year < 2050) {
            t = y - 2000.0;
            return 62.92 + 0.32217 * t + 0.005589 * t * t;
        }

        u = (y - 1820.0) / 100.0;
        return -20.0 + 32.0 * u * u;
    }
}

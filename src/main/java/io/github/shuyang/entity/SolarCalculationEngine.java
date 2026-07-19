package io.github.shuyang.entity;

/**
 * 太阳黄经计算引擎。
 * <p>
 * 使用天文算法计算太阳在黄道上的精确位置，
 * 基于 Jean Meeus 的《Astronomical Algorithms》方法，
 * 考虑了太阳轨道运动的主要摄动项。
 * </p>
 */
public class SolarCalculationEngine {

    /**
     * 使用二分搜索查找太阳黄经等于目标值的时刻。
     * <p>
     * 算法：
     * 1. 在给定的时间范围内进行二分搜索
     * 2. 每次计算中间时刻的太阳黄经
     * 3. 与目标黄经比较，缩小搜索范围
     * 4. 精度控制在 60 秒（1 分钟）以内
     * </p>
     *
     * @param startSecs 搜索起始 UTC 秒级时间戳
     * @param endSecs   搜索结束 UTC 秒级时间戳
     * @param targetLongitude 目标太阳黄经（度）
     * @return 黄经最接近目标值的 UTC 秒级时间戳
     */
    public static long binarySearchForSolarLongitude(long startSecs, long endSecs, double targetLongitude) {
        long left = startSecs;
        long right = endSecs;
        /* 精度控制：二分到误差小于 60 秒（1 分钟）为止 */
        while (right - left > 60) {
            long mid = left + (right - left) / 2;
            double longitude = calculateAccurateSolarLongitude(mid);
            if (longitude < targetLongitude) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        /* 在左右两个候选中选择更接近目标黄经的 */
        long candidate1 = left;
        long candidate2 = right;
        double lon1 = calculateAccurateSolarLongitude(candidate1);
        double lon2 = calculateAccurateSolarLongitude(candidate2);
        if (Math.abs(lon1 - targetLongitude) <= Math.abs(lon2 - targetLongitude)) {
            return candidate1;
        } else {
            return candidate2;
        }
    }

    /**
     * 计算给定 UTC 时间戳的太阳黄经（度）。
     * <p>
     * 算法参考 Astronomical Almanac 和 Meeus 的方法。
     * 计算步骤：
     * 1. 将 UTC 时间戳转换为儒略世纪数 T（J2000.0 起算）
     * 2. 计算太阳几何平黄经 L0
     * 3. 计算太阳平近点角 M
     * 4. 计算中心差 C（修正地心视黄经，包含 3 项摄动）
     * 5. 太阳视黄经 L = L0 + C
     * 6. 归化到 0-360 度范围
     * </p>
     *
     * @param utcTimestamp UTC 秒级时间戳
     * @return 太阳黄经（度），范围 0-360
     */
    public static double calculateAccurateSolarLongitude(long utcTimestamp) {
        /* 将时间戳转换为儒略日 JD，再转为儒略世纪数 T（自 J2000.0 起算） */
        double jd = 2440587.5 + (double) utcTimestamp / 86400.0;
        double t = (jd - 2451545.0) / 36525.0;

        /* 太阳几何平黄经 L0（度） */
        double l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t * t;
        l0 = l0 % 360.0;
        if (l0 < 0) l0 += 360.0;

        /* 太阳平近点角 M（度） */
        double m = 357.52911 + 35999.05029 * t - 0.0001537 * t * t;
        m = m % 360.0;
        if (m < 0) m += 360.0;
        double mRad = Math.toRadians(m);

        /*
         * 中心差 C（度）：修正因地球椭圆轨道引起的太阳视位置偏差。
         * 包含三项摄动项，精度约 0.01 度。
         */
        double c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * Math.sin(mRad);
        c += (0.019993 - 0.000101 * t) * Math.sin(2 * mRad);
        c += 0.000289 * Math.sin(3 * mRad);

        /* 太阳视黄经 L = 几何平黄经 + 中心差 */
        double l = l0 + c;

        /* 归化到 0-360 度范围 */
        double solarLongitude = l % 360.0;
        if (solarLongitude < 0) solarLongitude += 360.0;

        return solarLongitude;
    }
}

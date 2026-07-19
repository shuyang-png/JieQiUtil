package io.github.shuyang.entity;

/**
 * 太阳黄经计算器接口。
 * <p>
 * 定义太阳黄经计算的方法，支持依赖注入和 Mock 测试。
 * 默认实现参考 {@link SolarCalculationEngine}。
 * </p>
 */
public interface SolarCalculator {

    /**
     * 使用二分搜索查找太阳黄经等于目标值的时刻。
     *
     * @param startSecs 搜索起始 UTC 秒级时间戳
     * @param endSecs   搜索结束 UTC 秒级时间戳
     * @param targetLongitude 目标太阳黄经（度）
     * @return 黄经最接近目标值的 UTC 秒级时间戳
     */
    long binarySearchForSolarLongitude(long startSecs, long endSecs, double targetLongitude);

    /**
     * 计算给定 UTC 时间戳的太阳黄经（度）。
     *
     * @param utcTimestamp UTC 秒级时间戳
     * @return 太阳黄经（度），范围 0-360
     */
    double calculateAccurateSolarLongitude(long utcTimestamp);
}

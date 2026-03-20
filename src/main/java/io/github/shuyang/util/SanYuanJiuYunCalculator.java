package io.github.shuyang.util;

import io.github.shuyang.entity.SanYuanJiuYunResult;

/**
 * 三元九运计算器 (仅计算元和运)
 * 基准点：1864年为上元一运的开始
 */
public class SanYuanJiuYunCalculator {

    // 基准年：1864年是上元一运的开始
    private static final int BASE_YEAR = 1864;
    // 每运持续年数
    private static final int YEARS_PER_YUN = 20;
    // 每元持续年数
    private static final int YEARS_PER_YUAN = 60;

    /**
     * 计算指定年份的元和运
     * @param year 公历年份
     * @return 一个包含元和运信息的对象
     */
    public static SanYuanJiuYunResult calculate(int year) {
        // 1. 计算从基准年到目标年份经过了多少年
        int totalYearsSinceBase = year - BASE_YEAR;

        // 2. 计算总的运数 (从基准年开始算起)
        // 关键：使用 floorDiv 来处理负数年份，确保向下取整
        int totalYunOrdinal = Math.floorDiv(totalYearsSinceBase, YEARS_PER_YUN);

        // 3. 计算当前年份属于第几个元 (从基准年开始算起)
        int totalYuanOrdinal = Math.floorDiv(totalYearsSinceBase, YEARS_PER_YUAN);

        // 4. 计算当前年份在当前元内的运数 (1-3)
        // (totalYunOrdinal % 3) 的结果是 0, 1, 2。加 1 后变成 1, 2, 3。
        // 由于 totalYunOrdinal 可能为负，需要再次使用 floorMod 确保结果为正
        int yunInCurrentYuan = (Math.floorMod(totalYunOrdinal, 3) + 1);

        // 5. 计算当前年份属于哪个元 ("上元", "中元", "下元")
        String yuan = getYuanName(totalYuanOrdinal);

        // 6. 计算当前年份属于哪个运 (1-9)
        int yun = getYunNumber(totalYuanOrdinal, yunInCurrentYuan);

        return new SanYuanJiuYunResult(year, yuan, yun, yunInCurrentYuan);
    }

    /**
     * 根据总元序号获取元名
     * @param totalYuanOrdinal 从基准年算起的总元序号 (0-based, 可以为负)
     * @return 元名 ("上元", "中元", "下元")
     */
    private static String getYuanName(int totalYuanOrdinal) {
        // 0 -> 上元, 1 -> 中元, 2 -> 下元, 3 -> 上元 (新周期)
        // 对于负数，也需要使用 floorMod 确保索引为正
        int yuanIndexInCycle = Math.floorMod(totalYuanOrdinal, 3);
        switch (yuanIndexInCycle) {
            case 0: return "上元";
            case 1: return "中元";
            case 2: return "下元";
            default: return "error"; // 理论上不会到达
        }
    }

    /**
     * 根据元序号和元内运序号计算全局运数 (1-9)
     * @param totalYuanOrdinal 从基准年算起的总元序号 (0-based, 可以为负)
     * @param yunInCurrentYuan 在当前元内的运序号 (1-3)
     * @return 全局运数 (1-9)
     */
    private static int getYunNumber(int totalYuanOrdinal, int yunInCurrentYuan) {
        // 上元: 运1, 2, 3
        // 中元: 运4, 5, 6
        // 下元: 运7, 8, 9
        // 公式: (元序号 * 3) + 元内运序号
        // 对于负数元，同样使用 floorMod 来确定其在 3 元周期中的位置
        int yuanIndexInCycle = Math.floorMod(totalYuanOrdinal, 3);
        return yuanIndexInCycle * 3 + yunInCurrentYuan;
    }
}
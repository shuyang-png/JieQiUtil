package io.github.shuyang.service;

import io.github.shuyang.entity.SanYuanJiuYunResult;

/**
 * 三元九运计算服务。
 * <p>
 * 三元九运是中国传统风水学中以 180 年为一周期的时间划分方法。
 * 每 60 年为一元（上元、中元、下元），每 20 年为一运。
 * </p>
 *
 * <p>周期规律：</p>
 * <ul>
 *   <li>上元：一运(1864-1883)、二运(1884-1903)、三运(1904-1923)</li>
 *   <li>中元：四运(1924-1943)、五运(1944-1963)、六运(1964-1983)</li>
 *   <li>下元：七运(1984-2003)、八运(2004-2023)、九运(2024-2043)</li>
 * </ul>
 *
 * <p>基准点：1864 年为上元一运的开始。</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * SanYuanJiuYunService service = new SanYuanJiuYunService();
 * SanYuanJiuYunResult result = service.calculate(2026);
 * result.getYuan();  // "下元"
 * result.getYun();   // 8
 * }</pre>
 */
public class SanYuanJiuYunService {

    /* 上元一运的起始年份：1864 年 */
    private static final int BASE_YEAR = 1864;
    /* 每运持续年数：20 年 */
    private static final int YEARS_PER_YUN = 20;
    /* 每元持续年数：60 年（含 3 运） */
    private static final int YEARS_PER_YUAN = 60;

    /**
     * 计算指定年份的三元九运信息。
     * <p>
     * 算法：
     * 1. 计算从基准年 1864 年到目标年份的年数差
     * 2. 年数差 ÷ 20 得到运序号，÷ 60 得到元序号
     * 3. 元序号 % 3 确定上/中/下元
     * 4. 元内序号 (1-3) 结合元序号计算全局运数 (1-9)
     * 5. 使用 floorDiv/floorMod 确保负年份也能正确计算
     * </p>
     *
     * @param year 公历年份
     * @return 包含元、运信息的计算结果
     */
    public SanYuanJiuYunResult calculate(int year) {
        /* 计算从基准年到目标年份经过的总年数 */
        int totalYearsSinceBase = year - BASE_YEAR;
        /* 总运序号（从 0 开始，每 20 年 +1） */
        int totalYunOrdinal = Math.floorDiv(totalYearsSinceBase, YEARS_PER_YUN);
        /* 总元序号（从 0 开始，每 60 年 +1） */
        int totalYuanOrdinal = Math.floorDiv(totalYearsSinceBase, YEARS_PER_YUAN);
        /* 当前元内的运序号：总运序号 % 3 + 1，得到 1-3 */
        int yunInCurrentYuan = (Math.floorMod(totalYunOrdinal, 3) + 1);
        /* 获取元名称和全局运数 */
        String yuan = getYuanName(totalYuanOrdinal);
        int yun = getYunNumber(totalYuanOrdinal, yunInCurrentYuan);
        return new SanYuanJiuYunResult(year, yuan, yun, yunInCurrentYuan);
    }

    /**
     * 根据总元序号获取元名称。
     * 0 → 上元, 1 → 中元, 2 → 下元, 3 → 上元（新周期）
     */
    private static String getYuanName(int totalYuanOrdinal) {
        /* floorMod 确保负数的模结果也为正 */
        int yuanIndexInCycle = Math.floorMod(totalYuanOrdinal, 3);
        switch (yuanIndexInCycle) {
            case 0: return "上元";
            case 1: return "中元";
            case 2: return "下元";
            default: return "error";
        }
    }

    /**
     * 根据元序号和元内运序号计算全局运数 (1-9)。
     * 上元：运 1, 2, 3   中元：运 4, 5, 6   下元：运 7, 8, 9
     */
    private static int getYunNumber(int totalYuanOrdinal, int yunInCurrentYuan) {
        int yuanIndexInCycle = Math.floorMod(totalYuanOrdinal, 3);
        /* 公式：元序号(0-2) × 3 + 元内运序号(1-3) */
        return yuanIndexInCycle * 3 + yunInCurrentYuan;
    }
}

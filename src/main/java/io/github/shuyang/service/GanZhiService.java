package io.github.shuyang.service;

import io.github.shuyang.entity.GanZhi;
import io.github.shuyang.entity.SolarTerm;
import io.github.shuyang.exception.JieQiException;
import io.github.shuyang.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 干支计算服务。
 * <p>
 * 用于计算年、月、日、时的天干地支，
 * 支持一次性计算出完整的四柱干支。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * JieQiService jieQiService = new JieQiService();
 * GanZhiService service = new GanZhiService(jieQiService);
 * GanZhi ganZhi = service.calculateGanZhi(2026, 2, 25, 21);
 * }</pre>
 */
public class GanZhiService {
    private static final Logger log = LoggerFactory.getLogger(GanZhiService.class);

    /* 十二地支：子、丑、寅、卯、辰、巳、午、未、申、酉、戌、亥，索引 0-11 */
    private static final List<String> DIZHI_NAMES = List.of(
        "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"
    );

    /* 十天干：甲、乙、丙、丁、戊、己、庚、辛、壬、癸，索引 0-9 */
    private static final List<String> TIANGAN_NAMES = List.of(
        "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"
    );

    /* 地支名称 → 序号，1=子 ... 12=亥 */
    private static final Map<String, Integer> DIZHI_INDEX;
    /* 天干名称 → 序号，1=甲 ... 10=癸 */
    private static final Map<String, Integer> TIANGAN_INDEX;

    /*
     * 五虎遁：根据年干确定正月（寅月）的天干。
     * 口诀：甲己之年丙作首，乙庚之岁戊为头，丙辛必定寻庚起，丁壬壬位顺行流，若问戊癸何方发，甲寅之上好追求。
     * key = 年干，value = 寅月天干
     */
    private static final Map<String, String> WUHU;
    /*
     * 五鼠遁：根据日干确定子时的天干。
     * 口诀：甲己还加甲，乙庚丙作初，丙辛从戊起，丁壬庚子居，戊癸何方发，壬子是真途。
     * key = 日干，value = 子时天干
     */
    private static final Map<String, String> WUSHU;

    /* 日干支推算基准：1984-01-01 */
    private static final LocalDate BASE_DATE = LocalDate.of(1984, 1, 1);

    static {
        /* 构建地支名称 → 序号索引 */
        Map<String, Integer> dzIdx = new LinkedHashMap<>();
        for (int i = 0; i < DIZHI_NAMES.size(); i++) {
            dzIdx.put(DIZHI_NAMES.get(i), i + 1);
        }
        DIZHI_INDEX = Collections.unmodifiableMap(dzIdx);

        /* 构建天干名称 → 序号索引 */
        Map<String, Integer> tgIdx = new LinkedHashMap<>();
        for (int i = 0; i < TIANGAN_NAMES.size(); i++) {
            tgIdx.put(TIANGAN_NAMES.get(i), i + 1);
        }
        TIANGAN_INDEX = Collections.unmodifiableMap(tgIdx);

        /* 初始化五虎遁映射 */
        Map<String, String> wuhu = new LinkedHashMap<>();
        wuhu.put("甲", "丙"); wuhu.put("己", "丙");
        wuhu.put("乙", "戊"); wuhu.put("庚", "戊");
        wuhu.put("丙", "庚"); wuhu.put("辛", "庚");
        wuhu.put("丁", "壬"); wuhu.put("壬", "壬");
        wuhu.put("戊", "甲"); wuhu.put("癸", "甲");
        WUHU = Collections.unmodifiableMap(wuhu);

        /* 初始化五鼠遁映射 */
        Map<String, String> wushu = new LinkedHashMap<>();
        wushu.put("甲", "甲"); wushu.put("己", "甲");
        wushu.put("乙", "丙"); wushu.put("庚", "丙");
        wushu.put("丙", "戊"); wushu.put("辛", "戊");
        wushu.put("丁", "庚"); wushu.put("壬", "庚");
        wushu.put("戊", "壬"); wushu.put("癸", "壬");
        WUSHU = Collections.unmodifiableMap(wushu);
    }

    private final JieQiService jieQiService;

    /**
     * @param jieQiService 节气计算服务，用于获取节气时间以确定月柱
     */
    public GanZhiService(JieQiService jieQiService) {
        this.jieQiService = jieQiService;
    }

    /**
     * 计算指定时间的年、月、日、时全部干支（四柱）。
     *
     * @param year  公历年份
     * @param month 公历月份 (1-12)
     * @param day   公历日期
     * @param hour  小时 (0-23)
     * @return 四柱干支对象
     */
    public GanZhi calculateGanZhi(int year, int month, int day, int hour, int minutes) {
        log.info("cal {}年{}月{}日{}时", year, month, day, hour);
        String ganZhiYear = getGanZhiYear(year, month, day, hour, minutes);
        String ganZhiMonth = getGanZhiMonth(ganZhiYear, year, month, day, hour);
        String ganZhiDay = getGanZhiDay(year, month, day);
        String ganZhiHour = getGanZhiHour(ganZhiDay, hour);
        return new GanZhi(ganZhiYear, ganZhiMonth, ganZhiDay, ganZhiHour);
    }

    /**
     * 计算年柱干支。以立春为年柱分界，立春前按上年干支，立春后按本年干支。
     * <p>
     * 年柱推算规则：
     * 1. 年份减 3 后取模 10（天干）、模 12（地支）
     * 2. 以立春为界，立春前用上年干支，立春后用本年干支
     * </p>
     *
     * @param year  公历年份
     * @param month 公历月份
     * @param day   公历日期
     * @param hour  小时
     * @return 年柱干支字符串
     */
    public String getGanZhiYear(int year, int month, int day, int hour, int minutes) {
        log.info("{}年{}月{}日{}时", year, month, day, hour);
        if (month == 0 || day == 0) {
            throw new JieQiException("无效的日期: " + year + "-" + month + "-" + day);
        }
        /* 年份减 3 后取余数：甲子对应 1,1，故年份 -3 使 4(甲子年)归 1 */
        int baseYear = year - 3;
        /* 获取当年立春时间，判断是否在立春之后 */
        LocalDateTime lichun = jieQiService.getJieQi(year, SolarTerm.LICHUN);
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minutes);
        boolean isAfterLichun = lichun.isBefore(dateTime);
        /* 公历 3 月（寅月）起算：3 月已过立春，直接按本年；3 月前需看是否在立春后 */
        boolean inLichunYear = month >= 3 || isAfterLichun;
        int effectiveYear = inLichunYear ? baseYear : baseYear - 1;
        /* 天干 = 有效年 % 10，地支 = 有效年 % 12（用 floorMod 处理负数） */
        int zhiIndex = Math.floorMod(effectiveYear, 12);
        int ganIndex = Math.floorMod(effectiveYear, 10);
        /* 余数 0 对应第 10/12 位（癸/亥），索引需要减 1 */
        String zhi = DIZHI_NAMES.get(zhiIndex == 0 ? 11 : zhiIndex - 1);
        String gan = TIANGAN_NAMES.get(ganIndex == 0 ? 9 : ganIndex - 1);
        log.info("年干支: {}年 {} -> {}{}", effectiveYear, year, gan, zhi);
        return gan + zhi;
    }

    /**
     * 计算月柱干支。以节气（节）为月柱分界。
     * <p>
     * 月柱推算规则：
     * 1. 每个公历月包含一个"节"和一个"气"
     * 2. "节"之前属上月，"节"之后至"气"之间属本月，"气"之后属下月
     * 3. 月干使用五虎遁法推算
     * </p>
     *
     * @param ganzhiYear 年柱干支
     * @param year       公历年份
     * @param month      公历月份
     * @param day        公历日期
     * @param hour       小时
     * @return 月柱干支字符串
     */
    public String getGanZhiMonth(String ganzhiYear, int year, int month, int day, int hour) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, 30);
        if (month < 1 || month > 12) {
            throw new JieQiException("无效的月份: " + month);
        }
        /* 从年干中提取天干部分（年干支的前一个字） */
        List<String> yearGanParts = StringUtil.splitByLength(ganzhiYear, 1);
        String yearGan = yearGanParts.get(0);
        /* 获取当前公历月对应的两个节气（节和气） */
        List<SolarTerm> termsInMonth = getTermsByGregorianMonth(month);
        if (termsInMonth.isEmpty()) {
            throw new JieQiException("月份 " + month + " 没有对应的节气");
        }
        SolarTerm jie = termsInMonth.get(0);
        SolarTerm zhong = termsInMonth.get(1);
        LocalDateTime jieTime = jieQiService.getJieQi(year, jie);
        LocalDateTime zhongTime = jieQiService.getJieQi(year, zhong);
        /*
         * 判断日期所属的月份干支：
         * - "节"之前 → 属上月，取前月的中气
         * - "节"与"气"之间 → 属本月，取本月的节
         * - "气"之后 → 属下月，取本月的节气
         */
        String targetJieQi;
        if (dateTime.isBefore(jieTime)) {
            List<SolarTerm> prevMonthTerms = getTermsByGregorianMonth(month == 1 ? 12 : month - 1);
            targetJieQi = prevMonthTerms.get(1).getChineseName();
            log.debug("月份 {}: 在 {} 之前，取前月的中气", month, jie.getChineseName());
        } else if (dateTime.isBefore(zhongTime)) {
            targetJieQi = jie.getChineseName();
            log.debug("月份 {}: 在 {} 和 {} 之间", month, jie.getChineseName(), zhong.getChineseName());
        } else {
            targetJieQi = zhong.getChineseName();
            log.debug("月份 {}: 在 {} 之后", month, zhong.getChineseName());
        }
        return calculateGanZhiForTerm(targetJieQi, yearGan);
    }

    /**
     * 计算日柱干支。以公历日期为基准，从 1984-01-01 推算。
     * <p>
     * 日柱推算规则：
     * 1. 计算目标日期与基准日 1984-01-01 的天数差
     * 2. 天干序号 = (天数 + 1) % 10，地支序号 = (天数 + 1 + 6) % 12
     * 3. 余数为 0 时分别对应第 10 位天干（癸）和第 12 位地支（亥）
     * </p>
     *
     * @param year  公历年份
     * @param month 公历月份
     * @param day   公历日期
     * @return 日柱干支字符串
     */
    public String getGanZhiDay(int year, int month, int day) {
        LocalDate d = LocalDate.of(year, month, day);
        long daysDiff = ChronoUnit.DAYS.between(BASE_DATE, d);
        log.info("基数年偏差：{}",daysDiff);
        /* 基准日天干序号：天数 +1 归零，使用 Math.floorMod 处理负天数（1984 年之前） */
        int targetGanIndex = Math.floorMod(daysDiff + 1, 10);
        /* 基准日地支序号：天数 +1 +6 归零 */
        int targetZhiIndex = Math.floorMod(daysDiff + 1 + 6, 12);
        targetGanIndex = targetGanIndex == 0 ? 10 : targetGanIndex;
        targetZhiIndex = targetZhiIndex == 0 ? 12 : targetZhiIndex;
        String gan = TIANGAN_NAMES.get(targetGanIndex - 1);
        String zhi = DIZHI_NAMES.get(targetZhiIndex - 1);
        log.debug("日干支: {}-{}-{} -> {}{}", year, month, day, gan, zhi);
        return gan + zhi;
    }

    /**
     * 计算时柱干支。以日柱天干为依据，使用五鼠遁法推算。
     * <p>
     * 时柱推算规则：
     * 1. 用五鼠遁确定子时天干
     * 2. 23 点为子时（特殊处理，属于次日夜半）
     * 3. 其他时辰按 2 小时一组，从子时开始顺推
     * </p>
     *
     * @param ganzhiDay 日柱干支
     * @param hour      小时 (0-23)
     * @return 时柱干支字符串
     */
    public String getGanZhiHour(String ganzhiDay, int hour) {
        List<String> ganzhi = StringUtil.splitByLength(ganzhiDay, 1);
        String dayGan = ganzhi.get(0);
        String gan, zhi;
        int ganIndex;
        /* 23 点属于晚子时，归属次日，用次日天干查五鼠遁 */
        if (hour == 23) {
            int todayGanIndex = TIANGAN_INDEX.get(dayGan);
            /* 推进到次日天干序号 */
            int nextGanIndex = todayGanIndex % 10 + 1;
            String nextDayGan = TIANGAN_NAMES.get(nextGanIndex - 1);
            /* 五鼠遁：次日天干 → 子时天干 */
            gan = WUSHU.get(nextDayGan);
            zhi = "子";
            log.debug("时干支 (23点 晚子时): 日干{}→次日{}→{}{}", dayGan, nextDayGan, gan, zhi);
            return gan + zhi;
        }
        /* 将 24 小时制转为地支序号：0-1→子(1), 2-3→丑(2), ..., 22-23→亥(12) */
        int zhiIndex;
        if (hour % 2 != 0) {
            /* 奇数小时（如 1,3,5...23）：属于该时辰的后半段，(hour+1)/2+1 */
            zhiIndex = (hour + 1) / 2 + 1;
        } else {
            /* 偶数小时（如 0,2,4...22）：属于该时辰的前半段，hour/2+1 */
            zhiIndex = hour / 2 + 1;
        }
        zhi = DIZHI_NAMES.get(zhiIndex - 1);
        /* 五鼠遁：由日干 → 子时天干，再 + 偏移量得到当前时辰天干 */
        String ziGan = WUSHU.get(dayGan);
        int ziGanIndex = TIANGAN_INDEX.get(ziGan);
        ganIndex = (ziGanIndex - 1 + zhiIndex) % 10;
        ganIndex = ganIndex == 0 ? 10 : ganIndex;
        gan = TIANGAN_NAMES.get(ganIndex - 1);
        log.debug("时干支: {}点 -> {}{}", hour, gan, zhi);
        return gan + zhi;
    }

    /**
     * 根据节气名称和年干计算月柱干支（五虎遁法）。
     * <p>
     * 算法：以寅月（正月）为基准，天干按五虎遁确定寅月天干，
     * 再根据目标节气对应的地支序号，计算与寅月的偏移量，
     * 最后将寅月天干顺推偏移量得到目标月天干。
     * </p>
     */
    private String calculateGanZhiForTerm(String jieQiName, String yearGan) {
        SolarTerm term = SolarTerm.fromChineseName(jieQiName);
        /* 五虎遁：年干 → 寅月天干 */
        String firstMonthGan = WUHU.get(yearGan);
        int firstMonthGanIndex = TIANGAN_INDEX.get(firstMonthGan);
        /* 目标节气对应的干支月地支序号，寅=3 */
        int monthZhiIndex = term.getStemBranchMonth();
        /* 计算目标月距寅月（农历正月）的偏移量 */
        int offset = (monthZhiIndex - 3 + 12) % 12;
        /* 从寅月天干顺推偏移量，得到目标月天干 */
        int monthGanIndex = (firstMonthGanIndex - 1 + offset) % 10 + 1;
        String monthGan = TIANGAN_NAMES.get(monthGanIndex - 1);
        String monthZhi = DIZHI_NAMES.get(monthZhiIndex - 1);
        log.debug("月干支: {} -> {}{}", jieQiName, monthGan, monthZhi);
        return monthGan + monthZhi;
    }

    /**
     * 获取指定公历月份对应的节气列表。
     * 每个公历月包含两个节气（节和气），按日期排序。
     */
    private List<SolarTerm> getTermsByGregorianMonth(int gregorianMonth) {
        List<SolarTerm> result = new ArrayList<>();
        for (SolarTerm term : SolarTerm.values()) {
            if (term.getGregorianMonth() == gregorianMonth) {
                result.add(term);
            }
        }
        result.sort(Comparator.comparingInt(SolarTerm::getEarliestDay));
        return result;
    }

    public static List<String> getDizhiNames() {
        return DIZHI_NAMES;
    }

    public static List<String> getTianganNames() {
        return TIANGAN_NAMES;
    }

    public static Map<String, Integer> getDizhiIndex() {
        return DIZHI_INDEX;
    }

    public static Map<String, Integer> getTianganIndex() {
        return TIANGAN_INDEX;
    }

    public static Map<String, String> getWuhu() {
        return WUHU;
    }

    public static Map<String, String> getWushu() {
        return WUSHU;
    }
}

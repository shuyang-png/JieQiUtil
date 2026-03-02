package io.github.shuyang.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GanZhiUtil {
    public static HashMap<Integer, String> dizhi = new HashMap<Integer, String>();
    public static HashMap<Integer, String> tiangan = new HashMap<Integer, String>();
    public static HashMap<String, Integer> dizhi2 = new HashMap<String, Integer>();
    public static HashMap<String, Integer> tiangan2 = new HashMap<String, Integer>();
    public static HashMap<String, String> WUHU = new HashMap<String, String>();
    public static HashMap<String, String> WUSHU = new HashMap<String, String>();
    private static final LocalDate BASE_DATE = LocalDate.of(1984, 1, 1);
    static {
        dizhi.put(1,"子");   dizhi2.put("子",1);
        dizhi.put(2,"丑");   dizhi2.put("丑",2);
        dizhi.put(3,"寅");   dizhi2.put("寅",3);
        dizhi.put(4,"卯");   dizhi2.put("卯",4);
        dizhi.put(5,"辰");   dizhi2.put("辰",5);
        dizhi.put(6,"巳");   dizhi2.put("巳",6);
        dizhi.put(7,"午");   dizhi2.put("午",7);
        dizhi.put(8,"未");   dizhi2.put("未",8);
        dizhi.put(9,"申");   dizhi2.put("申",9);
        dizhi.put(10,"酉");  dizhi2.put("酉",10);
        dizhi.put(11,"戌");  dizhi2.put("戌",11);;
        dizhi.put(12,"亥");  dizhi2.put("亥",12);
        tiangan.put(1,"甲"); tiangan2.put("甲",1);
        tiangan.put(2,"乙"); tiangan2.put("乙",2);
        tiangan.put(3,"丙"); tiangan2.put("丙",3);
        tiangan.put(4,"丁"); tiangan2.put("丁",4);
        tiangan.put(5,"戊"); tiangan2.put("戊",5);
        tiangan.put(6,"己"); tiangan2.put("己",6);
        tiangan.put(7,"庚"); tiangan2.put("庚",7);
        tiangan.put(8,"辛"); tiangan2.put("辛",8);
        tiangan.put(9,"壬"); tiangan2.put("壬",9);
        tiangan.put(10,"癸");tiangan2.put("癸",10);

        WUHU.put("甲","丙");  WUSHU.put("甲","甲");
        WUHU.put("己","丙");  WUSHU.put("己","甲");
        WUHU.put("乙","戊");  WUSHU.put("乙","丙");
        WUHU.put("庚","戊");  WUSHU.put("庚","丙");
        WUHU.put("丙","庚");  WUSHU.put("丙","戊");
        WUHU.put("辛","庚");  WUSHU.put("辛","戊");
        WUHU.put("丁","壬");  WUSHU.put("丁","庚");
        WUHU.put("壬","壬");  WUSHU.put("壬","庚");
        WUHU.put("戊","甲");  WUSHU.put("戊","壬");
        WUHU.put("癸","甲");  WUSHU.put("癸","壬");
    }
    public static List<String> splitTianAndDi(String str, int chunkSize){
        List<String> parts = new ArrayList<>();
        if (str == null || str.isEmpty() || chunkSize <=0){
            return parts;
        }
        int length = str.length();
        for (int i = 0;i < length; i+=chunkSize){
            int end = Math.min(i + chunkSize,length);
            parts.add(str.substring(i,end));
        }
        return parts;
    }
    //缺少分
    public static String getGanZhiYear(int year, int month, int day, int hour){
        if (month == 0 || day == 0){
            return "error";
        }
        String gan = "";
        String zhi = "";
        year = year - 3;
        int a = 0;
        LocalDateTime lichun = JieQiUtil.getJieQi(year,"立春");
        LocalDateTime dateTime1 = LocalDateTime.of(year, month, day, hour, 30);
        boolean isBefore = lichun.isBefore(dateTime1);

        boolean isAfterLichunInYear = month >= 3 || lichun.isBefore(dateTime1);
        // 先确定要使用的年份
        int effectiveYear = isAfterLichunInYear ? year : year - 1;

        a = effectiveYear % 12;
        zhi = dizhi.get(a);
        a = effectiveYear % 10;
        gan = tiangan.get(a);

        return gan + zhi;
    }
    public static String getGanZhiMonth(String ganzhi, int year, int month, int day, int hour){
        LocalDateTime dateTime1 = LocalDateTime.of(year, month, day, hour, 30);
        if (month < 1 || month > 12) {
            System.err.println("错误: 无效的月份 " + month);
            return "error";
        }
        String gz;
        String[] jieQiNames = JieQiUtil.JIEQINAME[month - 1];
        //跨年的月
        String pre = (month > 1) ? JieQiUtil.JIEQINAME[month - 2][1] : JieQiUtil.JIEQINAME[11][1];

        LocalDateTime jieqi = JieQiUtil.getJieQi(year,jieQiNames[0]);
        LocalDateTime zhongqi = JieQiUtil.getJieQi(year,jieQiNames[1]);

        if (dateTime1.isBefore(jieqi)){
            System.out.println("month1");
            return calculateGanZhi(pre,ganzhi);
        }else if (dateTime1.isBefore(zhongqi)){
            System.out.println("month2");
            return calculateGanZhi(jieQiNames[0],ganzhi);
        }else {
            System.out.println("month3");
            return calculateGanZhi(jieQiNames[1],ganzhi);
        }
    }
    public static String getGanZhiDay(int year, int month, int day){
        LocalDate d = LocalDate.of(year, month, day);
        long daysDiff = ChronoUnit.DAYS.between(BASE_DATE, d);
        int targetGanIndex = (int) ((daysDiff + 1) % 10);
        int targetZhiIndex = (int) ((daysDiff + 1 + 6) % 12);
        String gan;
        String zhi;
        targetGanIndex = targetGanIndex == 0 ? 10 : targetGanIndex;
        targetZhiIndex = targetZhiIndex == 0 ? 12 :targetZhiIndex;
        gan = tiangan.get(targetGanIndex);
        zhi = dizhi.get(targetZhiIndex);
        System.out.println(daysDiff + "," + gan + "," + zhi);
        return gan + zhi;
    }
    public static String getGanZhiHour(String ganzhiday,int hour){
        List<String> ganzhi = splitTianAndDi(ganzhiday, 1);
        String gan,zhi;
        int ganIndex,zhiIndex;

        if (hour == 23){
            ganIndex = tiangan2.get(ganzhi.get(0));
            ganIndex = (ganIndex + 1) % 10;
            ganIndex = ganIndex == 0 ? 10 : ganIndex;
            gan = tiangan.get(ganIndex);
            zhi = "子";
            return gan + zhi;
        }
        if (hour % 2 != 0) {
            hour =(hour + 1) / 2 + 1;
        }else {
            hour = hour / 2 + 1;
        }
        zhi = dizhi.get(hour);
        System.out.println(hour + "," + zhi);

        // 先取日天干,五鼠遁 出 子时天干
        String zigan = WUSHU.get(ganzhi.get(0));
        System.out.println("子天干："+zigan);

        int ziganIndex = tiangan2.get(zigan);
        ganIndex = (ziganIndex - 1 + hour) % 10;
        gan = tiangan.get(ganIndex);
        System.out.println(gan + "," + ganIndex);
        // 计算偏移
        return gan + zhi;
    }
    private static String calculateGanZhi(String jieQi,String ganzhi){
        List<String> yearGan;
        String g;
        //接收干、支
        yearGan = splitTianAndDi(ganzhi,1);
        //接受干
        g = yearGan.get(0);
        String monthGan;
        String monthZhi;
        //目标月 距离寅月偏移多少月（寅月为1年月始）
        int offset;
        //转回月
        int monthGan2index;
        int monthZhi2index;
        String firstMonthGan = GanZhiUtil.WUHU.get(g);
        int firstMonthGan2index = GanZhiUtil.tiangan2.get(firstMonthGan);
        System.out.println(firstMonthGan + firstMonthGan2index);
        //首月为寅 1
        //String firstMonthZhi = "寅";
        //System.out.println(firstMonthGan);
        // 新位置 = (原位置 - 3 + 12) % 12 + 1  寅->1 - 申->7 = 6
        // 庚->7 偏移6位后  (n-1+a)%10 + 1   (7-1+6)%10+1 = 2+1 = 3
        monthZhi = GanZhiUtil.dizhi.get(JieQiUtil.jieQi2month.get(jieQi));
        monthZhi2index = GanZhiUtil.dizhi2.get(monthZhi);
        int newIndex = (monthZhi2index - 3 + 12) % 12 + 1;
        offset = newIndex - 1;
        System.out.println(offset);
        //距离首月偏移后的月
        monthGan2index = (firstMonthGan2index - 1 + offset) %10 + 1;
        monthGan = GanZhiUtil.tiangan.get(monthGan2index);
        System.out.println("属于1" + jieQi + ",天干:" + monthGan + ",地支:" + monthZhi);
        return monthGan + monthZhi;
    }
}

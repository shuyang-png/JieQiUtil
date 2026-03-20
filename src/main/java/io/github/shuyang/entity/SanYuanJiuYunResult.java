package io.github.shuyang.entity;

public class SanYuanJiuYunResult {
    private final int year;
    private final String yuan; // 元
    private final int yun;     // 全局运数 (1-9)
    private final int yunInCurrentYuan; // 元内运数 (1-3)

    public SanYuanJiuYunResult(int year, String yuan, int yun, int yunInCurrentYuan) {
        this.year = year;
        this.yuan = yuan;
        this.yun = yun;
        this.yunInCurrentYuan = yunInCurrentYuan;
    }

    // Getters
    public int getYear() { return year; }
    public String getYuan() { return yuan; }
    public int getYun() { return yun; }
    public int getYunInCurrentYuan() { return yunInCurrentYuan; }

    @Override
    public String toString() {
        return String.format("年份: %d, 元: %s, 运 (全局): %d, 运 (元内): %d", year, yuan, yun, yunInCurrentYuan);
    }
}

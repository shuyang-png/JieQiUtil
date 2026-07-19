package io.github.shuyang.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类。
 * <p>
 * 提供通用的字符串处理方法。
 * 私有构造器防止实例化。
 * </p>
 */
public class StringUtil {

    private StringUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 按指定长度分割字符串。
     *
     * @param str       待分割的字符串
     * @param chunkSize 每段长度
     * @return 分割后的字符串列表
     */
    public static List<String> splitByLength(String str, int chunkSize) {
        List<String> parts = new ArrayList<>();
        if (str == null || str.isEmpty() || chunkSize <= 0) {
            return parts;
        }
        int length = str.length();
        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(i + chunkSize, length);
            parts.add(str.substring(i, end));
        }
        return parts;
    }
}

package io.github.shuyang;

import io.github.shuyang.util.StringUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilTest {

    @Test
    @DisplayName("正常按长度分割字符串")
    void testSplitByLength_Normal() {
        List<String> result = StringUtil.splitByLength("丙午甲午", 2);
        assertEquals(2, result.size());
        assertEquals("丙午", result.get(0));
        assertEquals("甲午", result.get(1));
    }

    @Test
    @DisplayName("恰好整除的分割")
    void testSplitByLength_ExactChunk() {
        List<String> result = StringUtil.splitByLength("甲乙丙丁", 2);
        assertEquals(2, result.size());
        assertEquals("甲乙", result.get(0));
        assertEquals("丙丁", result.get(1));
    }

    @Test
    @DisplayName("不能整除的分割")
    void testSplitByLength_NonExactChunk() {
        List<String> result = StringUtil.splitByLength("甲乙丙", 2);
        assertEquals(2, result.size());
        assertEquals("甲乙", result.get(0));
        assertEquals("丙", result.get(1));
    }

    @Test
    @DisplayName("单字符分割")
    void testSplitByLength_SingleChar() {
        List<String> result = StringUtil.splitByLength("丙午", 1);
        assertEquals(2, result.size());
        assertEquals("丙", result.get(0));
        assertEquals("午", result.get(1));
    }

    @Test
    @DisplayName("chunkSize 大于字符串长度")
    void testSplitByLength_ChunkLargerThanString() {
        List<String> result = StringUtil.splitByLength("丙", 5);
        assertEquals(1, result.size());
        assertEquals("丙", result.get(0));
    }

    @Test
    @DisplayName("null 输入返回空列表")
    void testSplitByLength_Null() {
        List<String> result = StringUtil.splitByLength(null, 2);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("空字符串输入返回空列表")
    void testSplitByLength_Empty() {
        List<String> result = StringUtil.splitByLength("", 2);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("非法 chunkSize (<=0) 返回空列表")
    void testSplitByLength_InvalidChunkSize() {
        List<String> result = StringUtil.splitByLength("丙午", 0);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        List<String> result2 = StringUtil.splitByLength("丙午", -1);
        assertNotNull(result2);
        assertTrue(result2.isEmpty());
    }
}

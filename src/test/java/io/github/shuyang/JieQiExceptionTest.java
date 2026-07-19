package io.github.shuyang;

import io.github.shuyang.exception.JieQiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JieQiExceptionTest {

    @Test
    @DisplayName("带消息的构造")
    void testExceptionMessage() {
        JieQiException ex = new JieQiException("无效的日期");
        assertEquals("无效的日期", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("带消息和原因的构造")
    void testExceptionMessageAndCause() {
        IllegalArgumentException cause = new IllegalArgumentException("root cause");
        JieQiException ex = new JieQiException("无效的日期", cause);
        assertEquals("无效的日期", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    @DisplayName("是 RuntimeException 的子类")
    void testExceptionIsRuntimeException() {
        JieQiException ex = new JieQiException("test");
        assertTrue(ex instanceof RuntimeException, "JieQiException 应为非受检异常");
    }
}

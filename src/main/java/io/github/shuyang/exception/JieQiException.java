package io.github.shuyang.exception;

public class JieQiException extends RuntimeException {
    public JieQiException(String message) {
        super(message);
    }

    public JieQiException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.itheima.health.exception;

/**
 * 预约时间不允许
 */
public class OrderTimeNotAllowedException extends BaseException {
    public OrderTimeNotAllowedException() {
        super();
    }

    public OrderTimeNotAllowedException(String message) {
        super(message);
    }

    public OrderTimeNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderTimeNotAllowedException(Throwable cause) {
        super(cause);
    }

    protected OrderTimeNotAllowedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

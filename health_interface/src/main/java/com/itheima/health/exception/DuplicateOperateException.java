package com.itheima.health.exception;

/**
 * 重复操作
 */
public class DuplicateOperateException extends BaseException {
    public DuplicateOperateException() {
        super();
    }

    public DuplicateOperateException(String message) {
        super(message);
    }

    public DuplicateOperateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateOperateException(Throwable cause) {
        super(cause);
    }

    protected DuplicateOperateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

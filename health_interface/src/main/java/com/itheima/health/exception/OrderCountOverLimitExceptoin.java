package com.itheima.health.exception;

public class OrderCountOverLimitExceptoin extends BaseException {
    public OrderCountOverLimitExceptoin() {
        super();
    }

    public OrderCountOverLimitExceptoin(String message) {
        super(message);
    }

    public OrderCountOverLimitExceptoin(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderCountOverLimitExceptoin(Throwable cause) {
        super(cause);
    }

    protected OrderCountOverLimitExceptoin(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

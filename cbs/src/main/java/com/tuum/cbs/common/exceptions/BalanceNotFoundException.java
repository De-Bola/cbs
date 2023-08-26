package com.tuum.cbs.common.exceptions;

public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException() {
        super();
    }

    public BalanceNotFoundException(String msg) {
        super(msg);
    }

    public BalanceNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }
}

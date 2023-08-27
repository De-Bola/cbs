package com.tuum.cbs.common.exceptions;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super();
    }

    public InsufficientFundsException(String str) {
        super(str);
    }

    public InsufficientFundsException(String str, Throwable thr) {
        super(str, thr);
    }
}

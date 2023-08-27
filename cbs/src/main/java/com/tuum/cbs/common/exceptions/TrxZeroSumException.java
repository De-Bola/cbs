package com.tuum.cbs.common.exceptions;

public class TrxZeroSumException extends RuntimeException {
    public TrxZeroSumException() {
        super();
    }

    public TrxZeroSumException(String s) {
        super(s);
    }

    public TrxZeroSumException(String s, Throwable t) {
        super(s, t);
    }
}

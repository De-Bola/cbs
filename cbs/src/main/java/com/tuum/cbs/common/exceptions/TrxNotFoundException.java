package com.tuum.cbs.common.exceptions;

public class TrxNotFoundException extends RuntimeException {
    public TrxNotFoundException() {
        super();
    }

    public TrxNotFoundException(String s) {
        super(s);
    }

    public TrxNotFoundException(String s, Throwable t) {
        super(s, t);
    }
}

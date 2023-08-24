package com.tuum.cbs.common.exceptions;

public class BadRequestException extends Throwable {

    public BadRequestException() {
        super();
    }

    public BadRequestException(String s) {
        super(s);
    }

    public BadRequestException(String s, Throwable throwable) {
        super(s, throwable);
    }
}

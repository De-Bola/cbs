package com.tuum.cbs.common.exceptions;

public class AccountAlreadyException extends RuntimeException {
    public AccountAlreadyException(){
        super();
    }

    public AccountAlreadyException(String msg){
        super(msg);
    }

    public AccountAlreadyException(String msg, Throwable thr){
        super(msg, thr);
    }
}

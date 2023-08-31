package com.tuum.cbs.common.exceptions;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(){
        super();
    }

    public AccountNotFoundException(String msg){
        super(msg);
    }

    public AccountNotFoundException(String msg, Throwable throwable){
        super(msg, throwable);
    }

}

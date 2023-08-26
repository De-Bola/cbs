package com.tuum.cbs.common;

import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.controller.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public ErrorResponse notFoundException(Exception e){
        LOGGER.debug(e.getMessage(), e.getCause());
        return new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), e.getMessage(),TIMESTAMP);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse badRequestException(Exception e){
        LOGGER.debug(e.getMessage(), e.getCause());
        return new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage(),TIMESTAMP);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse JsonParseException(Exception e){
        LOGGER.debug(e.getMessage(), e.getCause());
        return new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage(),TIMESTAMP);
    }
}

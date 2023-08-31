package com.tuum.cbs.common;

import com.tuum.cbs.common.exceptions.*;
import com.tuum.cbs.controller.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({AccountNotFoundException.class, BalanceNotFoundException.class, TrxNotFoundException.class, HttpClientErrorException.NotFound.class})
    public ErrorResponse notFoundException(Exception e){
        LOGGER.debug(e.getMessage(), e.getCause());
        return new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), e.getMessage(), TIMESTAMP);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, InsufficientFundsException.class, TrxZeroSumException.class})
    public ErrorResponse badRequestException(Exception e){
        LOGGER.debug(e.getMessage(), e.getCause());
        return new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage(), TIMESTAMP);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse jsonParseException(Exception e){
        LOGGER.debug(e.getMessage(), e.getCause());
        return new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage(), TIMESTAMP);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse notSupportedException(HttpRequestMethodNotSupportedException ex) {
        LOGGER.debug(ex.getMessage(), ex.getCause());
        return new ErrorResponse(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()),"Method Not Allowed. Please verify you request", TIMESTAMP);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception ex) {
        LOGGER.error(ex.getMessage(), ex.getLocalizedMessage());
        return new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage(), TIMESTAMP);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptionHandler(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), errors.toString(), TIMESTAMP);
    }
}

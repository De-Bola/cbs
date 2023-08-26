package com.tuum.cbs.controller;

import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.controller.response.ErrorResponse;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    @PostMapping("/account-open")
    public ResponseEntity<SuccessResponse> createAccount(@RequestBody AccountDao accountDao) throws BadRequestException {
        // if currency list is larger than 4 items or is less than 1
        // attaching enum to currencies already handles not allowed currencies from request body
        int currencyListSize = accountDao.getCurrencies().size();
        if (currencyListSize > 4 ){
            throw new BadRequestException("Invalid currency: currency list larger than allowed values");
        }

        if (accountDao.getCurrencies() == null || accountDao.getCurrencies().isEmpty()){
            throw new BadRequestException("Invalid currency: currency list is empty");
        }

        if (accountDao.getCountry() == null || accountDao.getCountry().isEmpty()){
            throw new BadRequestException("Invalid entry: country cannot be blank");
        }

        if (accountDao.getCustomerId() == null || accountDao.getCustomerId().isEmpty()){
            throw new BadRequestException("Invalid entry: customer ID cannot be blank");
        }

        System.out.println(accountDao);
        Account account = service.save(accountDao);
        System.out.println(account);
        return new ResponseEntity<>(
                new SuccessResponse(account, "Account created!"),
                HttpStatus.CREATED
        );
    }
}

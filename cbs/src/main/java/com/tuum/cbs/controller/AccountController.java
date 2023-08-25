package com.tuum.cbs.controller;

import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    @PostMapping("/account-open")
    public ResponseEntity<SuccessResponse> createAccount(@RequestBody AccountDao accountDao){
        System.out.println(accountDao);
        Account account = service.save(accountDao);
        System.out.println(account);
        return new ResponseEntity<>(
                new SuccessResponse(account, "Account created!"),
                HttpStatus.CREATED
        );
    }
}

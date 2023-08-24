package com.tuum.cbs.controller;

import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Account;
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
    public ResponseEntity<SuccessResponse> createAccount(@RequestBody Account account) throws BadRequestException {
        if(!ObjectUtils.isEmpty(account.getAccountId())){
            throw new BadRequestException("New payloads don't have ID");
        }
        return new ResponseEntity<>(
                new SuccessResponse(service.save(account), "Account created!"),
                HttpStatus.CREATED
        );
    }
}

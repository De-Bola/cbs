package com.tuum.cbs.controller;

import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.service.AccountService;
import com.tuum.cbs.service.RabbitMQFOSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
@Slf4j
public class AccountController {

    private final AccountService service;
    private final RabbitMQFOSender mqFoSender;
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "AccountController";

    @PostMapping("/account-open")
    public ResponseEntity<SuccessResponse<Account>> createAccount(@RequestBody AccountDao accountDao) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " create account input: " + accountDao);
        Account account = service.save(accountDao);
        String accountId = account.getAccountId().toString();
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " created new account with id: " + accountId);
        mqFoSender.sendToFanoutXchange(account.toString());
        return new ResponseEntity<>(
                new SuccessResponse<Account>(account, "Account created!"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/account")
    public ResponseEntity<SuccessResponse<Account>> getAccount(@RequestParam(name = "id") String accountId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get account input: " + accountId);
        Account account = service.getByAccountId(accountId);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got account with id: " + accountId);
        return new ResponseEntity<>(
                new SuccessResponse<Account>(account, "Account found!"),
                HttpStatus.OK
        );
    }

}

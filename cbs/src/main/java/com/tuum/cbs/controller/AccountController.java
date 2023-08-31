package com.tuum.cbs.controller;

import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.service.AccountService;
import com.tuum.cbs.service.RabbitMQFOSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;
    private final RabbitMQFOSender mqFoSender;

    @PostMapping("/account-open")
    public ResponseEntity<SuccessResponse> createAccount(@RequestBody AccountDao accountDao) {
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
        // publish to queue for consumers
        mqFoSender.sendToFanoutXchange(account.toString());

        return new ResponseEntity<>(
                new SuccessResponse(account, "Account created!"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/account")
    public ResponseEntity<SuccessResponse> getAccount(@RequestParam(name = "id") String accountId) {
        System.out.println(accountId);
        Account account = service.getByAccountId(accountId);
        System.out.println(account);
        return new ResponseEntity<>(
                new SuccessResponse(account, "Account found!"),
                HttpStatus.OK
        );
    }

}

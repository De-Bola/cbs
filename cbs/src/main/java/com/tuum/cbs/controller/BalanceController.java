package com.tuum.cbs.controller;

import com.tuum.cbs.common.exceptions.BalanceNotFoundException;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.service.BalanceService;
import com.tuum.cbs.service.RabbitMQDESender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/balances")
public class BalanceController {

    private final BalanceService service;
    private final RabbitMQDESender mqDeSender;

    @GetMapping("/balance")
    public ResponseEntity<SuccessResponse> getBalance(@RequestParam(name = "accountId") String accountId, @RequestParam(name = "currency") Currency currency) {
        System.out.println("Params: " + accountId + " " + currency);
        Balance balance = service.getBalanceByAccountId(UUID.fromString(accountId), currency);
        System.out.println(balance);
        return new ResponseEntity<>(
                new SuccessResponse(balance, "Balance found!"),
                HttpStatus.OK
        );
    }

    @GetMapping("/balances")
    public ResponseEntity<SuccessResponse> getBalance(@RequestParam(name = "accountId") String accountId) {
        System.out.println("Params: " + accountId);
        List<Balance> balances = service.getBalanceByAccountId(UUID.fromString(accountId));
        if (balances.isEmpty()) throw new BalanceNotFoundException("Balances not found for this account!");
        System.out.println(balances);
        return new ResponseEntity<>(
                new SuccessResponse(balances, "Balance list found!"),
                HttpStatus.OK
        );
    }

    @GetMapping("/balance-balanceId")
    public ResponseEntity<SuccessResponse> getBalance(@RequestParam(name = "balanceId") Long balanceId) {
        System.out.println("Params: " + balanceId);
        Balance balance = service.getBalanceByBalanceId(balanceId);
        System.out.println(balance);
        return new ResponseEntity<>(
                new SuccessResponse(balance, "Balance found!"),
                HttpStatus.OK
        );
    }

    @PutMapping("/balance")
    public ResponseEntity<SuccessResponse> updateBalance(@RequestParam(name = "balanceId") Long balanceId, @RequestParam(name = "amount") BigDecimal amount) {
        System.out.println("Params: " + balanceId + " " + amount);
        Balance balance = service.updateBalance(balanceId, amount);
        System.out.println(balance);
        
        // publish to queue for consumer
        mqDeSender.publishToUpdateBalanceQueue(balance.toString());

        return new ResponseEntity<>(
                new SuccessResponse(balance, "Balance updated!"),
                HttpStatus.OK
        );
    }
}

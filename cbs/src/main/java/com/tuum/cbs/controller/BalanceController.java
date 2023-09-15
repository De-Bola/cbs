package com.tuum.cbs.controller;

import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.service.BalanceService;
import com.tuum.cbs.service.RabbitMQDESender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/balances")
@Slf4j
public class BalanceController {

    private final BalanceService service;
    private final RabbitMQDESender mqDeSender;
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "BalanceController";

    @GetMapping("/balance")
    public ResponseEntity<SuccessResponse<Balance>> getBalance(@RequestParam(name = "accountId") String accountId, @RequestParam(name = "currency") Currency currency) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balance input: " + accountId + " " + currency);
        Balance balance = service.getBalanceByAccountId(UUID.fromString(accountId), currency);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got " + currency + " balance for: " + accountId);
        return new ResponseEntity<>(
                new SuccessResponse<Balance>(balance, "Balance found!"),
                HttpStatus.OK
        );
    }

    @GetMapping("/balances")
    public ResponseEntity<SuccessResponse<List<Balance>>> getBalance(@RequestParam(name = "accountId") String accountId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balances input: " + accountId);
        List<Balance> balances = service.getBalanceByAccountId(UUID.fromString(accountId));
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got balances for: " + accountId);
        return new ResponseEntity<>(
                new SuccessResponse<List<Balance>>(balances, "Balance list found!"),
                HttpStatus.OK
        );
    }

    @GetMapping("/balance-balanceId")
    public ResponseEntity<SuccessResponse<Balance>> getBalance(@RequestParam(name = "balanceId") Long balanceId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balance for balance id: " + balanceId);
        Balance balance = service.getBalanceByBalanceId(balanceId);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got balance for: " + balanceId);
        return new ResponseEntity<>(
                new SuccessResponse<Balance>(balance, "Balance found!"),
                HttpStatus.OK
        );
    }

    @PutMapping("/balance")
    public ResponseEntity<SuccessResponse<Balance>> updateBalance(@RequestParam(name = "balanceId") Long balanceId, @RequestParam(name = "amount") BigDecimal amount) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " update balance for balance id: " + balanceId);
        Balance balance = service.updateBalance(balanceId, amount);
        // publish to queue for consumer
        mqDeSender.publishToUpdateBalanceQueue(balance.toString());
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " updated balance for balance id: " + balanceId);
        return new ResponseEntity<>(
                new SuccessResponse<Balance>(balance, "Balance updated!"),
                HttpStatus.OK
        );
    }
}

package com.tuum.cbs.controller;

import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.service.BalanceService;
import com.tuum.cbs.service.RabbitMQDESender;
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

@RestController
@RequestMapping("/api")
@Slf4j
public class BalanceController {

    private final BalanceService service;
    private final RabbitMQDESender mqDeSender;
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "BalanceController";

    public BalanceController(BalanceService service, RabbitMQDESender mqDeSender) {
        this.service = service;
        this.mqDeSender = mqDeSender;
    }

    @PostMapping("/balances")
    public ResponseEntity<SuccessResponse<List<Balance>>> createBalance(@RequestBody List<Balance> balances) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " create balance list: " + balances);
        List<Balance> balanceList = service.createBalance(balances);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " created new balance list with " + balanceList.size());
        mqDeSender.publishToCreateBalanceQueue(balanceList.toString());
        return new ResponseEntity<>(
                new SuccessResponse<>(balanceList, "Account created!"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/accounts/{accountId}/balances/{currency}")
    public ResponseEntity<SuccessResponse<Balance>> getBalance(@PathVariable String accountId, @PathVariable Currency currency) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balance input: " + accountId + " " + currency);
        Balance balance = service.getBalanceByAccountId(UUID.fromString(accountId), currency);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got " + currency + " balance for: " + accountId);
        return new ResponseEntity<>(
                new SuccessResponse<>(balance, "Balance found!"),
                HttpStatus.OK
        );
    }

    @GetMapping("/accounts/{accountId}/balances")
    public ResponseEntity<SuccessResponse<List<Balance>>> getBalance(@PathVariable String accountId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balances input: " + accountId);
        List<Balance> balances = service.getBalanceByAccountId(UUID.fromString(accountId));
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got balances for: " + accountId);
        return new ResponseEntity<>(
                new SuccessResponse<>(balances, "Balance list found!"),
                HttpStatus.OK
        );
    }

    @GetMapping("/balances/{balanceId}")
    public ResponseEntity<SuccessResponse<Balance>> getBalance(@PathVariable Long balanceId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balance for balance id: " + balanceId);
        Balance balance = service.getBalanceByBalanceId(balanceId);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got balance for: " + balanceId);
        return new ResponseEntity<>(
                new SuccessResponse<>(balance, "Balance found!"),
                HttpStatus.OK
        );
    }

    @PutMapping("/balances/{balanceId}/{amount}")
    public ResponseEntity<SuccessResponse<Balance>> updateBalance(@PathVariable Long balanceId, @PathVariable BigDecimal amount) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " update balance for balance id: " + balanceId);
        Balance balance = service.updateBalance(balanceId, amount);
        // publish to queue for consumer
        mqDeSender.publishToUpdateBalanceQueue(balance.toString());
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " updated balance for balance id: " + balanceId);
        return new ResponseEntity<>(
                new SuccessResponse<>(balance, "Balance updated!"),
                HttpStatus.OK
        );
    }
}

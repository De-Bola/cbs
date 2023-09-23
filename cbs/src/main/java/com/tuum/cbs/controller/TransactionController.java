package com.tuum.cbs.controller;

import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Transaction;
import com.tuum.cbs.models.TransactionDao;
import com.tuum.cbs.service.RabbitMQDESender;
import com.tuum.cbs.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final RabbitMQDESender rabbitMQDESender;
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "TransactionController";

    public TransactionController(TransactionService transactionService, RabbitMQDESender rabbitMQDESender) {
        this.transactionService = transactionService;
        this.rabbitMQDESender = rabbitMQDESender;
    }

    @PostMapping("/transactions")
    public ResponseEntity<SuccessResponse<Transaction>> createTransaction(@RequestBody TransactionDao trxDao) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " create trx with input: " + trxDao);
        Transaction newTrx = transactionService.createTransaction(trxDao);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " created trx with id: " + newTrx.getTrxId());
        // publish to queue for consumers
        if (newTrx.getTrxType().name().equalsIgnoreCase("IN")){
            rabbitMQDESender.publishToTrxCreditQueue(newTrx.toString());
        } else rabbitMQDESender.publishToTrxDebitQueue(newTrx.toString());
        return new ResponseEntity<>(
                new SuccessResponse<>(newTrx, "Transaction created!"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<SuccessResponse<List<Transaction>>> getTransactions(@PathVariable String accountId){
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get trx(s) for account with id: " + accountId);
        List<Transaction> transactions = transactionService.getTrxByAccountId(UUID.fromString(accountId));
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got " + transactions.size() + " trx(s) for account with id: " + accountId);
        return new ResponseEntity<>(
                new SuccessResponse<>(transactions, "Transactions found!"),
                HttpStatus.OK
        );
    }
}

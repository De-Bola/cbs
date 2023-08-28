package com.tuum.cbs.controller;

import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.common.exceptions.TrxNotFoundException;
import com.tuum.cbs.common.exceptions.TrxZeroSumException;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Transaction;
import com.tuum.cbs.models.TransactionDao;
import com.tuum.cbs.service.RabbitMQDESender;
import com.tuum.cbs.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final RabbitMQDESender rabbitMQDESender;

    @PostMapping("/transaction-create")
    public ResponseEntity<SuccessResponse> createTransaction(@RequestBody TransactionDao trxDao) {

        if (trxDao.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Invalid amount: amount cannot be negative");
        }

        if (trxDao.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new TrxZeroSumException("Invalid amount: can't post zero");
        }

        if (trxDao.getCurrency() == null ){
            throw new BadRequestException("Invalid currency: currency absent");
        }

        if (trxDao.getTrxType() == null){
            throw new BadRequestException("Invalid direction: trx direction cannot be blank");
        }

        if (trxDao.getAccountId() == null ){
            throw new BadRequestException("Account missing: account ID cannot be blank");
        }

        if (trxDao.getDescription().isEmpty() || trxDao.getDescription() == null ){
            throw new BadRequestException("Description missing: description cannot be blank");
        }

        System.out.println("Trx params: " + trxDao);
        Transaction newTrx = transactionService.createTransaction(trxDao);
        System.out.println(newTrx);

        // publish to queue for consumers
        if (newTrx.getTrxType().name().equalsIgnoreCase("IN")){
            rabbitMQDESender.publishToTrxCreditQueue(newTrx.toString());
        } else rabbitMQDESender.publishToTrxDebitQueue(newTrx.toString());

        return new ResponseEntity<>(
                new SuccessResponse(newTrx, "Transaction created!"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/get")
    public ResponseEntity<SuccessResponse> getTransactions(@RequestParam(name = "id") String accountId){
        System.out.println(accountId);
        List<Transaction> transactions = transactionService.getTrxByAccountId(UUID.fromString(accountId));
        if (transactions.isEmpty()) throw new TrxNotFoundException("No Transactions found!");
        System.out.println("Number of Trx found: " + transactions.size());
        return new ResponseEntity<>(
                new SuccessResponse(transactions, "Transactions found!"),
                HttpStatus.OK
        );
    }
}

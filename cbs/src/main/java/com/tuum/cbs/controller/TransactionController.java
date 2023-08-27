package com.tuum.cbs.controller;

import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Transaction;
import com.tuum.cbs.models.TransactionDao;
import com.tuum.cbs.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction-create")
    public ResponseEntity<SuccessResponse> createTransaction(@RequestBody TransactionDao trxDao) {

        if (trxDao.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Invalid amount: amount cannot be negative");
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
        return new ResponseEntity<>(
                new SuccessResponse(newTrx, "Transaction created!"),
                HttpStatus.CREATED
        );
    }
}

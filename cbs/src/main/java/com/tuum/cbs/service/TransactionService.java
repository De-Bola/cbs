package com.tuum.cbs.service;

import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Transaction;
import com.tuum.cbs.models.TransactionDao;
import com.tuum.cbs.models.TransactionType;
import com.tuum.cbs.repositories.AccountsRepository;
import com.tuum.cbs.repositories.TransactionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TransactionService {

    private final TransactionsRepository repo;
    private final AccountService accountService;
    private final BalanceService balanceService;

    public TransactionService(TransactionsRepository repo, AccountService accountService, BalanceService balanceService) {
        this.repo = repo;
        this.accountService = accountService;
        this.balanceService = balanceService;
    }

    /**
     * for creating a new trx
     * */
    public Transaction createTransaction(TransactionDao transactionDao){
        Transaction transaction;
        final Long transactionId = accountService.generateRandomId();

        BigDecimal newAmount = addSignToAmount(transactionDao.getAmount(), transactionDao.getTrxType());
        Balance newBalance = balanceService.updateBalanceByAccountId(transactionDao.getAccountId(), transactionDao.getCurrency(), newAmount);

        transaction = Transaction.builder()
                .accountId(transactionDao.getAccountId())
                .currency(transactionDao.getCurrency())
                .description(transactionDao.getDescription())
                .amount(transactionDao.getAmount()).trxId(transactionId)
                .trxType(transactionDao.getTrxType())
                .balanceAfterTrx(newBalance.getAmount())
                .build();

        System.out.println("Service layer: " + transaction);
        repo.insertTransaction(transaction);

        return transaction;
    }

    public List<Transaction> getTrxByAccountId(UUID accountId) {
        return repo.getTrxByAccountId(accountId);
    }

    public BigDecimal addSignToAmount(BigDecimal amount, TransactionType trxType){
        if (trxType.name().equalsIgnoreCase("IN")) {return amount;}
        else return amount.negate();
    }
}

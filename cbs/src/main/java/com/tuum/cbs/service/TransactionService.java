package com.tuum.cbs.service;

import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Transaction;
import com.tuum.cbs.models.TransactionDao;
import com.tuum.cbs.models.TransactionType;
import com.tuum.cbs.repositories.CbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class TransactionService {

    private final CbsRepository repo;
    private final AccountService accountService;
    private final BalanceService balanceService;

    // business logic for transactions
    /**
     * for creating a new trx
     * */
    public Transaction createTransaction(TransactionDao transactionDao){
        Transaction transaction;
        final Long transactionId = accountService.generateRandomId();

        // set the sign based on trxType
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

    // plan to handle 'Insufficient funds' in balance service layer

    public List<Transaction> getTrxByAccountId(UUID accountId) {
        return repo.getTrxByAccountId(accountId);
    }

    // for simplicity
    public BigDecimal addSignToAmount(BigDecimal amount, TransactionType trxType){
        if (trxType.name().equalsIgnoreCase("IN")) {return amount;}
        else return amount.negate();
    }
}

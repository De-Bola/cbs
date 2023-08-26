package com.tuum.cbs.service;

import com.tuum.cbs.models.Transaction;
import com.tuum.cbs.models.TransactionDao;
import com.tuum.cbs.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class TransactionService {

    private final AccountRepository repo;

    // business logic for transactions
    /**
     * for creating a new trx
     * */
    public Transaction createTransaction(TransactionDao transactionDao){
        return new Transaction();
    }
}

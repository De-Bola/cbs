package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.InsufficientFundsException;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Transaction;
import com.tuum.cbs.models.TransactionDao;
import com.tuum.cbs.repositories.CbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        final Long transactionId = accountService.generateRandomId();
        Balance balance = balanceService
                .getBalanceByAccountId(transactionDao.getAccountId(), transactionDao.getCurrency());

        BigDecimal newAmount = transactionDao.getAmount().abs().multiply(
                new BigDecimal(transactionDao.getTrxType().mime.concat("1")));

        newAmount = balance.getAmount().add(newAmount);

        // insufficient funds is only valid for outgoing trx at this point
        if (newAmount.compareTo(BigDecimal.ZERO) < 0 &&
                transactionDao.getTrxType().name().equalsIgnoreCase("OUT")){
            throw new InsufficientFundsException("Insufficient funds: ");
        }

        balance.setAmount(newAmount);

        Transaction transaction = Transaction.builder()
                .accountId(transactionDao.getAccountId())
                .currency(transactionDao.getCurrency())
                .description(transactionDao.getDescription())
                .amount(transactionDao.getAmount()).trxId(transactionId)
                .trxType(transactionDao.getTrxType())
                .balanceAfterTrx(balance)
                .build();

        repo.insertTransaction(transaction);

        return transaction;
    }

    // plan to handle 'Insufficient funds' in service layer

    public Transaction getTrxByAccountId(UUID accountId) {
        return repo.getTrxByAccountId(accountId);
    }
}

package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.common.exceptions.TrxNotFoundException;
import com.tuum.cbs.common.exceptions.TrxZeroSumException;
import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.common.util.TrxSignUtil;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Transaction;
import com.tuum.cbs.models.TransactionDao;
import com.tuum.cbs.repositories.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
@Slf4j
public class TransactionService {

    private final TransactionsRepository repo;
    private final BalanceService balanceService;
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "TransactionService";

    public TransactionService(TransactionsRepository repo, BalanceService balanceService) {
        this.repo = repo;
        this.balanceService = balanceService;
    }

    /**
     * for creating a new trx
     * */
    public Transaction createTransaction(TransactionDao transactionDao){
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " create trx with input: " + transactionDao);
        if (transactionDao.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Invalid amount: amount cannot be negative");
        }

        if (transactionDao.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new TrxZeroSumException("Invalid amount: can't post zero");
        }

        if (transactionDao.getCurrency() == null ){
            throw new BadRequestException("Invalid currency: currency absent");
        }

        if (transactionDao.getTrxType() == null){
            throw new BadRequestException("Invalid direction: trx direction cannot be blank");
        }

        if (transactionDao.getAccountId() == null ){
            throw new BadRequestException("Account missing: account ID cannot be blank");
        }

        if (transactionDao.getDescription().isEmpty()){
            throw new BadRequestException("Description missing: description cannot be blank");
        }

        Transaction transaction;
        final Long transactionId = IdUtil.generateRandomId();

        BigDecimal newAmount = TrxSignUtil.addSignToAmount(transactionDao.getAmount(), transactionDao.getTrxType());
        Balance newBalance = balanceService
                .updateBalanceByAccountId(transactionDao.getAccountId(), transactionDao.getCurrency(), newAmount);

        transaction = Transaction.builder()
                .accountId(transactionDao.getAccountId())
                .currency(transactionDao.getCurrency())
                .description(transactionDao.getDescription())
                .amount(transactionDao.getAmount()).trxId(transactionId)
                .trxType(transactionDao.getTrxType())
                .balanceAfterTrx(newBalance.getAmount())
                .build();

        repo.insertTransaction(transaction);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " created trx with id: " + transactionId);
        return transaction;
    }

    /**
     * for getting all trx based on account id
     * */
    public List<Transaction> getTrxByAccountId(UUID accountId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get trx(s) for account with id: " + accountId);
        List<Transaction> transactions = repo.getTrxByAccountId(accountId);
        if (transactions.isEmpty()) throw new TrxNotFoundException("No Transactions found!");
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got trx(s) for account with id: " + accountId);
        return transactions;
    }
}

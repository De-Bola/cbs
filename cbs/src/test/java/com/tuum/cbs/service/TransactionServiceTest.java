package com.tuum.cbs.service;

import com.tuum.cbs.models.*;
import com.tuum.cbs.repositories.CbsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TransactionServiceTest {

    @Mock
    private CbsRepository repo;

    @InjectMocks
    private TransactionService uut;

    @Captor
    private ArgumentCaptor<Transaction> captor;

    private TransactionDao testTrxDao;
    private Transaction testTrx;
    private String balanceId;
    private Balance balance;
    private Balance balanceAfterTrx;


    @BeforeEach
    void setUp() {
        final Currency currency = Currency.EUR;
        final UUID accountId = UUID.randomUUID();
        String trxId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        trxId = trxId.substring(trxId.length() - 10);

        balanceId = String.format("%010d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        balanceId = balanceId.substring(balanceId.length() - 10);

        testTrxDao = TransactionDao.builder()
                .accountId(accountId)
                .amount(new BigDecimal("50.0"))
                .currency(currency)
                .trxType(TransactionType.IN)
                .description("I am inevitable")
                .build();

        balance = Balance.builder().balanceId(Long.valueOf(balanceId))
                .accountId(accountId).amount(new BigDecimal("6.0"))
                .currency(currency).build();
        balanceAfterTrx = Balance.builder().balanceId(Long.valueOf(balanceId))
                .accountId(accountId).amount(balance.getAmount().add(testTrxDao.getAmount()))
                .currency(currency).build();

        testTrx = Transaction.builder().accountId(testTrxDao.getAccountId())
                .trxId(Long.valueOf(trxId)).trxType(testTrxDao.getTrxType())
                .amount(testTrxDao.getAmount()).balanceAfterTrx(balanceAfterTrx)
                .currency(currency).description(testTrxDao.getDescription())
                .build();

        repo = mock(CbsRepository.class);
        uut = new TransactionService(repo);
    }

    @Test
    void checkContextStarts() {
        assertThat(uut).isNotNull();
    }

    @Test
    void createTransactionShouldTakeDaoAndReturnTrxObj() {

    }
}
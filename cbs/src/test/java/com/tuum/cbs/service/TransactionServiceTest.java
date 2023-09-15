package com.tuum.cbs.service;

import com.tuum.cbs.models.*;
import com.tuum.cbs.repositories.AccountsRepository;
import com.tuum.cbs.repositories.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@MybatisTest
class TransactionServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Mock
    private TransactionsRepository repo;

    @InjectMocks
    private TransactionService uut;

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceService balanceService;

    @Captor
    private ArgumentCaptor<Transaction> captor;

    private TransactionDao testTrxDao;
    private Transaction testTrx;
    private final List<Transaction> transactions = new ArrayList<>();

    private Balance balance;


    @BeforeEach
    void setUp() {
        final Currency currency = Currency.EUR;
        final UUID accountId = UUID.randomUUID();
        String trxId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        trxId = trxId.substring(trxId.length() - 10);

        String balanceId = String.format("%010d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        balanceId = balanceId.substring(balanceId.length() - 10);

        testTrxDao = TransactionDao.builder()
                .accountId(accountId)
                .amount(new BigDecimal("50.0"))
                .currency(currency)
                .trxType(TransactionType.IN)
                .description("I am inevitable")
                .build();

        balance = Balance.builder().balanceId(Long.valueOf(balanceId))
                .accountId(accountId).amount(new BigDecimal("600.0"))
                .currency(currency).build();
        Balance balanceAfterTrx = Balance.builder().balanceId(Long.valueOf(balanceId))
                .accountId(accountId).amount(balance.getAmount().add(testTrxDao.getAmount()))
                .currency(currency).build();

        testTrx = Transaction.builder().accountId(testTrxDao.getAccountId())
                .trxId(Long.valueOf(trxId)).trxType(testTrxDao.getTrxType())
                .amount(testTrxDao.getAmount()).balanceAfterTrx(balanceAfterTrx.getAmount())
                .currency(currency).description(testTrxDao.getDescription())
                .build();

        transactions.add(testTrx);

        repo = mock(TransactionsRepository.class);
        uut = new TransactionService(repo, accountService, balanceService);
    }

    @Test
    void checkContextStarts() {
        assertThat(uut).isNotNull();
    }

    @Test
    void createTransactionShouldTakeDaoAndReturnTrxObj() {
        //given this stub return number of inserts
        when(accountService.generateRandomId()).thenReturn(testTrx.getTrxId());
        when(balanceService.updateBalanceByAccountId(any(UUID.class), any(Currency.class), any(BigDecimal.class))).thenReturn(balance);
        given(repo.insertTransaction(captor.capture())).willReturn(1);
        Transaction createdTrx = uut.createTransaction(testTrxDao);
        assertEquals(testTrxDao.getAccountId(), createdTrx.getAccountId());
        verify(repo, times(1)).insertTransaction(captor.capture());
        assertThat(createdTrx).isNotNull();
        assertThat(createdTrx).hasSameClassAs(testTrx);
        assertThat(createdTrx.getBalanceAfterTrx()).isNotNull();
        assertThat(createdTrx.getAmount()).isEqualByComparingTo(testTrxDao.getAmount());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(createdTrx);
    }

    @Test
    void createTransactionVerifiesThatINTrxIncrementsBalance() {
        final BigDecimal finalBalance = balance.getAmount().add(testTrxDao.getAmount());
        Balance newBalance = Balance.builder()
                .balanceId(balance.getBalanceId()).amount(finalBalance).accountId(balance.getAccountId())
                .currency(balance.getCurrency())
                .build();
        //given this stub return number of inserts
        when(accountService.generateRandomId()).thenReturn(testTrx.getTrxId());
        when(balanceService.updateBalanceByAccountId(any(UUID.class), any(Currency.class), any(BigDecimal.class))).thenReturn(newBalance);
        given(repo.insertTransaction(any(Transaction.class))).willReturn(1);
        Transaction createdTrx = uut.createTransaction(testTrxDao);
        verify(repo, times(1)).insertTransaction(captor.capture());
        assertThat(createdTrx.getTrxType()).isEqualByComparingTo(testTrx.getTrxType());
        assertThat(createdTrx.getBalanceAfterTrx()).isGreaterThan(balance.getAmount());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(createdTrx);
    }

    @Test
    void createTransactionVerifiesThatOUTTrxDecrementsBalance() {
        testTrxDao.setTrxType(TransactionType.OUT);
        testTrx.setTrxType(TransactionType.OUT);
        final BigDecimal finalBalance = balance.getAmount().add(testTrxDao.getAmount().negate());
        Balance newBalance = Balance.builder()
                .balanceId(balance.getBalanceId()).amount(finalBalance).accountId(balance.getAccountId())
                .currency(balance.getCurrency())
                .build();
        //given this stub return number of inserts
        when(accountService.generateRandomId()).thenReturn(testTrx.getTrxId());
        when(balanceService.updateBalanceByAccountId(any(UUID.class), any(Currency.class), any(BigDecimal.class))).thenReturn(newBalance);
        given(repo.insertTransaction(any(Transaction.class))).willReturn(1);
        Transaction createdTrx = uut.createTransaction(testTrxDao);
        verify(repo, times(1)).insertTransaction(captor.capture());
        assertThat(createdTrx.getTrxType()).isEqualByComparingTo(testTrx.getTrxType());
        assertThat(createdTrx.getAmount()).isLessThan(balance.getAmount());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(createdTrx);
    }

    @Test
    void getTrxByAccountIdShouldReturnTrx(){
        UUID accountId = testTrxDao.getAccountId();
        given(repo.getTrxByAccountId(accountId)).willReturn(transactions);
        List<Transaction> foundTrx = uut.getTrxByAccountId(accountId);
        assertThat(foundTrx).isNotNull();
        assertEquals(transactions.size(), foundTrx.size());
        assertEquals(accountId, foundTrx.get(0).getAccountId());
        verify(repo, times(1)).getTrxByAccountId(accountId);
    }

    @Test
    void getTrxByAccountIdShouldReturnMisMatch(){
        UUID accountId = UUID.fromString("eb01ed99-59fc-48f2-8bcc-5f245e3a17bd");
        given(repo.getTrxByAccountId(accountId)).willReturn(transactions);
        List<Transaction> foundTrx = uut.getTrxByAccountId(accountId);
        assertThat(foundTrx).isNotNull();
        assertNotEquals(accountId, foundTrx.get(0).getAccountId());
        verify(repo, times(1)).getTrxByAccountId(accountId);
    }
}
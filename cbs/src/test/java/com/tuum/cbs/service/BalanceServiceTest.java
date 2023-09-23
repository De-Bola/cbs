package com.tuum.cbs.service;

import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.AccountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@MybatisTest
class BalanceServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @InjectMocks
    private BalanceService uut;

    @Mock
    private AccountsRepository repo;

    @Mock
    private RabbitMQDESender mqDeSender;

    private Balance bal;
    private List<Balance> bal_List;
    private Long balanceId;



    @BeforeEach
    void setUp() {
        balanceId = IdUtil.generateRandomId();
        UUID accountId = IdUtil.generateUUID();
        bal = new Balance(balanceId, new BigDecimal("0.00"), Currency.EUR, accountId);
        bal_List = new ArrayList<>();
        bal_List.add(bal);
        repo = mock(AccountsRepository.class);
        uut = new BalanceService(repo, mqDeSender);
    }

    @Test
    void checkContextStarts() {
        assertThat(uut).isNotNull();
    }

    @Test
    void getBalanceByAccountIdShouldReturnBalance() {
        UUID accountId = UUID.fromString("eb01ed99-59fc-48f2-8bcc-5f245e3a17bd");
        given(repo.getAccountBalance(accountId)).willReturn(bal_List);
        List<Balance> foundBalances = uut.getBalanceByAccountId(accountId);
        assertEquals(bal_List.size(), foundBalances.size());
        assertThat(foundBalances.get(0)).isNotNull();//for when list has only 1 element
        assertThat(accountId).isNotEqualByComparingTo(foundBalances.get(0).getAccountId());
        verify(repo, times(1)).getAccountBalance(accountId);
    }

    @Test
    void getBalanceByAccountIdAndCurrencyShouldReturnBalance() {
        UUID accountId = UUID.fromString("eb01ed99-59fc-48f2-8bcc-5f245e3a17bd");
        given(repo.getAccountBalanceByIdAndCurrency(accountId, "EUR"))
                .willReturn(java.util.Optional.ofNullable(bal));
        Balance foundBalance = uut.getBalanceByAccountId(accountId, Currency.EUR);
        assertThat(foundBalance).isNotNull();
        assertThat(accountId).isNotEqualByComparingTo(foundBalance.getAccountId());
        verify(repo, times(1)).getAccountBalanceByIdAndCurrency(accountId, "EUR");
    }

    @Test
    void updateBalanceObjShouldReturnUpdatedBalanceObj() {
        Balance newBal = new Balance(balanceId, new BigDecimal("5.00"), Currency.EUR, bal.getAccountId());
        when(repo.getAccountBalanceByBalanceId(bal.getBalanceId())).thenReturn(java.util.Optional.of(newBal));
        given(repo.updateBalanceObj(any(Balance.class))).willReturn(anyInt());
        Balance updatedBalance = uut.updateBalanceObj(newBal);
        assertThat(updatedBalance).isNotNull();
        assertThat(balanceId).isEqualByComparingTo(updatedBalance.getBalanceId());
        assertThat(bal.getAmount()).isNotEqualByComparingTo(updatedBalance.getAmount());
        verify(repo, times(1)).getAccountBalanceByBalanceId(balanceId);
    }

    @Test
    void updateBalanceShouldReturnUpdatedBalanceObj() {
        Balance newBal = new Balance(balanceId, new BigDecimal("5.00"), Currency.EUR, bal.getAccountId());
        when(repo.getAccountBalanceByBalanceId(bal.getBalanceId())).thenReturn(java.util.Optional.of(newBal));
        given(repo.updateBalanceAmount(any(BigDecimal.class), any(Long.class))).willReturn(1);
        Balance updatedBalance = uut.updateBalance(newBal.getBalanceId(), newBal.getAmount());
        assertThat(updatedBalance).isNotNull();
        assertThat(balanceId).isEqualByComparingTo(updatedBalance.getBalanceId());
        assertThat(bal.getAmount()).isNotEqualByComparingTo(updatedBalance.getAmount());
        verify(repo, times(1)).getAccountBalanceByBalanceId(balanceId);
    }

    @Test
    void getBalanceByBalanceIdShouldReturnBalance() {
        given(repo.getAccountBalanceByBalanceId(balanceId)).willReturn(java.util.Optional.ofNullable(bal));
        Balance foundBalance = uut.getBalanceByBalanceId(balanceId);
        assertThat(foundBalance).isNotNull();
        assertThat(balanceId).isEqualByComparingTo(foundBalance.getBalanceId());
        verify(repo, times(1)).getAccountBalanceByBalanceId(balanceId);
    }

    @Test
    void updateBalanceByAccountIdShouldReturnUpdatedBalanceObj() {
        Balance newBal = new Balance(balanceId, new BigDecimal("5.00"), Currency.EUR, bal.getAccountId());
        when(repo.getAccountBalanceByIdAndCurrency(bal.getAccountId(), bal.getCurrency().name()))
                .thenReturn(java.util.Optional.of(newBal));
        given(repo.updateBalanceAmount(any(BigDecimal.class), any(Long.class))).willReturn(1);
        Balance updatedBalance = uut
                .updateBalanceByAccountId(newBal.getAccountId(), newBal.getCurrency(), newBal.getAmount());
        assertThat(updatedBalance).isNotNull();
        assertThat(balanceId).isEqualByComparingTo(updatedBalance.getBalanceId());
        assertThat(bal.getAmount()).isNotEqualByComparingTo(updatedBalance.getAmount());
        verify(repo, times(1))
                .getAccountBalanceByIdAndCurrency(newBal.getAccountId(), newBal.getCurrency().name());
    }
}
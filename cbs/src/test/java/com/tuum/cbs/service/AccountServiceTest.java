package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.AccountNotFoundException;
import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.AccountsRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@MybatisTest
class AccountServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @InjectMocks
    private AccountService uut;

    @Mock
    private AccountsRepository repo;

    @Mock
    private RabbitMQDESender rabbitMQDESender;

    @Mock
    private BalanceService balService;

    @Captor
    private ArgumentCaptor<Account> captor;

    private Account testAccount;
    private AccountDao testAccountDao;
    private List<Balance> bal_List;

    @BeforeEach
    public void setup(){
        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        currencies.add(currency1);
        currencies.add(currency2);

        final String customerId = String.valueOf(IdUtil.generateRandomId());
        UUID accountId = IdUtil.generateUUID();

        testAccountDao = AccountDao.builder()
                .customerId(customerId)
                .country("Estonia")
                .currencies(currencies)
                .build();

        bal_List = new ArrayList<>();
        for (Currency currency :
                currencies) {
            Long balanceId = IdUtil.generateRandomId();
            Balance bal = new Balance(balanceId, new BigDecimal("0.00"), currency, accountId);
            bal_List.add(bal);
        }

        testAccount = Account.builder().accountId(accountId)
                .country("Estonia").customerId(customerId).balanceList(bal_List)
                .build();
        repo = mock(AccountsRepository.class);
        uut = new AccountService(repo, balService, rabbitMQDESender);
    }

    @Test
    void checkContextStarts() {
        assertThat(uut).isNotNull();
    }

    @Test
    void saveShouldReturnNewAccountWithBalances() {
        given(repo.insertAccount(captor.capture())).willReturn(1);
        when(balService.createBalanceList(any(), any())).thenReturn(bal_List);
        Account savedAccount = uut.save(testAccountDao);
        System.out.println(bal_List);
        System.out.println(savedAccount.getBalanceList());
        assertEquals(bal_List.size(), savedAccount.getBalanceList().size());
        verify(repo, times(1)).insertAccount(captor.capture());
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount).hasSameClassAs(testAccount);
        assertThat(savedAccount.getBalanceList()).isNotNull();
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(savedAccount);
    }

    @Test
    void getAccountByIdShouldThrowAccountNotFoundException() {
        String accountId = "d1fed854-8e94-40f5-ac03-7c663f7b3a08";
        when(repo.getAccountById(UUID.fromString(accountId))).thenThrow(AccountNotFoundException.class);
        assertThatThrownBy(() -> uut.getByAccountId(accountId)).isInstanceOf(AccountNotFoundException.class);
        verify(repo, times(1)).getAccountById(UUID.fromString(accountId));
    }

    @Test
    void getAccountByIdShouldReturnAccount() {
        String accountId = String.valueOf(testAccount.getAccountId());
        when(balService.getBalanceByAccountId(UUID.fromString(accountId))).thenReturn(bal_List);
        when(repo.getAccountById(testAccount.getAccountId())).thenReturn(Optional.ofNullable(testAccount));
        Account foundAccount = uut.getByAccountId(accountId);
        verify(repo, times(1)).getAccountById(testAccount.getAccountId());
        assertThat(foundAccount).usingRecursiveComparison().isEqualTo(testAccount);
    }
}
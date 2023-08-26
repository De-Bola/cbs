package com.tuum.cbs.repositories;

import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@MybatisTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insertAccount() {
        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        currencies.add(currency1);
        currencies.add(currency2);

        String customerId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        customerId = customerId.substring(customerId.length() - 10);

        final UUID accountId = UUID.randomUUID();
        List<Balance> bal_List = new ArrayList<>();
        for (Currency currency :
                currencies) {
            String balanceId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
            balanceId = balanceId.substring(balanceId.length() - 10);
            Balance bal = new Balance(Long.valueOf(balanceId), new BigDecimal("0.00"), currency, accountId);
            bal_List.add(bal);
        }

        final Account testAccount = Account.builder().accountId(accountId)
                .country("Estonia").customerId(customerId).balanceList(bal_List)
                .build();

        when(repository.insertAccount(testAccount)).thenReturn(anyInt());

        int savedAccount = repository.insertAccount(testAccount);
        assertEquals(1, savedAccount);
    }

    @Test
    void getAccountById() {
    }

    @Test
    void getAccountBalance() {
    }

    @Test
    void insertBalance() {
    }

    @Test
    void insertBalances() {
    }
}
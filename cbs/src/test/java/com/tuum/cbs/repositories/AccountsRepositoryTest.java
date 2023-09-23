package com.tuum.cbs.repositories;

import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MybatisTest
@RunWith(SpringRunner.class)
@Sql({"/templates/accounts.sql", "/templates/balance.sql"})
class AccountsRepositoryTest {

    @Autowired
    private AccountsRepository repository;

    @Autowired
    SqlSession sqlSession;

    @Autowired
    DataSource dataSource;

    Connection connection;
    Statement statement;
    Account testAccount;

    @BeforeEach
    void setUp() throws SQLException {

        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        currencies.add(currency1);
        currencies.add(currency2);

        final String customerId = String.valueOf(IdUtil.generateRandomId());

        UUID accountId = IdUtil.generateUUID();
        final List<Balance> bal_List = new ArrayList<>();
        for (Currency currency :
                currencies) {
            Long balanceId = IdUtil.generateRandomId();
            Balance bal = new Balance(balanceId, new BigDecimal("0.00"), currency, accountId);
            bal_List.add(bal);
        }

        testAccount = Account.builder().accountId(accountId)
                .country("Estonia").customerId(customerId).balanceList(bal_List)
                .build();

        connection = sqlSession.getConnection();
        statement = connection.createStatement();
    }

    @Test
    void insertAccount() {
        int savedAccount = repository.insertAccount(testAccount);
        assertEquals(1, savedAccount);
    }

    @Test
    void getAccountById() {
        int savedAccount = repository.insertAccount(testAccount);
        assertEquals(1, savedAccount);
        Optional<Account> foundAccount = repository.getAccountById(testAccount.getAccountId());
        assertThat(foundAccount.isPresent()).isTrue();
        assertThat(foundAccount.get().getAccountId()).isEqualByComparingTo(testAccount.getAccountId());
    }

    @Test
    void getAccountBalance() {
        int savedBalances = repository.insertBalances(testAccount.getBalanceList());
        assertEquals(testAccount.getBalanceList().size(), savedBalances);
        List<Balance> foundAccountBalances = repository.getAccountBalance(testAccount.getAccountId());
        assertEquals(foundAccountBalances.size(), testAccount.getBalanceList().size());
        assertThat(foundAccountBalances.get(0).getAccountId()).isEqualByComparingTo(testAccount.getAccountId());
    }

    @Test
    void insertBalance() {
        int savedBalance = repository.insertBalance(testAccount.getBalanceList().get(0));
        assertEquals(1, savedBalance);
    }

    @Test
    void insertBalances() {
        int savedBalances = repository.insertBalances(testAccount.getBalanceList());
        assertEquals(testAccount.getBalanceList().size(), savedBalances);
    }
}
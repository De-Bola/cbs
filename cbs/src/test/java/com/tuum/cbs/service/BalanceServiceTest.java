package com.tuum.cbs.service;

import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.AccountRepository;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
    private AccountRepository repo;

//    @Autowired
//    DataSourceTransactionManager transactionManager;
//
//    @Autowired
//    SqlSession sqlSession;
//
//    @Autowired
//    DataSource dataSource;
//
//    //@Autowired
//    Connection connection;
//    Statement statement;
//
//    @Captor
//    private ArgumentCaptor<Balance> captor;

    private Balance bal;
    private List<Balance> bal_List;
    private String balanceId;



    @BeforeEach
    void setUp() throws SQLException {
        balanceId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        balanceId = balanceId.substring(balanceId.length() - 10);
        final UUID accountId = UUID.randomUUID();
        bal = new Balance(Long.valueOf(balanceId), new BigDecimal("0.00"), Currency.EUR, accountId);
        bal_List = new ArrayList<>();
        bal_List.add(bal);
        repo = mock(AccountRepository.class);
        uut = new BalanceService(repo);
//        connection = sqlSession.getConnection();
//        statement = connection.createStatement();
//        String stmt = "DROP TABLE IF EXISTS balances CASCADE;\n" +
//                "CREATE TABLE IF NOT EXISTS balances\n" +
//                "(\n" +
//                "    amount numeric(38,2),\n" +
//                "    balance_id bigint NOT NULL,\n" +
//                "    currency VARCHAR (255),\n" +
//                "    account_id uuid NOT NULL,\n" +
//                "    PRIMARY KEY (balance_id)\n" +
//                "    --CONSTRAINT FK_account_balances FOREIGN KEY (account_id) REFERENCES accounts (account_id)\n" +
//                ");";
//        statement.execute(stmt);
    }

//    @After
//    public void tearDown() throws SQLException {
//        statement.close();
//        connection.close();
//        sqlSession.close();
//    }

    @Test
    void getBalanceByAccountIdShouldReturnBalance() {
        UUID accountId = UUID.fromString("eb01ed99-59fc-48f2-8bcc-5f245e3a17bd");
        given(repo.getAccountBalance(accountId)).willReturn(bal_List);
        List<Balance> foundBalances = uut.getBalanceByAccountId(accountId);
        System.out.println("Service test: " + foundBalances);
        assertEquals(bal_List.size(), foundBalances.size());
        assertThat(foundBalances.get(0)).isNotNull();//for when list has only 1 element
        assertThat(accountId).isNotEqualByComparingTo(foundBalances.get(0).getAccountId());
        verify(repo, times(1)).getAccountBalance(accountId);
    }

    @Test
    void getBalanceByAccountIdAndCurrencyShouldReturnBalance() {
        UUID accountId = UUID.fromString("eb01ed99-59fc-48f2-8bcc-5f245e3a17bd");
        given(repo.getAccountBalanceByIdAndCurrency(accountId, "EUR")).willReturn(bal);
        Balance foundBalance = uut.getBalanceByAccountId(accountId, Currency.EUR);
        System.out.println("Service test: " + foundBalance);
        assertThat(foundBalance).isNotNull();
        assertThat(accountId).isNotEqualByComparingTo(foundBalance.getAccountId());
        verify(repo, times(1)).getAccountBalanceByIdAndCurrency(accountId, "EUR");
    }

    @Test
    void updateBalanceObjShouldReturnUpdatedBalanceObj() throws SQLException {
        Balance newBal = new Balance(Long.valueOf(balanceId), new BigDecimal("5.00"), Currency.EUR, bal.getAccountId());

        when(repo.getAccountBalanceByBalanceId(bal.getBalanceId())).thenReturn(newBal);
        given(repo.updateBalanceObj(any(Balance.class))).willReturn(anyInt());

        Balance updatedBalance = uut.updateBalanceObj(newBal);
        //sqlSession.update("com.tuum.cbs.repositories.AccountRepository.updateBalanceObj", bal);

        System.out.println("Service test: " + updatedBalance);

        assertThat(updatedBalance).isNotNull();
        assertThat(Long.valueOf(balanceId)).isEqualByComparingTo(updatedBalance.getBalanceId());
        assertThat(bal.getAmount()).isNotEqualByComparingTo(updatedBalance.getAmount());
        verify(repo, times(1)).getAccountBalanceByBalanceId(Long.valueOf(balanceId));
    }

    @Test
    void updateBalanceShouldReturnUpdatedBalanceObj() {
        Balance newBal = new Balance(Long.valueOf(balanceId), new BigDecimal("5.00"), Currency.EUR, bal.getAccountId());

        when(repo.getAccountBalanceByBalanceId(bal.getBalanceId())).thenReturn(newBal);
        given(repo.updateBalanceAmount(any(BigDecimal.class), any(Long.class))).willReturn(1);

        Balance updatedBalance = uut.updateBalance(newBal.getBalanceId(), newBal.getAmount());
        System.out.println("Service test: " + updatedBalance);
        assertThat(updatedBalance).isNotNull();
        assertThat(Long.valueOf(balanceId)).isEqualByComparingTo(updatedBalance.getBalanceId());
        assertThat(bal.getAmount()).isNotEqualByComparingTo(updatedBalance.getAmount());
        verify(repo, times(1)).getAccountBalanceByBalanceId(Long.valueOf(balanceId));
    }

    @Test
    void getBalanceByBalanceIdShouldReturnBalance() {
        given(repo.getAccountBalanceByBalanceId(Long.valueOf(balanceId))).willReturn(bal);
        Balance foundBalance = uut.getBalanceByBalanceId(Long.valueOf(balanceId));
        System.out.println("Service test: " + foundBalance);
        assertThat(foundBalance).isNotNull();
        assertThat(Long.valueOf(balanceId)).isEqualByComparingTo(foundBalance.getBalanceId());
        verify(repo, times(1)).getAccountBalanceByBalanceId(Long.valueOf(balanceId));
    }
}
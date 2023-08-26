package com.tuum.cbs.service;

import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService uut;

    @Mock
    private AccountRepository repo;

    private Account testAccount;
    private AccountDao testAccountDao;

    @BeforeEach
    public void setup(){
        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        currencies.add(currency1);
        currencies.add(currency2);

        String customerId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        customerId = customerId.substring(customerId.length() - 10);

        testAccountDao = AccountDao.builder()
                .customerId(customerId)
                .country("Estonia")
                .currencies(currencies)
                .build();

        final UUID accountId = UUID.randomUUID();
        List<Balance> bal_List = new ArrayList<>();
        for (Currency currency :
                currencies) {
            String balanceId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
            balanceId = balanceId.substring(balanceId.length() - 10);
            Balance bal = new Balance(Long.valueOf(balanceId), new BigDecimal("0.00"), currency, accountId);
            bal_List.add(bal);
        }

        testAccount = Account.builder().accountId(accountId)
                .country("Estonia").customerId(customerId).balanceList(bal_List)
                .build();
    }

    @Test
    void checkContextStarts() {
        assertThat(uut).isNotNull();
    }

    @Test
    void saveShouldReturnNewAccount() {
        //final AccountService spy = spy(uut);
        doReturn(any()).when(uut).save(testAccountDao);

        Account savedAccount = uut.save(testAccountDao);
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount).hasSameClassAs(testAccount);
    }

//    @Test
//    void saveShouldThrowExceptionWhenAccountIdIsMissing() {
//        testAccount.setAccountId(null);
//        assertThatThrownBy(() -> {
//            uut.save(testAccount);
//        }).isInstanceOf(AccountAlreadyException.class);
//    }
}
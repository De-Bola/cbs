package com.tuum.cbs.service;

import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.when;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {
//        AccountService.class
//})
@SpringJUnitConfig(classes = {AccountService.class})
class AccountServiceTest {

    @MockBean
    AccountService uut;

    @Test
    void checkContextStarts() {
        assertThat(uut).isNotNull();
    }

    @Test
    void saveShouldReturnNewAccount() {
        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        currencies.add(currency1);
        currencies.add(currency2);

        String customerId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        customerId = customerId.substring(customerId.length() - 10);

        final AccountDao testAccountDao = AccountDao.builder()
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

        final Account testAccount = Account.builder().accountId(accountId)
                .country("Estonia").customerId(customerId).balanceList(bal_List)
                .build();
        when(uut.save(any(AccountDao.class))).thenReturn(testAccount);

        final Account savedAccount = uut.save(testAccountDao);
        assertThat(savedAccount).hasSameClassAs(testAccount);
        assertThat(savedAccount).usingRecursiveComparison().isEqualTo(testAccount);
    }

//    @Test
//    void saveShouldThrowExceptionWhenUserAlreadyExists() {
//        final AccountDao testAccount = AccountDao.builder().build();
//        assertThatThrownBy(() -> {
//            uut.save(testAccount);
//        }).isInstanceOf(AccountAlreadyException.class);
//    }
}
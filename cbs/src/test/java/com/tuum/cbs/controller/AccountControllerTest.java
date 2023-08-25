package com.tuum.cbs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<AccountDao> captor;

    @Test
    void checkContextStarts() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void createAccountShouldReturnAccountDetails() throws Exception {

        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        currencies.add(currency1);
        currencies.add(currency2);
//        final Balance balance1 = Balance.builder()
//                .balanceId(UUID.randomUUID())
//                .amount(new BigDecimal("50.0"))
//                .currency(Currency.EUR).build();
//        final Balance balance2 = Balance.builder()
//                .balanceId(UUID.randomUUID())
//                .amount(new BigDecimal("25.0"))
//                .currency(Currency.SEK).build();
//        balList.add(balance1);
//        balList.add(balance2);
//        final ArgumentCaptor<Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);
//        String randomVal = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
//        randomVal= randomVal.substring(randomVal.length() - 10);
//        final Long accountId = Long.valueOf(randomVal);
//        //accountId = accountId.substring(accountId.length() - 10);

        String customerId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        customerId = customerId.substring(customerId.length() - 10);

        final AccountDao testAccount = AccountDao.builder()
                .customerId(customerId)
                .country("Estonia")
                .currencies(currencies)
                .build();
        final String jsonBody = objectMapper.writeValueAsString(testAccount);

        when(accountService.save(any(AccountDao.class))).thenAnswer(invocation -> invocation.getArgument(0, AccountDao.class));

        mockMvc.perform(
                post("/api/accounts/account-open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isCreated());
                //.andExpect(any(SuccessResponse.class));

        verify(accountService, times(1)).save(captor.capture());

        final Object capturedValue = captor.getValue();

        assertThat(capturedValue).usingRecursiveComparison().ignoringFields("accountId").isEqualTo(testAccount);
        System.out.println(capturedValue);
        System.out.println(testAccount);
    }
}
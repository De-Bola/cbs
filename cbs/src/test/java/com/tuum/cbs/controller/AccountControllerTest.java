package com.tuum.cbs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Captor
    private ArgumentCaptor<AccountDao> captor;

    @Test
    void checkContextStarts() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("Verifies that AccountDao is sent to Service layer")
    void createAccountShouldReturnAccountDao() throws Exception {

        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        currencies.add(currency1);
        currencies.add(currency2);

        String customerId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        customerId = customerId.substring(customerId.length() - 10);

        final AccountDao testAccount = AccountDao.builder()
                .customerId(customerId)
                .country("Estonia")
                .currencies(currencies)
                .build();
        final String jsonBody = objectMapper.writeValueAsString(testAccount);

        mockMvc.perform(
                post("/api/accounts/account-open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isCreated());


        verify(accountService, times(1)).save(captor.capture());

        final Object capturedValue = captor.getValue();

        assertThat(capturedValue).usingRecursiveComparison().ignoringFields("accountId").isEqualTo(testAccount);
        System.out.println(capturedValue);
        System.out.println(testAccount);
    }

    @Test
    @DisplayName("Verifies that AccountDetails is returned from Service layer")
    void createAccountShouldReturnAccountDetails() throws Exception {
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
        final String jsonBody = objectMapper.writeValueAsString(testAccountDao);

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
        when(accountService.save(captor.capture())).thenReturn(testAccount);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult mvcResult = mockMvc.perform(
                post("/api/accounts/account-open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isCreated()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accountId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.balanceList").isArray()).andReturn();

        verify(accountService, times(1)).save(any(AccountDao.class));

        System.out.println(mvcResult.getResponse());
    }

    @Test
    @DisplayName("Verifies that error response is given when something goes wrong")
    void createAccountShouldReturnErrorResponse() throws Exception {

        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        currencies.add(currency1);
        currencies.add(currency2);

        String customerId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        customerId = customerId.substring(customerId.length() - 10);

        final AccountDao testAccountDao = AccountDao.builder()
                //.customerId(customerId)
                .country("Estonia")
                .currencies(currencies)
                .build();
        final String jsonBody = objectMapper.writeValueAsString(testAccountDao);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(
                        post("/api/accounts/account-open")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                ).andExpect(status().isBadRequest()).andDo(print())
                .andExpect(jsonPath("$.message").exists())
                .andReturn();

        verify(accountService, times(0)).save(any(AccountDao.class));
    }
}
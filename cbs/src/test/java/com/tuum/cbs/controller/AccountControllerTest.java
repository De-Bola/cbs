package com.tuum.cbs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.CbsRepository;
import com.tuum.cbs.service.AccountService;
import com.tuum.cbs.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private BalanceService balanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Captor
    private ArgumentCaptor<AccountDao> captor;

    private AccountDao testAccountDao;
    private Account testAccount;

    @Test
    void checkContextStarts() {
        assertThat(mockMvc).isNotNull();
    }

    @BeforeEach
    void setUp(){

        final UUID accountId = UUID.randomUUID();
        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;
        String balanceId1 = String.format("%010d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        balanceId1 = balanceId1.substring(balanceId1.length() - 10);
        String customerId = String.format("%010d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        customerId = customerId.substring(customerId.length() - 10);
        Balance bal1 = new Balance(Long.valueOf(balanceId1), new BigDecimal("0.00"), Currency.EUR, accountId);
        List<Balance> bal_List = new ArrayList<>();

        bal_List.add(bal1);
        currencies.add(currency1);
        currencies.add(currency2);
        testAccountDao = AccountDao.builder()
                .customerId(customerId)
                .country("Estonia")
                .currencies(currencies)
                .build();
        testAccount = Account.builder().accountId(accountId)
                .country("Estonia").customerId(customerId).balanceList(bal_List)
                .build();

        for (Currency currency : currencies) {
            String balanceId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
            balanceId = balanceId.substring(balanceId.length() - 10);
            Balance bal = new Balance(Long.valueOf(balanceId), new BigDecimal("0.00"), currency, accountId);
            bal_List.add(bal);
        }

    }

    @Test
    @DisplayName("Verifies that AccountDao is sent to Service layer")
    void createAccountShouldReturnAccountDao() throws Exception {
        final String jsonBody = objectMapper.writeValueAsString(testAccountDao);

        mockMvc.perform(
                post("/api/accounts/account-open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isCreated());

        verify(accountService, times(1)).save(captor.capture());

        final Object capturedValue = captor.getValue();

        assertThat(capturedValue).usingRecursiveComparison().ignoringFields("accountId").isEqualTo(testAccountDao);
        System.out.println(capturedValue);
        System.out.println(testAccount);
    }

    @Test
    @DisplayName("Verifies that AccountDetails is returned from Service layer")
    void createAccountShouldReturnAccountDetails() throws Exception {
        final String jsonBody = objectMapper.writeValueAsString(testAccountDao);

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

        testAccountDao.setCustomerId(null);
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
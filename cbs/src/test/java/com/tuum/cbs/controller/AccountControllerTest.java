package com.tuum.cbs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.service.AccountService;
import com.tuum.cbs.service.RabbitMQFOSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
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

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private RabbitMQFOSender rabbitMQFOSender;

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

        UUID accountId = IdUtil.generateUUID();
        final List<Currency> currencies = new ArrayList<>();
        final Currency currency1 = Currency.EUR;
        final Currency currency2 = Currency.SEK;

        final Long balanceId1 = IdUtil.generateRandomId();
        final String customerId = String.valueOf(IdUtil.generateRandomId());

        Balance bal1 = new Balance(balanceId1, new BigDecimal("0.00"), Currency.EUR, accountId);
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
            Long balanceId = IdUtil.generateRandomId();
            Balance bal = new Balance(balanceId, new BigDecimal("0.00"), currency, accountId);
            bal_List.add(bal);
        }

    }

    @Test
    @DisplayName("Verifies that AccountDao is sent to Service layer")
    void createAccountShouldReturn201() throws Exception {
        final String jsonBody = objectMapper.writeValueAsString(testAccountDao);
        when(accountService.save(captor.capture())).thenReturn(testAccount);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(
                post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isCreated()).andReturn();
        verify(accountService, times(1)).save(any(AccountDao.class));
        final Object capturedValue = captor.getValue();
        assertThat(capturedValue).usingRecursiveComparison()
                .ignoringFields("accountId")
                .isEqualTo(testAccountDao);
    }

    @Test
    @DisplayName("Verifies that AccountDetails is returned from Service layer")
    void createAccountShouldReturnAccountDetails() throws Exception {
        final String jsonBody = objectMapper.writeValueAsString(testAccountDao);
        when(accountService.save(captor.capture())).thenReturn(testAccount);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(
                post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isCreated()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accountId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.balanceList").isArray()).andReturn();
        verify(accountService, times(1)).save(any(AccountDao.class));
    }

    @Test
    @DisplayName("Verifies that error response is given when something goes wrong")
    void createAccountShouldReturnErrorResponse() throws Exception {
        testAccountDao.setCustomerId(null);
        final String jsonBody = objectMapper.writeValueAsString(testAccountDao);
        when(accountService.save(captor.capture()))
                .thenThrow(new BadRequestException("Invalid entry: customer ID cannot be blank"));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                ).andExpect(status().isBadRequest()).andDo(print())
                .andExpect(jsonPath("$.message").exists())
                .andReturn();
        verify(accountService, times(1)).save(any(AccountDao.class));
    }
}
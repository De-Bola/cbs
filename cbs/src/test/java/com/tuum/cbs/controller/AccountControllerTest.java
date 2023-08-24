package com.tuum.cbs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Test
    void checkContextStarts() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void createAccountShouldReturnAccountDetails() throws Exception {

        final List<Balance> balList = new ArrayList<>();
        final Balance balance1 = Balance.builder()
                .amount(new BigDecimal("50.0"))
                .currency(Currency.EUR).build();
        final Balance balance2 = Balance.builder()
                .amount(new BigDecimal("25.0"))
                .currency(Currency.SEK).build();
        balList.add(balance1);
        balList.add(balance2);
        String accountId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        accountId = accountId.substring(accountId.length() - 10);

        final String jsonBody = objectMapper.writeValueAsString(
                Account.builder().accountId(Long.valueOf(accountId)).balanceList(balList).build()
        );

        mockMvc.perform(
                post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isCreated());

        verify(accountService, times(1)).save(any(Account.class));
    }
}
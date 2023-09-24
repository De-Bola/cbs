package com.tuum.cbs.controller.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tuum.cbs.models.Currency.EUR;
import static com.tuum.cbs.models.Currency.SEK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BalanceControllerIntegrationTest {
    // this is balance controller integration test with no mocks
    @Test
    void contextLoads() {
    }

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static TestRestTemplate restTemplate;
    private UUID accountId;

    private final List<Balance> nestedBalDao = new ArrayList<>();
    private final List<Currency> currencies = new ArrayList<>();
    private URI uri;

    @BeforeAll
    static void init(){
        restTemplate = new TestRestTemplate();
    }

    @BeforeEach // goes to inner test as well
    public void setup() throws URISyntaxException, JsonProcessingException {
        baseUrl = baseUrl + ":" + port + "/api";
        String customerId = String.valueOf(IdUtil.generateRandomId());
        String country = "Sweden";
        currencies.add(EUR);

        AccountDao accountDao = new AccountDao(customerId, country, currencies);
        uri = new URI(baseUrl + "/accounts");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate
                .postForEntity(uri, accountDao, SuccessResponse.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getBody()).isNotNull();
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(201));

        Object createdAccount = responseEntity.getBody().getData();
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(createdAccount); // write first
        Account account = mapper.readValue(jsonStr, Account.class); // then read
        accountId = account.getAccountId();
    }

    @Nested
    class TestsThatRequireCreatedBalances{
        @Test
        void contextLoads() {
        }

        @BeforeEach
        void setupNestedTests() throws URISyntaxException {
            // make post request first
            Balance balance = new Balance(IdUtil.generateRandomId(), new BigDecimal("76.50"), SEK, accountId);
            nestedBalDao.add(balance);

            uri = new URI(baseUrl + "/balances");
            ResponseEntity<SuccessResponse> responseEntity = restTemplate
                    .postForEntity(uri, nestedBalDao, SuccessResponse.class);
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getBody()).isNotNull();
            assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(201));
        }
    }


}

package com.tuum.cbs.controller.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.controller.response.ErrorResponse;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.tuum.cbs.models.Currency.EUR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerIntegrationTest {
    // this is transaction controller integration test with no mocks
    @Test
    void contextLoads() {
    }

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";
    private static RestTemplate restTemplate;
    private UUID accountId;
    private final List<Currency> currencies = new ArrayList<>();
    private URI uri;

    @BeforeAll
    static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach // goes to inner test
    public void setup() throws URISyntaxException, JsonProcessingException {
        baseUrl = baseUrl + ":" + port + "/api";
        String customerId = String.valueOf(IdUtil.generateRandomId());
        String country = "Sweden";
        currencies.add(EUR);

        AccountDao accountDao = new AccountDao(customerId, country, currencies);
        uri = new URI(baseUrl + "/accounts/account-open");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.postForEntity(uri, accountDao, SuccessResponse.class);
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
    class TestGetRequest{
        @Test
        void contextLoads() {
        }

        @BeforeEach
        void setupNestedTests() throws URISyntaxException {
            // make post request first
            String description = "This is a test!";
            TransactionDao trxDao = new TransactionDao(accountId, new BigDecimal("76.50"),
                    currencies.get(0), TransactionType.IN, description);

            uri = new URI(baseUrl + "/transactions/transaction-create");
            ResponseEntity<SuccessResponse> responseEntity = restTemplate.postForEntity(uri, trxDao, SuccessResponse.class);
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getBody()).isNotNull();
            assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(201));
        }

        @Test
        public void getTransactionITest() {
            Map<String, UUID> params = Collections.singletonMap("id", accountId);
            // change the uri to url
            String url = baseUrl + "/transactions/get?id={id}";
            ResponseEntity<SuccessResponse> getResponseEntity = restTemplate.getForEntity(url, SuccessResponse.class, params);
            // final assertions
            assertThat(getResponseEntity).isNotNull();
            assertThat(getResponseEntity.getBody()).isNotNull();
            assertEquals(getResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        }

        @Test()
        public void getTransactionITestShouldReturnNotFound() {
            Map<String, UUID> params = Collections.singletonMap("id", IdUtil.generateUUID());
            // change the uri to url
            String url = baseUrl + "/transactions/get?id={id}";

            Exception exception = assertThrows(HttpClientErrorException.NotFound.class, ()->{
                restTemplate.getForEntity(url, ErrorResponse.class, params);
            });
        }

    }



    @Test
    public void createTransactionITest() throws URISyntaxException {
        String description = "This is a test!";
        TransactionDao trxDao = new TransactionDao(accountId, new BigDecimal("65.85"),
                currencies.get(0), TransactionType.IN, description);

        uri = new URI(baseUrl + "/transactions/transaction-create");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate.postForEntity(uri, trxDao, SuccessResponse.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getBody()).isNotNull();
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(201));
    }

}

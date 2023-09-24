package com.tuum.cbs.controller.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.controller.response.ErrorResponse;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerIntegrationTest {
    // this is transaction controller integration test with no mocks
    @Test
    void contextLoads() {
    }

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";
    private static TestRestTemplate restTemplate;
    private UUID accountId;

    private TransactionDao nestedTrxDao;
    private final List<Currency> currencies = new ArrayList<>();
    private URI uri;

    @BeforeAll
    static void init(){
        restTemplate = new TestRestTemplate();
    }

    @BeforeEach // goes to inner test
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
    class TestGetRequest{
        @Test
        void contextLoads() {
        }

        @BeforeEach
        void setupNestedTests() throws URISyntaxException {
            // make post request first
            String description = "This is a test!";
            nestedTrxDao = new TransactionDao(accountId, new BigDecimal("76.50"),
                    currencies.get(0), TransactionType.IN, description);

            uri = new URI(baseUrl + "/transactions");
            ResponseEntity<SuccessResponse> responseEntity = restTemplate
                    .postForEntity(uri, nestedTrxDao, SuccessResponse.class);
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getBody()).isNotNull();
            assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(201));
        }

        @Test
        public void getTransactionITest() {
            // change the uri to url
            String url = baseUrl + "/accounts/{accountId}/transactions";
            ResponseEntity<SuccessResponse> getResponseEntity = restTemplate
                    .getForEntity(url, SuccessResponse.class, accountId);
            // final assertions
            assertThat(getResponseEntity).isNotNull();
            assertThat(getResponseEntity.getBody()).isNotNull();
            assertEquals(getResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        }

        @Test()
        public void getTransactionITestShouldReturnNotFound() {
            // change the uri to url
            String url = baseUrl + "/accounts/{accountId}/transactions";
            String message = "No Transactions found!";

            ResponseEntity<ErrorResponse> getResponseEntity = restTemplate
                    .getForEntity(url, ErrorResponse.class, IdUtil.generateUUID());
            assertEquals(HttpStatusCode.valueOf(404), getResponseEntity.getStatusCode());
            assertThat(getResponseEntity.getBody()).isNotNull();
            assertThat(getResponseEntity.getBody()).isInstanceOf(ErrorResponse.class);
            assertEquals("404", getResponseEntity.getBody().getCode());
            assertEquals(message, getResponseEntity.getBody().getMessage());
        }

        @Test
        public void createDebitTransactionITestShouldReturnCreated() throws URISyntaxException, JsonProcessingException {
            String description = "This is a test!";
            String message = "Transaction created!";

            TransactionDao trxDao = new TransactionDao(accountId, new BigDecimal("5.85"),
                    currencies.get(0), TransactionType.OUT, description);

            BigDecimal balAfterTrx = nestedTrxDao.getAmount().subtract(trxDao.getAmount());

            uri = new URI(baseUrl + "/transactions");

            ResponseEntity<SuccessResponse> responseEntity = restTemplate
                    .postForEntity(uri, trxDao, SuccessResponse.class);

            assertEquals(HttpStatusCode.valueOf(201), responseEntity.getStatusCode());
            assertThat(responseEntity.getBody()).isNotNull();
            assertThat(responseEntity.getBody()).isInstanceOf(SuccessResponse.class);
            assertThat(responseEntity.getBody().getData()).isNotNull();
            assertEquals(message, responseEntity.getBody().getMessage());

            Object jsonObj = responseEntity.getBody().getData();
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(jsonObj); // write first
            Transaction createdTrx = mapper.readValue(jsonStr, Transaction.class); // then read

            assertEquals(trxDao.getTrxType(), createdTrx.getTrxType());
            assertEquals(trxDao.getAccountId(), createdTrx.getAccountId());
            assertEquals(trxDao.getAmount(), createdTrx.getAmount());
            assertEquals(trxDao.getDescription(), createdTrx.getDescription());
            assertEquals(trxDao.getCurrency(), createdTrx.getCurrency());
            assertEquals(balAfterTrx, createdTrx.getBalanceAfterTrx());
        }

    }



    @Test
    public void createCreditTransactionITest() throws URISyntaxException {
        String description = "This is a test!";
        TransactionDao trxDao = new TransactionDao(accountId, new BigDecimal("65.85"),
                currencies.get(0), TransactionType.IN, description);

        uri = new URI(baseUrl + "/transactions");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate
                .postForEntity(uri, trxDao, SuccessResponse.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getBody()).isNotNull();
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(201));
    }

    @Test
    public void createDebitTransactionITestShouldReturnBadRequest() throws URISyntaxException {
        String description = "This is a test!";
        String message = "Insufficient funds: account balance shouldn't goto negative";

        TransactionDao trxDao = new TransactionDao(accountId, new BigDecimal("5.85"),
                currencies.get(0), TransactionType.OUT, description);

        uri = new URI(baseUrl + "/transactions");

        ResponseEntity<ErrorResponse> responseEntity = restTemplate
                .postForEntity(uri, trxDao, ErrorResponse.class);

        assertEquals(HttpStatusCode.valueOf(400), responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isInstanceOf(ErrorResponse.class);
        assertEquals("400", responseEntity.getBody().getCode());
        assertEquals(message, responseEntity.getBody().getMessage());
    }

}

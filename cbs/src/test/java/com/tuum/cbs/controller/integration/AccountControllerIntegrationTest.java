package com.tuum.cbs.controller.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.controller.response.ErrorResponse;
import com.tuum.cbs.controller.response.SuccessResponse;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Currency;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.tuum.cbs.models.Currency.EUR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerIntegrationTest {
    // this is account controller integration test with no mocks
    @Test
    void contextLoads() {
    }

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";
    private static RestTemplate restTemplate;
    private String customerId;
    private String country;
    private final List<Currency> currencies = new ArrayList<>();
    private URI uri;

    @BeforeAll
    static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setup() {
        baseUrl = baseUrl + ":" + port + "/api";
        customerId = String.valueOf(IdUtil.generateRandomId());
        country = "Sweden";
        currencies.add(EUR);
    }

    @Test
    public void createAccountITest() throws URISyntaxException {
        AccountDao accountDao = new AccountDao(customerId, country, currencies);
        uri = new URI(baseUrl + "/accounts/account-open");
        ResponseEntity<SuccessResponse> responseEntity = restTemplate
                .postForEntity(uri, accountDao, SuccessResponse.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getBody()).isNotNull();
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(201));
    }

    @Test
    public void getAccountITestShouldReturnSuccess() throws URISyntaxException, JsonProcessingException {
        // make post request first
        AccountDao accountDao = new AccountDao(customerId, country, currencies);
        uri = new URI(baseUrl + "/accounts/account-open");
        ResponseEntity<SuccessResponse> postResponseEntity = restTemplate
                .postForEntity(uri, accountDao, SuccessResponse.class);
        // make sure post request was successful
        assertThat(postResponseEntity).isNotNull();
        assertThat(postResponseEntity.getBody()).isNotNull();
        assertEquals(postResponseEntity.getStatusCode(), HttpStatusCode.valueOf(201));
        // prep for get request by retrieving account id from post request
        Object createdAccount = postResponseEntity.getBody().getData();
        // use objectMapper to deserialize Object data
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(createdAccount); // write first
        Account account = mapper.readValue(jsonStr, Account.class); // then read
        // set @RequestParams for get request
        UUID accountId = account.getAccountId();
        Map<String, UUID> params = Collections.singletonMap("id", accountId);
        // change the uri to url
        String url = baseUrl + "/accounts/account?id={id}";
        ResponseEntity<Account> getResponseEntity = restTemplate.getForEntity(url, Account.class, params);
        // final assertions
        assertThat(getResponseEntity).isNotNull();
        assertThat(getResponseEntity.getBody()).isNotNull();
        assertEquals(getResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test()
    public void getAccountITestShouldReturnNotFound() throws URISyntaxException {
        // make post request first
        AccountDao accountDao = new AccountDao(customerId, country, currencies);
        uri = new URI(baseUrl + "/accounts/account-open");
        ResponseEntity<SuccessResponse> postResponseEntity = restTemplate
                .postForEntity(uri, accountDao, SuccessResponse.class);
        // make sure post request was successful
        assertThat(postResponseEntity).isNotNull();
        assertThat(postResponseEntity.getBody()).isNotNull();
        assertEquals(postResponseEntity.getStatusCode(), HttpStatusCode.valueOf(201));

        Map<String, UUID> params = Collections.singletonMap("id", UUID.randomUUID());
        // change the uri to url
        String url = baseUrl + "/accounts/account?id={id}";

        Exception exception = assertThrows(HttpClientErrorException.NotFound.class,
                ()-> restTemplate.getForEntity(url, ErrorResponse.class, params));
        System.out.println(exception.getMessage());
    }

}

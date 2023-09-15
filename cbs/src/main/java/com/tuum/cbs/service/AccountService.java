package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.AccountNotFoundException;
import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class AccountService {

    private final AccountsRepository repo;
    private final BalanceService balService;
    private final RabbitMQDESender mqDeSender;
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "AccountService";

    /**
     * for creating a new account
     * */
    public Account save(AccountDao accountDao) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " create account input: " + accountDao);

        int currencyListSize = accountDao.getCurrencies().size();
        if (currencyListSize > 4 ){
            throw new BadRequestException("Invalid currency: currency list larger than allowed values");
        }

        if (accountDao.getCurrencies().isEmpty()){
            throw new BadRequestException("Invalid currency: currency list is empty");
        }

        if (accountDao.getCountry() == null || accountDao.getCountry().isEmpty()){
            throw new BadRequestException("Invalid entry: country cannot be blank");
        }

        if (accountDao.getCustomerId() == null || accountDao.getCustomerId().isEmpty()){
            throw new BadRequestException("Invalid entry: customer ID cannot be blank");
        }

        Account account;
        final UUID accountId = UUID.randomUUID();
        account = Account.builder()
                        .accountId(accountId)
                        .country(accountDao.getCountry())
                        .customerId(accountDao.getCustomerId())
                        .build();
        List<Balance> balList = new ArrayList<>();
        for (Currency currency : accountDao.getCurrencies()) {
            Balance bal = new Balance(generateRandomId(), new BigDecimal("0.00"), currency, accountId);
            balList.add(bal);
        }
        account.setBalanceList(new ArrayList<Balance>(balList));
        repo.insertAccount(account);
        createBalance(balList);
        // notify consumers
        mqDeSender.publishToCreateBalanceQueue(balList.toString());

        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " created account with id: " + accountId);
        return account;
    }

    /**
     * for generating random ids of type Long
     * */
    public Long generateRandomId() {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " generate a random id");
        String format = String.format("%010d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        format = format.substring(format.length() - 10);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " random id generated: " + format);
        return Long.valueOf(format);
    }

    /**
     * for creating new list balances
     * */
    public void createBalance(List<Balance> balances){
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " create balance records with balance list");
        repo.insertBalances(balances);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " created balance records!");
    }

    /**
     * get account by accountId
     * */
    public Account getByAccountId(String accountId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get account input: " + accountId);
        Account foundAccount;
        UUID accId = UUID.fromString(accountId);
        Optional<Account> optionalAccount = repo.getAccountById(accId);
         if (optionalAccount.isEmpty()) {
             throw new AccountNotFoundException("Account with id - " + accId + " not found!");
         }
         else {
             List<Balance> balances = balService.getBalanceByAccountId(accId);
             foundAccount = Account.builder().accountId(accId)
                .customerId(optionalAccount.get().getCustomerId())
                .country(optionalAccount.get().getCountry())
                .balanceList(balances)
                .build();
        }
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got account with id: " + accountId);
        return foundAccount;
    }

}

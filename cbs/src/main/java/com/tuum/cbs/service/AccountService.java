package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.AccountNotFoundException;
import com.tuum.cbs.common.exceptions.BadRequestException;
import com.tuum.cbs.common.util.IdUtil;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.repositories.AccountsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
@Slf4j
public class AccountService {

    private final AccountsRepository repo;
    private final BalanceService balService;
    private final RabbitMQDESender mqDeSender;
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "AccountService";

    public AccountService(AccountsRepository repo, BalanceService balService, RabbitMQDESender mqDeSender) {
        this.repo = repo;
        this.balService = balService;
        this.mqDeSender = mqDeSender;
    }

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
        final UUID accountId = IdUtil.generateUUID();
        account = Account.builder()
                        .accountId(accountId)
                        .country(accountDao.getCountry())
                        .customerId(accountDao.getCustomerId())
                        .build();
        final List<Balance> balList = balService.createBalanceList(accountDao.getCurrencies(), accountId);
        account.setBalanceList(new ArrayList<>(balList));
        repo.insertAccount(account);
        balService.createBalance(balList);
        // notify consumers
        mqDeSender.publishToCreateBalanceQueue(balList.toString());

        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " created account with id: " + accountId);
        return account;
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

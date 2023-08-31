package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.AccountNotFoundException;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.CbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class AccountService {

    private final CbsRepository repo;
    private final BalanceService balService;
    private final RabbitMQDESender mqDeSender;


    /**
     * for creating a new account
     * */
    public Account save(AccountDao accountDao) {

        final UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
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

        System.out.println("Service: " + account);
        return account;
    }

    /**
     * for generating random ids of type Long
     * */
    public Long generateRandomId() {
        // made an active decision to be generating uuid for account_id and
        // balance_id generation will be here
        // not for customer_id since its provided
        // jury is still out on whether to do this at db level or here
        // for now I prefer here
        String format = String.format("%010d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        format = format.substring(format.length() - 10);
        return Long.valueOf(format);
    }

    /**
     * for creating new list balances
     * */
    public void createBalance(List<Balance> balances){
        repo.insertBalances(balances);
    }

    /**
     * get account by accountId
     * */
    public Account getByAccountId(String accountId) {
        Account foundAccount;
        UUID accId = UUID.fromString(accountId);
        Optional<Account> optionalAccount = repo.getAccountById(accId);
         if (optionalAccount.isEmpty()) {
            // return null;
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
        return foundAccount;
    }

}

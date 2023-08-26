package com.tuum.cbs.service;

import com.tuum.cbs.models.*;
import com.tuum.cbs.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class AccountService {

    private final AccountRepository repo;
    private final BalanceService balService;

    /**
     * for creating a new account
     * */
    public Account save(AccountDao accountDao) {
        // todo : validate currency
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
        //repo.insertBalances(balList);
        createBalance(balList);

        System.out.println("Service: " + account);
        return account;
    }

    /**
     * for generating random ids of type Long
     * */
    private Long generateRandomId() {
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
        UUID accId = UUID.fromString(accountId);
        Account account = repo.getAccountById(accId);
        if (account != null) {
            List<Balance> foundBalances = balService.getBalanceByAccountId(accId);
            account.setBalanceList(new ArrayList<Balance>(foundBalances));
        }
        return account;
    }

    //todo : on Sunday, rabbitmq and docker stuff
}

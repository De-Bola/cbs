package com.tuum.cbs.service;

import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
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
        repo.insertBalances(balList);

        System.out.println("Service: " + account);
        return account;
    }

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

    // todo: get account api

}

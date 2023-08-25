package com.tuum.cbs.service;

import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository repo;

    public Account save(AccountDao accountDao) {
        final Long accountId = generateRandomId();
        Account account = Account.builder()
                        .accountId(accountId)
                        .customerId(accountDao.getCustomerId())
                        .build();

        repo.insert(account);

        List<Balance> balList = new ArrayList<>();
        for (Currency currency :
                accountDao.getCurrencies()) {
            Balance bal = new Balance(UUID.randomUUID(), new BigDecimal(0.00), currency, accountId);
            balList.add(bal);
        }

        repo.insertBalances(balList, accountId);
        return account;
    }

    private Long generateRandomId() {
        String format = String.format("%010d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        format = format.substring(format.length() - 10);
        return Long.valueOf(format);
    }
}

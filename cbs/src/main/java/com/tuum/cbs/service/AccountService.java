package com.tuum.cbs.service;

import com.tuum.cbs.models.Account;
import com.tuum.cbs.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository repo;

    public Account save(Account account) {

        return account;
    }
}

package com.tuum.cbs.service;

import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.CbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BalanceService {

    private final CbsRepository repo;

    // business logic for balances
    public List<Balance> getBalanceByAccountId(UUID accId) {
        return repo.getAccountBalance(accId);
    }

    public Balance getBalanceByAccountId(UUID accountId, Currency currency) {
        return repo.getAccountBalanceByIdAndCurrency(accountId, currency.name());
    }

    public Balance updateBalanceObj(Balance balance){
        repo.updateBalanceObj(balance);
        Balance updatedBalance = getBalanceByBalanceId(balance.getBalanceId());
        return updatedBalance;
    }

    public Balance updateBalance(Long balanceId, BigDecimal amount){
        repo.updateBalanceAmount(amount, balanceId);
        return getBalanceByBalanceId(balanceId);
    }

    public Balance getBalanceByBalanceId(Long balanceId) {
        return repo.getAccountBalanceByBalanceId(balanceId);
    }
}

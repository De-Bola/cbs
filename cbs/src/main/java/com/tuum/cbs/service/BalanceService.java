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
        Balance initBalance = getBalanceByBalanceId(balance.getBalanceId());
        BigDecimal updatedAmount = initBalance.getAmount().add(balance.getAmount());

        if (updatedAmount.compareTo(initBalance.getAmount()) == 0) {
            // do nothing
            return initBalance;
        } else {
            balance.setAmount(updatedAmount);
            repo.updateBalanceObj(balance);
        }
        // not sure the difference between putting return outside if/else statement
        // and putting it inside it; for now I'll leave it outside
        return balance;
    }

    public Balance updateBalance(Long balanceId, BigDecimal amount){
        // get init balance
        Balance balance = getBalanceByBalanceId(balanceId);
        // do addition {always because sign changes in trx service already}
        BigDecimal updatedAmount = balance.getAmount().add(amount);

        if (updatedAmount.compareTo(balance.getAmount()) == 0) {
            // do nothing, maybe early return is better here
            return balance;
        } else {
            balance.setAmount(updatedAmount);
            repo.updateBalanceAmount(updatedAmount, balanceId);
        }
        // same concern here
        return balance;
    }

    public Balance getBalanceByBalanceId(Long balanceId) {
        return repo.getAccountBalanceByBalanceId(balanceId);
    }
}

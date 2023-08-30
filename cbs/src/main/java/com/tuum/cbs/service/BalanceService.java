package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.BalanceNotFoundException;
import com.tuum.cbs.common.exceptions.InsufficientFundsException;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.CbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BalanceService {

    private final CbsRepository repo;
    private final RabbitMQDESender mqDeSender;

    // business logic for balances
    public List<Balance> getBalanceByAccountId(UUID accId) {
        return repo.getAccountBalance(accId);
    }

    public Balance getBalanceByAccountId(UUID accountId, Currency currency) {
        Optional<Balance> optionalBalance = repo.getAccountBalanceByIdAndCurrency(accountId, currency.name());
        if (optionalBalance.isEmpty()) throw new BalanceNotFoundException("Balance for account with id - " + accountId + " not found!");
        return optionalBalance.get();
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
            // notify consumers
            mqDeSender.publishToUpdateBalanceQueue(balance.toString());
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
            // notify consumers
            mqDeSender.publishToUpdateBalanceQueue(balance.toString());
        }

        return balance;
    }

    //Trx layer calls this
    public Balance updateBalanceByAccountId(UUID accountId, Currency currency, BigDecimal amount){
        // get init balance
        Balance balance = getBalanceByAccountId(accountId, currency);
        // do addition {always because sign changes in trx service already}
        BigDecimal updatedAmount = balance.getAmount().add(amount);

        // insufficient funds is only valid for outgoing trx at this point
        if (updatedAmount.compareTo(BigDecimal.ZERO) < 0 &&
                amount.compareTo(BigDecimal.ZERO) < 0){
            throw new InsufficientFundsException("Insufficient funds: account balance shouldn't goto negative");
        }

        balance.setAmount(updatedAmount);
        repo.updateBalanceAmount(updatedAmount, balance.getBalanceId());
        // notify consumers
        mqDeSender.publishToUpdateBalanceQueue(balance.toString());

        return balance;
    }

    public Balance getBalanceByBalanceId(Long balanceId) {
        Optional<Balance> optionalBalance = repo.getAccountBalanceByBalanceId(balanceId);
        if (optionalBalance.isEmpty()) throw new BalanceNotFoundException("Balance with id - " + balanceId + " not found!");
        return optionalBalance.get();
    }
}

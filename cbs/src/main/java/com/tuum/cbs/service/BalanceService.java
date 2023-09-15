package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.BalanceNotFoundException;
import com.tuum.cbs.common.exceptions.InsufficientFundsException;
import com.tuum.cbs.models.Balance;
import com.tuum.cbs.models.Currency;
import com.tuum.cbs.repositories.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BalanceService {

    private final AccountsRepository repo;
    private final RabbitMQDESender mqDeSender;

    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "BalanceService";

    // business logic for balances
    public List<Balance> getBalanceByAccountId(UUID accId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balances input: " + accId);
        List<Balance> balances = repo.getAccountBalance(accId);
        if (balances.isEmpty()) throw new BalanceNotFoundException("Balances not found for this account!");
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got balances for: " + accId);
        return balances;
    }

    public Balance getBalanceByAccountId(UUID accountId, Currency currency) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balance input: " + accountId + " " + currency);
        Optional<Balance> optionalBalance = repo.getAccountBalanceByIdAndCurrency(accountId, currency.name());
        if (optionalBalance.isEmpty()) throw new BalanceNotFoundException("Balance for account with id - " + accountId + " not found!");
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got " + currency + " balance for: " + accountId);
        return optionalBalance.get();
    }

    public Balance updateBalanceObj(Balance balance){
        Long balanceId = balance.getBalanceId();
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " update balanceObj for balance id: " + balanceId);
        Balance initBalance = getBalanceByBalanceId(balanceId);
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
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " updated balanceObj for balance id: " + balanceId);
        return balance;
    }

    public Balance updateBalance(Long balanceId, BigDecimal amount){
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " update balance for balance id: " + balanceId);
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
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " updated balance for balance id: " + balanceId);
        return balance;
    }

    //Trx layer calls this
    public Balance updateBalanceByAccountId(UUID accountId, Currency currency, BigDecimal amount){
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " update " + currency + " balance for account with id: " + accountId);
        Balance balance = getBalanceByAccountId(accountId, currency);
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
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " updated " + currency + " balance for account with id: " + accountId);
        return balance;
    }

    public Balance getBalanceByBalanceId(Long balanceId) {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " get balance for balance id: " + balanceId);
        Optional<Balance> optionalBalance = repo.getAccountBalanceByBalanceId(balanceId);
        if (optionalBalance.isEmpty()) throw new BalanceNotFoundException("Balance with id - " + balanceId + " not found!");
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " got balance for: " + balanceId);
        return optionalBalance.get();
    }
}

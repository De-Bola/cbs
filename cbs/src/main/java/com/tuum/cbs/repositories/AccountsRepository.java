package com.tuum.cbs.repositories;

import com.tuum.cbs.common.handlers.UuidTypeHandler;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.Balance;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
@Repository
public interface AccountsRepository {

    @Insert("INSERT INTO accounts (account_id, country, customer_id) " +
            " VALUES (#{accountId, typeHandler = com.tuum.cbs.common.handlers.UuidTypeHandler}, #{country}, #{customerId})")
    @Options(keyProperty = "accountId", keyColumn = "account_id")
    int insertAccount(Account account);

    // handling inserts and get operations for account and balances separately
    @Select("SELECT * FROM accounts WHERE account_id = #{accountId}")
    @Results(id = "accountResultMap", value = {
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "accountId", column = "account_id", jdbcType = JdbcType.OTHER, typeHandler = UuidTypeHandler.class),
            @Result(property = "country", column = "country"),
    })
    Optional<Account> getAccountById(@Param("accountId") UUID accountId);


    /**
     * Gets account balance list using accountId
     * */
    @Select("SELECT * FROM balances WHERE account_id = #{accountId}")
    @Results(id = "balanceResultMap", value = {
            @Result(property = "balanceId", column = "balance_id"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "accountId", column = "account_id", jdbcType = JdbcType.OTHER, typeHandler = UuidTypeHandler.class),
            @Result(property = "currency", column = "currency")
    })
    List<Balance> getAccountBalance(@Param("accountId") UUID accountId);

    @Insert("INSERT INTO balances (balance_id, amount, currency, account_id)" +
            "VALUES (#{balanceId}, #{amount},#{currency},#{accountId, typeHandler = com.tuum.cbs.common.handlers.UuidTypeHandler})")
    int insertBalance(Balance balance);

    @Insert({
            "<script>",
            "INSERT INTO balances (balance_id, amount, currency, account_id)",
            "VALUES",
            "<foreach collection = 'list' item='Balance' separator = ','>",
            "( #{Balance.balanceId}, #{Balance.amount}, #{Balance.currency}, #{Balance.accountId, typeHandler = com.tuum.cbs.common.handlers.UuidTypeHandler} )",
            "</foreach>",
            "</script>"
    })
    int insertBalances(@Param("list") List<Balance> balanceList);

    @Update("UPDATE balances SET amount = #{amount} WHERE balance_id = #{balanceId}")
    int updateBalanceObj(Balance balance);

    @Update("UPDATE balances SET amount = #{amount} WHERE balance_id = #{balanceId}")
    int updateBalanceAmount(@Param("amount") BigDecimal amount, @Param("balanceId") Long balanceId);

    /**
     * Gets account balance using accountId
     * */
    @Select("SELECT balance_id, amount, currency, account_id FROM balances WHERE account_id = #{accountId} and currency = #{currency}")
    @ResultMap("balanceResultMap")
    Optional<Balance> getAccountBalanceByIdAndCurrency(@Param("accountId") UUID accountId, @Param("currency") String currency);

    /**
     * Gets account balance using accountId
     * */
    @Select("SELECT balance_id, amount, currency, account_id FROM balances WHERE balance_id = #{balanceId}")
    @ResultMap("balanceResultMap")
    Optional<Balance> getAccountBalanceByBalanceId(Long balanceId);

}

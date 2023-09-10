package com.tuum.cbs.repositories;

import com.tuum.cbs.common.handlers.UuidTypeHandler;
import com.tuum.cbs.models.Transaction;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Mapper
public interface TransactionsRepository {

    @Insert("INSERT INTO transactions (trx_id, amount, currency, trx_type, description, balanceAfterTrx, account_id)" +
            "VALUES (#{trxId}, #{amount}, #{currency}, #{trxType}, #{description}, #{balanceAfterTrx}, " +
            "#{accountId, typeHandler = com.tuum.cbs.common.handlers.UuidTypeHandler})")
    int insertTransaction(Transaction transaction);

    /**
     * Gets account balance list using accountId
     * */
    @Select("SELECT * FROM transactions WHERE account_id = #{accountId}")
    @Results(id = "transactionResultMap", value = {
            @Result(property = "trxId", column = "trx_id"),
            @Result(property = "trxType", column = "trx_type"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "balanceAfterTrx", column = "balanceAfterTrx"),
            @Result(property = "accountId", column = "account_id", jdbcType = JdbcType.OTHER, typeHandler = UuidTypeHandler.class),
            @Result(property = "currency", column = "currency"),
            @Result(property = "description", column = "description")
    })
    List<Transaction> getTrxByAccountId(@Param("accountId") UUID accountId);

}

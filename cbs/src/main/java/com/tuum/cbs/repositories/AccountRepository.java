package com.tuum.cbs.repositories;

import com.tuum.cbs.models.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AccountRepository {

    @Insert("INSERT INTO a_b(accountId, customerId, description, page, price) " +
            " VALUES (#{title}, #{isbn}, #{description}, #{page}, #{price})")
    void insert(Account account);
}

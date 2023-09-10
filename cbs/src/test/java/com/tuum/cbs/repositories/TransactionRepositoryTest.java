package com.tuum.cbs.repositories;

import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@MybatisTest
@RunWith(SpringRunner.class)
@Sql({"/templates/transactions.sql"})
public class TransactionRepositoryTest {
}

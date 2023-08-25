package com.tuum.cbs.service;

import com.tuum.cbs.common.exceptions.AccountAlreadyException;
import com.tuum.cbs.models.Account;
import com.tuum.cbs.models.AccountDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {
//        AccountService.class
//})
@SpringJUnitConfig(classes = {AccountService.class})
class AccountServiceTest {

    @MockBean
    AccountService uut;

    @Test
    void checkContextStarts() {
        assertThat(uut).isNotNull();
    }

    @Test
    void saveShouldReturnNewAccount() {

    }

    @Test
    void saveShouldThrowExceptionWhenUserAlreadyExists() {
        final AccountDao testAccount = AccountDao.builder().build();
        assertThatThrownBy(() -> {
            uut.save(testAccount);
        }).isInstanceOf(AccountAlreadyException.class);
    }
}
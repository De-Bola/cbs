package com.tuum.cbs.common.handlers;

import com.tuum.cbs.models.Balance;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

@RequiredArgsConstructor
public class BalanceResultHandler implements ResultHandler<Balance> {

    private Balance balance;


    @Override
    public void handleResult(ResultContext<? extends Balance> resultContext) {
        this.balance = resultContext.getResultObject();
    }

    Balance getBalance(){
        return this.balance;
    }
}

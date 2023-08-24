package com.tuum.cbs.models;

import lombok.*;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    private Long accountId;
    @Generated
    private String customerId = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
    private List<Balance> balanceList;

//    public void setAccountId() {
//        this.accountId = ThreadLocalRandom.current().nextLong(1000000L, 9999999L);
//    }
}

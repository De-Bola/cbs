package com.tuum.cbs.models;

import lombok.*;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    private Long accountId;
    private String customerId;
    private List<Balance> balanceList;

}

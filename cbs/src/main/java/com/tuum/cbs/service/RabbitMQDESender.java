package com.tuum.cbs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQDESender {

    @Value("${trx.exchange.name}")
    String trxDirectExchange;

    @Value("${balances.exchange.name}")
    String balDirectExchange;

    @Value("${trx.credit.routingKey}")
    String trxRoutingKey1;

    @Value("${trx.debit.routingKey}")
    String trxRoutingKey2;

    @Value("${balances.create.routingKey}")
    String balancesRoutingKey1;

    @Value("${balances.update.routingKey}")
    String balancesRoutingKey2;

    private final RabbitTemplate rabbitTemplate;
    private static final String PREFIX = "Message sent to consumer: ";
    private static final String CLASS_NAME = "RabbitMQDESender";
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();

    public void publishToTrxCreditQueue(String message) {
        rabbitTemplate.convertAndSend(trxDirectExchange, trxRoutingKey1, message);
        LOGGER.info("[" + TIMESTAMP + "]: "+ CLASS_NAME + " " + PREFIX + " " + message);
    }

    public void publishToTrxDebitQueue(String message) {
        rabbitTemplate.convertAndSend(trxDirectExchange, trxRoutingKey2, message);
        LOGGER.info("[" + TIMESTAMP + "]: "+ CLASS_NAME + " " + PREFIX + " " + message);
    }

    public void publishToCreateBalanceQueue(String message) {
        rabbitTemplate.convertAndSend(balDirectExchange, balancesRoutingKey1, message);
        LOGGER.info("[" + TIMESTAMP + "]: "+ CLASS_NAME + " " + PREFIX + " " + message);
    }

    public void publishToUpdateBalanceQueue(String message) {
        rabbitTemplate.convertAndSend(balDirectExchange, balancesRoutingKey2, message);
        LOGGER.info("[" + TIMESTAMP + "]: "+ CLASS_NAME + " " + PREFIX + " " + message);
    }
}

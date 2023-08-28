package com.tuum.cbs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public void publishToTrxCreditQueue(String message) {
        String prefix = "Message sent to consumer: ";
        rabbitTemplate.convertAndSend(trxDirectExchange, trxRoutingKey1, message);
        LOGGER.info(prefix + " " + message);
    }

    public void publishToTrxDebitQueue(String message) {
        String prefix = "Message sent to consumer: ";
        rabbitTemplate.convertAndSend(trxDirectExchange, trxRoutingKey2, message);
        LOGGER.info(prefix + " " + message);
    }

    public void publishToCreateBalanceQueue(String message) {
        String prefix = "Message sent to consumer: ";
        rabbitTemplate.convertAndSend(balDirectExchange, balancesRoutingKey1, message);
        LOGGER.info(prefix + " " + message);
    }

    public void publishToUpdateBalanceQueue(String message) {
        String prefix = "Message sent to consumer: ";
        rabbitTemplate.convertAndSend(balDirectExchange, balancesRoutingKey2, message);
        LOGGER.info(prefix + " " + message);
    }
}

package com.tuum.cbs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQFOSender {

    @Value("${accounts.one.queue.name}")
    String accountQueue1;

    @Value("${accounts.two.queue.name}")
    String accountQueue2;

    @Value("${accounts.exchange.name}")
    String fanoutExchange;

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQFOSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToFanoutXchange(String message){
        String prefix = "Message sent to consumer: ";
        rabbitTemplate.convertAndSend(fanoutExchange, "", message);
        LOGGER.info(prefix + " " + message);
    }
}

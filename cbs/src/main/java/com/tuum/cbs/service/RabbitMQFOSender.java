package com.tuum.cbs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    private static final String PREFIX = "Message sent to consumer: ";
    private static final String CLASS_NAME = "RabbitMQFOSender";
    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();

    public RabbitMQFOSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToFanoutXchange(String message){
        rabbitTemplate.convertAndSend(fanoutExchange, "", message);
        LOGGER.info("[" + TIMESTAMP + "]: "+ CLASS_NAME + " " + PREFIX + " " + message);
    }
}

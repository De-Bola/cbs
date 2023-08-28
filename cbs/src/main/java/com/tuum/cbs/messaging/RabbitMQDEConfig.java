package com.tuum.cbs.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQDEConfig {

    @Value("${trx.credit.queue.name}")
    String trxCreditQueue;

    @Value("${trx.debit.queue.name}")
    String trxDebitQueue;

    @Value("${balances.create.queue.name}")
    String createBalanceQueue;

    @Value("${balances.update.queue.name}")
    String updateBalanceQueue;

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

    @Bean
    Queue creditQueue(){
        return new Queue(trxCreditQueue, true);
    }

    @Bean
    Queue debitQueue(){
        return new Queue(trxDebitQueue, true);
    }

    @Bean
    Queue createBalanceQueue(){
        return new Queue(createBalanceQueue, true);
    }

    @Bean
    Queue updateBalanceQueue(){
        return new Queue(updateBalanceQueue, true);
    }

    @Bean
    DirectExchange trxDirectExchange(){
        return new DirectExchange(trxDirectExchange);
    }

    @Bean
    DirectExchange balDirectExchange(){
        return new DirectExchange(balDirectExchange);
    }

    @Bean
    Binding createBalanceBinding(){
        return BindingBuilder.bind(createBalanceQueue()).to(balDirectExchange()).with(balancesRoutingKey1);
    }

    @Bean
    Binding updateBalanceBinding(){
        return BindingBuilder.bind(updateBalanceQueue()).to(balDirectExchange()).with(balancesRoutingKey2);
    }

    @Bean
    Binding creditBinding(){
        return BindingBuilder.bind(creditQueue()).to(trxDirectExchange()).with(trxRoutingKey1);
    }

    @Bean
    Binding debitBinding(){
        return BindingBuilder.bind(debitQueue()).to(trxDirectExchange()).with(trxRoutingKey2);
    }

    @Bean
    public MessageConverter dXchangeMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate dXchangeRabbitTemplate(ConnectionFactory connectionFactory){
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(dXchangeMessageConverter());
        return template;
    }

}

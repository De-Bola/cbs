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
public class RabbitMQFOConfig {

    @Value("${accounts.one.queue.name}")
    String accountQueue1;

    @Value("${accounts.two.queue.name}")
    String accountQueue2;

    @Value("${accounts.exchange.name}")
    String accountFanoutExchange;

    @Bean
    Queue accountQueue1(){
        return new Queue(accountQueue1, true);
    }

    @Bean
    Queue accountQueue2(){
        return new Queue(accountQueue2, true);
    }

    @Bean
    FanoutExchange accountFanoutExchange(){
        return new FanoutExchange(accountFanoutExchange);
    }

    @Bean
    Binding accountBinding1(){
        return BindingBuilder.bind(accountQueue1()).to(accountFanoutExchange());
    }

    @Bean
    Binding accountBinding2(){
        return BindingBuilder.bind(accountQueue2()).to(accountFanoutExchange());
    }

    @Bean
    public MessageConverter fanoutMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate fanoutRabbitTemplate(ConnectionFactory connectionFactory){
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(fanoutMessageConverter());
        return template;
    }

}


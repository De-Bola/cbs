package com.tuum.cbs.configuration.messaging;

//@Configuration
////@ConditionalOnClass(EnableRabbit.class)
//@EnableRabbit
//public class RabbitConfig {
//
//    // add logger here
//
//    @Bean
//    public ConnectionFactory connectionFactory(){
//        return new ConnectionFactory();
//    }
//
//    @Autowired
//    protected RabbitTemplate rabbitTemplate;
//
//    @Bean
//    public RabbitTransactionManager rabbitTransactionManager() {
//        return new RabbitTransactionManager(connectionFactory());
//    }
//
//    @Bean
//    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//        return new RabbitAdmin((org.springframework.amqp.rabbit.connection.ConnectionFactory) connectionFactory);
//    }
//
//    @PostConstruct
//    protected void init() {
//        // make rabbit template to support transactions
//        rabbitTemplate.setChannelTransacted(true);
//    }
//
//}

package com.app.AddressBook_Workshop.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue userQueue() {
        return new Queue("UserQueue", true);
    }


    @Bean
    public Queue contactQueue() {
        return new Queue("ContactQueue", true);
    }


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("AddressBookExchange");
    }


    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with("userKey");
    }


    @Bean
    public Binding contactBinding(Queue contactQueue, TopicExchange exchange) {
        return BindingBuilder.bind(contactQueue).to(exchange).with("contactKey");
    }
}
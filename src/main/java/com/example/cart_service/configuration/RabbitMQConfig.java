package com.example.cart_service.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ===== Product -> Cart =====
    public static final String PRODUCT_EXCHANGE = "product_exchange";
    public static final String PRODUCT_QUEUE = "product_cart_queue";

    // ===== Order -> Cart =====
    public static final String ORDER_EXCHANGE = "order_exchange";
    public static final String ORDER_QUEUE = "order_cart_queue";

    /* ================= PRODUCT -> CART ================= */

    @Bean
    public FanoutExchange productFanoutExchange() {
        return new FanoutExchange(PRODUCT_EXCHANGE);
    }

    @Bean
    public Queue productQueue() {
        return QueueBuilder.durable(PRODUCT_QUEUE).build();
    }

    @Bean
    public Binding bindProductQueue(
            Queue productQueue,
            FanoutExchange productFanoutExchange
    ) {
        return BindingBuilder
                .bind(productQueue)
                .to(productFanoutExchange);
    }

    /* ================= ORDER -> CART ================= */

    @Bean
    public FanoutExchange orderFanoutExchange() {
        return new FanoutExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderCartQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }

    @Bean
    public Binding bindOrderCartQueue(
            Queue orderCartQueue,
            FanoutExchange orderFanoutExchange
    ) {
        return BindingBuilder
                .bind(orderCartQueue)
                .to(orderFanoutExchange);
    }

    /* ================= COMMON ================= */

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
}




package com.ems.notification_service.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";

    public static final String EMPLOYEE_EMAIL_NOTIFICATION_QUEUE = "employee-email-notification-queue";

    public static final String EMPLOYEE_NOTIFICATION_ROUTING_KEY = "employee.email.notification";

    @Bean
    public Queue signupEmailQueue() {
        return new Queue(EMPLOYEE_EMAIL_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding employeeNotificationBindings(Queue signupEmailQueue, DirectExchange exchange) {
        return BindingBuilder.bind(signupEmailQueue).to(exchange).with(EMPLOYEE_NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
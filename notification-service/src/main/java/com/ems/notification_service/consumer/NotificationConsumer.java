package com.ems.notification_service.consumer;

import com.ems.notification_service.config.rabbitmq.RabbitMQConfig;
import com.ems.notification_service.service.MailService;
import com.ems.notification_service.utils.helpers.Helpers;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final MailService mailService;

    @RabbitListener(queues = RabbitMQConfig.EMPLOYEE_EMAIL_NOTIFICATION_QUEUE, ackMode = "MANUAL")
    public void consumeEmployeeEmailNotificationQueue(
            Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("\n📩 Employee Email Queue received");

            Map<String, Object> msg = Helpers.deserializeMessageBody(message.getBody());
            mailService.employeeSignInPasswordMail(
                    (String) msg.get("email"), (String) msg.get("firstName"), (String) msg.get("password"));

            channel.basicAck(deliveryTag, false);

            log.info("\n✅ Employee email message acknowledged");
        } catch (Exception e) {
            log.info("\n❌ Processing failed: {} ", e.getMessage());
            channel.basicNack(deliveryTag, false, true);
        }
    }
}

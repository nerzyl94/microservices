package com.nerzyl.notification.rabbitmq;

import com.nerzyl.clients.notification.NotificationRequest;
import com.nerzyl.notification.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class NotificationConsumer {

    private NotificationService notificationService;

    @RabbitListener(queues = "notification.queue")
    public void consumer(NotificationRequest request) {
        log.info("Consume {} from queue", request);
        notificationService.send(request);
    }
}

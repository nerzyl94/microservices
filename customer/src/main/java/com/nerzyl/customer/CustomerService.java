package com.nerzyl.customer;

import com.nerzyl.amqp.RabbitMQMessageProducer;
import com.nerzyl.clients.fraud.FraudCheckResponse;
import com.nerzyl.clients.fraud.FraudClient;
import com.nerzyl.clients.notification.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
public record CustomerService(
        CustomerRepository repository,
        FraudClient fraudClient,
        RabbitMQMessageProducer messageProducer
) {

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        customer = repository.saveAndFlush(customer);

        FraudCheckResponse response = fraudClient.isFraudster(customer.getId());
        if (response.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to our service...",
                        customer.getFirstName())
        );

        messageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }
}

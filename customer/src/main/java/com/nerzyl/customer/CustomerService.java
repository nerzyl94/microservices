package com.nerzyl.customer;

import com.nerzyl.clients.fraud.FraudCheckResponse;
import com.nerzyl.clients.fraud.FraudClient;
import com.nerzyl.clients.notification.NotificationClient;
import com.nerzyl.clients.notification.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public record CustomerService(CustomerRepository repository, NotificationClient notificationClient, FraudClient fraudClient) {

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

        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome to our service...",
                                customer.getFirstName())
                )
        );
    }
}

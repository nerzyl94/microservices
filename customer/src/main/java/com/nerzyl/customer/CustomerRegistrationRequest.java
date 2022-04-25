package com.nerzyl.customer;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email) {
}

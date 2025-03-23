package com.app.AddressBook_Workshop.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListener {

    @RabbitListener(queues = "UserQueue")
    public void handleUserRegistration(String email) {
        System.out.println("📧 Sending registration email to: " + email);

    }

    @RabbitListener(queues = "ContactQueue")
    public void handleNewContact(String email) {
        System.out.println("📞 New contact added: " + email);

    }
}
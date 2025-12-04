package com.library.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class EmailServiceTest {

    @Test
    void sendEmail_invokesTransportSendSuccessfully() {
        EmailService service = new EmailService("sender@example.com", "password");

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            assertDoesNotThrow(() -> service.sendEmail("to@example.com", "Subject", "Body"));
            transportMock.verify(() -> Transport.send(any(Message.class)));
        }
    }

    @Test
    void sendEmail_whenTransportFails_wrapsMessagingException() {
        EmailService service = new EmailService("sender@example.com", "password");

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            transportMock.when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new MessagingException("SMTP error"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.sendEmail("to@example.com", "Subject", "Body"));
            assertTrue(ex.getCause() instanceof MessagingException);
        }
    }
}
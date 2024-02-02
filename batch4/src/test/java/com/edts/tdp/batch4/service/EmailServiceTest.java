package com.edts.tdp.batch4.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {EmailService.class})
@ExtendWith(SpringExtension.class)
class EmailServiceTest {
  @Autowired
  private EmailService emailService;

  @MockBean
  private JavaMailSender javaMailSender;

  /**
   * Method under test:
   * {@link EmailService#sendEmailToAdmin(String, String, String, StringWriter)}
   */
  @Test
  void testSendEmailToAdmin() throws MessagingException, MailException {
    // Arrange
    doNothing().when(javaMailSender).send(Mockito.<MimeMessage>any());
    when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

    // Act
    emailService.sendEmailToAdmin("jane.doe@example.org", "Hello from the Dreaming Spires",
            "Not all who wander are lost", new StringWriter());

    // Assert
    verify(javaMailSender).createMimeMessage();
    verify(javaMailSender).send(Mockito.<MimeMessage>any());
  }

  /**
   * Method under test:
   * {@link EmailService#sendEmailToCustomer(String, String, String)}
   */
  @Test
  void testSendEmailToCustomer() throws MessagingException, MailException {
    // Arrange
    doNothing().when(javaMailSender).send(Mockito.<MimeMessage>any());
    when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

    // Act
    emailService.sendEmailToCustomer("jane.doe@example.org", "Hello from the Dreaming Spires",
            "Not all who wander are lost");

    // Assert
    verify(javaMailSender).createMimeMessage();
    verify(javaMailSender).send(Mockito.<MimeMessage>any());
  }
}

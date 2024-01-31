package com.edts.tdp.batch4.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body, StringWriter csvData) throws MessagingException {
        jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body);

        // Attach the CSV data as an attachment
        helper.addAttachment("report.csv", new InputStreamSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return toInputStream(csvData.toString());
            }

            public String getFilename() {
                return "report.csv";
            }

            private InputStream toInputStream(String content) {
                return new InputStream() {
                    private byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                    private int currentIndex = 0;

                    @Override
                    public int read() throws IOException {
                        if (currentIndex < bytes.length) {
                            return bytes[currentIndex++];
                        } else {
                            return -1;
                        }
                    }
                };
            }
        });

        mailSender.send(message);
    }
}

package net.youssfi.authservice.security.service;

public interface MailService {
    void sendEmail(String emailDestination, String subject, String content);
}

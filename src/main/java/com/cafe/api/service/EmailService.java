package com.cafe.api.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}

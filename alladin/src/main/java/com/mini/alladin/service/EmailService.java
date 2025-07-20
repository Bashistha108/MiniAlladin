package com.mini.alladin.service;

public interface EmailService {
    void sendUnblockEmail(String to, String unblockLink);
}

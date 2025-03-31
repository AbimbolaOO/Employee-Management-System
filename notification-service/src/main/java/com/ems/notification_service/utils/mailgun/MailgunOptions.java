package com.ems.notification_service.utils.mailgun;

public record MailgunOptions(String username, String key, String sandboxUri, String senderEmail, String baseUrl) {
}

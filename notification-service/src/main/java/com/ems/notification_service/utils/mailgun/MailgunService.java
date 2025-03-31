package com.ems.notification_service.utils.mailgun;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class MailgunService {
    private final MailgunOptions mailgunOptions;
    private final RestTemplate restTemplate;

    @Autowired
    public MailgunService(MailgunOptions mailgunOptions, @Qualifier("mailgunRestTemplate") RestTemplate restTemplate) {
        this.mailgunOptions = mailgunOptions;
        this.restTemplate = restTemplate;
    }

    /**
     * Sends an email via Mailgun.
     *
     * @param mailFormat Map containing email details (to, subject, html, etc.).
     */
    public void sendMail(Map<String, String> mailFormat) {
        try {
            log.info("\n⚡️Preparing to send email to: {}", mailFormat.get("to"));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBasicAuth("api", mailgunOptions.key());

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("from", "Super-Company Inc <" + mailgunOptions.senderEmail() + ">");
            body.add("to", mailFormat.get("to"));
            body.add("subject", mailFormat.get("subject"));
            body.add("html", mailFormat.get("html"));


            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            String url = mailgunOptions.baseUrl() + "/v3/" + mailgunOptions.sandboxUri() + "/messages";

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            response.getBody();
        } catch (Exception e) {
            log.error("\n❌ MAILGUN SERVICE ERROR: {}", e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
package com.ems.notification_service.utils.mailgun;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Setter
@Configuration
@ConfigurationProperties(prefix = "mailgun")
public class MailgunConfig {
    private String username;
    private String key;
    private String sandboxUri;
    private String senderEmail;
    private String baseUrl;

    @Bean
    public MailgunOptions mailgunOptions() {
        return new MailgunOptions(username, key, sandboxUri, senderEmail, baseUrl);
    }

    @Bean
    public RestTemplate mailgunRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        return new RestTemplate(factory);
    }
}

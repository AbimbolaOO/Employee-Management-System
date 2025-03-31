package com.ems.notification_service.service;

import com.ems.notification_service.utils.mailgun.MailgunService;
import com.ems.notification_service.utils.template.EmailTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {
    private final MailgunService mailgunService;

    public void employeeSignInPasswordMail(String email, String firstName, String password) {
        Map<String, String> mail = new HashMap<>();
        mail.put("to", email);
        mail.put("subject", "Super-Company - Employee signing password");
        mail.put("html", EmailTemplates.employeeSignInPasswordMailTemplate(firstName, password));

        mailgunService.sendMail(mail);
    }
}

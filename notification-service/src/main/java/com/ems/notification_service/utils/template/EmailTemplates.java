package com.ems.notification_service.utils.template;

public class EmailTemplates {

    private EmailTemplates() {
    }

    public static String employeeSignInPasswordMailTemplate(String firstName, String password) {
//        System.out.println("password--->>> " + password);
        return String.format(
                "<html>" +
                        "<body>" +
                        "<p>Hi %s,</p>" +
                        "<p>Welcome to Super-Company.</p>" +
                        "<p>Here is your account sign-in password below:</p>" +
                        "<p><strong>%s</strong></p>" +
                        "<p>The Super-Company Team</p>" +
                        "</body>" +
                        "</html>",
                firstName, password
        );
    }

}

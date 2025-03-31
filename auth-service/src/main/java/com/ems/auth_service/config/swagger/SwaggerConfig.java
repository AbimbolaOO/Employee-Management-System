package com.ems.auth_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Authentication Service API")
                        .version("1.0")
                        .description("API documentation for authentication service")
                        .contact(new Contact()
                                .name("Abimbola Olayemi")
                                .email("abimbolaolayemiwhyte@gmail.com")
                                .url("https://abimbolaolayemi.com")));
    }
}

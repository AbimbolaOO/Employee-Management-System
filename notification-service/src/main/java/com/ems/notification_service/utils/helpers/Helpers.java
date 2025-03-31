package com.ems.notification_service.utils.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class Helpers {

    private Helpers() {
    }

    public static Map<String, Object> deserializeMessageBody(byte[] body) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(body, new TypeReference<>() {
            });
            if (map == null) {
                log.info("No message body found");
            }
            return map;
        } catch (IOException e) {
            log.error("Error deserializing message body: {}", e.getMessage());
            return Map.of();
        }
    }
}

package com.rajasreeit.backend.service.crmEmployeeServices;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    public void sendPushNotification(String expoPushToken, String message) {
        String expoUrl = "https://exp.host/--/api/v2/push/send";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> notificationBody = Map.of(
                "to", expoPushToken,
                "title", "Punch Activity Recorded",
                "body", message
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(notificationBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                expoUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send notification: " + response.getBody());
        }
    }
}

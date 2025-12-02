package com.tecnoponto.googleSheets.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeepAliveScheduler {

    @Value("${app.keepalive.url:}")
    private String keepAliveUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void pingSelf() {
        if (keepAliveUrl == null || keepAliveUrl.isEmpty()) {
            System.out.println("Keep-alive URL not configured. Skipping ping.");
            return;
        }

        try {
            restTemplate.getForObject(keepAliveUrl, String.class);
            System.out.println("Pinged " + keepAliveUrl + " successfully.");
        } catch (Exception e) {
            System.err.println("Failed to ping " + keepAliveUrl + ": " + e.getMessage());
        }
    }
}

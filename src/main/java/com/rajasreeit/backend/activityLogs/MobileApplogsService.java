package com.rajasreeit.backend.activityLogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class MobileApplogsService {

    @Autowired
    private MobileApplogsRepo logRepository;

    public void logActivity(String apiUrl, String method, String mobileNumber, String message) {
        MobileApplogs log = new MobileApplogs();
        log.setApiUrl(apiUrl);
        log.setRequestMethod(method);
        log.setMobileNumber(mobileNumber);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }
}

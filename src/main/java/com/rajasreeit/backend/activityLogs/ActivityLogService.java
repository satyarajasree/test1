package com.rajasreeit.backend.activityLogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogsRepo logRepository;

    public void logActivity(String apiUrl, String method, String mobileNumber, String message) {
        ActivityLogs log = new ActivityLogs();
        log.setApiUrl(apiUrl);
        log.setRequestMethod(method);
        log.setMobileNumber(mobileNumber);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }
}
